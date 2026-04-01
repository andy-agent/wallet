#!/usr/bin/env python3
"""
手动触发订单履行脚本 - 用于异常订单处理

用法:
    python scripts/manual_fulfill.py --order-id <order_id>
    python scripts/manual_fulfill.py --order-id <order_id> --force

环境变量:
    DATABASE_URL: 数据库连接字符串 (必需)
    MARZBAN_BASE_URL: Marzban API 地址
    MARZBAN_ADMIN_USERNAME: Marzban 管理员用户名
    MARZBAN_ADMIN_PASSWORD: Marzban 管理员密码
"""

import argparse
import asyncio
import os
import sys
from datetime import datetime, timezone

# 添加项目路径
sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

from sqlalchemy import select

from app.core.database import init_db, get_db_context
from app.core.state_machine import OrderStatus, transition_to_fulfilled
from app.models.order import Order
from app.models.plan import Plan
from app.services.fulfillment import (
    fulfill_new_order, 
    fulfill_renew_order, 
    FulfillmentError
)


def parse_args() -> argparse.Namespace:
    """解析命令行参数"""
    parser = argparse.ArgumentParser(
        prog="manual_fulfill",
        description="手动触发订单履行流程 (用于异常订单处理)",
        formatter_class=argparse.RawDescriptionHelpFormatter,
        epilog="""
示例:
  # 手动履行订单
  python scripts/manual_fulfill.py --order-id 01HQ1234567890ABCDEF123456

  # 强制履行 (跳过状态检查，谨慎使用)
  python scripts/manual_fulfill.py --order-id <order_id> --force

  # 显示帮助
  python scripts/manual_fulfill.py --help

注意事项:
  - 只有状态为 'paid_success' 的订单才能正常履行
  - --force 选项会尝试跳过状态检查，但仍可能因其他原因失败
  - 已履行的订单会返回已存在的履行结果 (幂等性)
        """
    )
    parser.add_argument(
        "--order-id", "-o",
        required=True,
        help="订单ID"
    )
    parser.add_argument(
        "--force", "-f",
        action="store_true",
        help="强制履行 (跳过状态检查，谨慎使用)"
    )
    parser.add_argument(
        "--token", "-t",
        help="续费订单的 access token (renew 类型订单需要)"
    )
    parser.add_argument(
        "--dry-run", "-d",
        action="store_true",
        help="试运行模式，检查但不执行"
    )
    return parser.parse_args()


async def get_order_with_plan(session, order_id: str):
    """获取订单及关联套餐"""
    result = await session.execute(
        select(Order, Plan)
        .join(Plan, Order.plan_id == Plan.id)
        .where(Order.id == order_id)
    )
    return result.first()


async def check_order_can_fulfill(order: Order, force: bool = False) -> tuple[bool, str]:
    """
    检查订单是否可以履行
    
    Returns:
        (是否可以履行, 原因)
    """
    # 检查订单是否已履行
    if order.status == OrderStatus.FULFILLED.value:
        return True, "订单已履行，将返回现有结果"
    
    # 检查订单状态
    if order.status == OrderStatus.PAID_SUCCESS.value:
        return True, "订单状态正常，可以履行"
    
    # 错误状态检查
    error_states = [
        OrderStatus.EXPIRED.value,
        OrderStatus.UNDERPAID.value,
        OrderStatus.OVERPAID.value,
        OrderStatus.FAILED.value,
        OrderStatus.LATE_PAID.value,
    ]
    
    if order.status in error_states:
        if force:
            return True, f"警告: 订单处于错误状态 '{order.status}'，强制履行"
        else:
            return False, f"错误: 订单处于错误状态 '{order.status}'，无法履行 (使用 --force 强制履行)"
    
    # 待支付状态
    if order.status == OrderStatus.PENDING_PAYMENT.value:
        return False, "错误: 订单尚未支付"
    
    # 确认中状态
    if order.status in [OrderStatus.SEEN_ONCHAIN.value, OrderStatus.CONFIRMING.value]:
        return False, "错误: 订单正在确认中，请等待确认完成"
    
    return False, f"错误: 未知订单状态 '{order.status}'"


async def main():
    """主函数"""
    args = parse_args()
    
    # 初始化数据库
    print("🔌 连接数据库...")
    init_db()
    
    async with get_db_context() as session:
        # 查询订单
        row = await get_order_with_plan(session, args.order_id)
        
        if not row:
            print(f"❌ 订单未找到: {args.order_id}")
            sys.exit(1)
        
        order = row.Order
        plan = row.Plan
        
        print(f"\n📋 订单信息:")
        print(f"  订单ID:     {order.id}")
        print(f"  订单号:     {order.order_no}")
        print(f"  状态:       {order.status}")
        print(f"  购买类型:   {'新购' if order.purchase_type == 'new' else '续费'}")
        print(f"  套餐:       {plan.code} - {plan.name}")
        print(f"  链/资产:    {order.chain} / {order.asset_code}")
        print(f"  金额:       {order.amount_crypto} {order.asset_code}")
        
        # 检查是否可以履行
        can_fulfill, reason = await check_order_can_fulfill(order, args.force)
        print(f"\n🔍 检查结果: {reason}")
        
        if not can_fulfill:
            print("❌ 无法履行订单")
            sys.exit(1)
        
        if args.dry_run:
            print("\n📝 [DRY RUN] 试运行模式，不执行实际履行")
            sys.exit(0)
        
        # 确认提示
        if args.force and order.status != OrderStatus.FULFILLED.value:
            print("\n⚠️  警告: 您正在强制履行一个非正常状态的订单")
            print("   这可能会导致数据不一致或其他问题")
            # 在自动化脚本中不等待用户输入
            # response = input("   是否继续? [y/N]: ")
            # if response.lower() != 'y':
            #     print("已取消")
            #     sys.exit(0)
        
        # 执行履行
        print(f"\n🚀 开始履行订单...")
        
        try:
            if order.purchase_type == "new":
                # 新购订单
                result = await fulfill_new_order(order.id)
            else:
                # 续费订单
                if not args.token:
                    # 尝试从现有会话获取 token
                    from app.models.client_session import ClientSession
                    result_session = await session.execute(
                        select(ClientSession)
                        .where(ClientSession.marzban_username == order.marzban_username)
                        .where(ClientSession.revoked_at.is_(None))
                        .order_by(ClientSession.created_at.desc())
                    )
                    client_session = result_session.scalar_one_or_none()
                    
                    if client_session:
                        token = client_session.access_token
                        print(f"   使用现有会话 token: {token[:30]}...")
                    else:
                        print("❌ 错误: 续费订单需要提供 --token 参数")
                        print("   或者关联的 Marzban 用户没有有效的会话")
                        sys.exit(1)
                else:
                    token = args.token
                
                result = await fulfill_renew_order(order.id, token)
            
            # 履行成功
            print("\n" + "=" * 60)
            print("✅ 订单履行成功!")
            print("=" * 60)
            print(f"  Marzban 用户名: {result.marzban_username}")
            print(f"  订阅链接:       {result.subscription_url}")
            print(f"  Access Token:   {result.access_token[:50]}...")
            print(f"  Refresh Token:  {result.refresh_token[:50]}...")
            print(f"  过期时间:       {result.expires_at.isoformat()}")
            print("=" * 60)
            
        except FulfillmentError as e:
            print(f"\n❌ 履行失败:")
            print(f"   错误码: {e.error_code}")
            print(f"   错误信息: {e.error_message}")
            sys.exit(1)
        except Exception as e:
            print(f"\n❌ 履行异常: {str(e)}")
            import traceback
            print(traceback.format_exc())
            sys.exit(1)


if __name__ == "__main__":
    asyncio.run(main())
