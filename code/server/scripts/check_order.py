#!/usr/bin/env python3
"""
查询订单状态脚本 - 用于调试和订单查询

用法:
    python scripts/check_order.py --order-id <order_id>
    python scripts/check_order.py --order-id ORDXXXXXXXXXXXXXXXX

环境变量:
    DATABASE_URL: 数据库连接字符串 (必需)
"""

import argparse
import asyncio
import json
import os
import sys
from datetime import datetime
from typing import Any, Dict, Optional

# 添加项目路径
sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

from sqlalchemy import select
from sqlalchemy.orm import selectinload

from app.core.database import init_db, get_db_context
from app.models.order import Order
from app.models.plan import Plan
from app.models.payment_address import PaymentAddress
from app.models.client_session import ClientSession
from app.core.state_machine import OrderStatus, state_machine


def parse_args() -> argparse.Namespace:
    """解析命令行参数"""
    parser = argparse.ArgumentParser(
        prog="check_order",
        description="查询订单完整状态信息",
        formatter_class=argparse.RawDescriptionHelpFormatter,
        epilog="""
示例:
  # 查询订单状态
  python scripts/check_order.py --order-id 01HQ1234567890ABCDEF123456

  # 使用订单号查询
  python scripts/check_order.py --order-id ORD1234567890ABCD

  # 显示帮助
  python scripts/check_order.py --help
        """
    )
    parser.add_argument(
        "--order-id", "-o",
        required=True,
        help="订单ID 或订单号 (order_no)"
    )
    parser.add_argument(
        "--json", "-j",
        action="store_true",
        help="以 JSON 格式输出"
    )
    return parser.parse_args()


def format_datetime(dt: Optional[datetime]) -> str:
    """格式化日期时间"""
    if not dt:
        return "N/A"
    return dt.strftime("%Y-%m-%d %H:%M:%S UTC")


def format_amount(amount: Any) -> str:
    """格式化金额"""
    if amount is None:
        return "N/A"
    return str(amount)


def get_status_label(status: str) -> str:
    """获取状态中文标签"""
    try:
        return state_machine.get_status_label(OrderStatus(status))
    except:
        return status


async def get_order_by_id_or_no(session, order_id: str) -> Optional[Order]:
    """通过 ID 或订单号查询订单"""
    # 先尝试按 ID 查询
    order = await session.get(Order, order_id)
    if order:
        return order
    
    # 再尝试按订单号查询
    result = await session.execute(
        select(Order).where(Order.order_no == order_id)
    )
    return result.scalar_one_or_none()


async def get_order_details(session, order: Order) -> Dict[str, Any]:
    """获取订单详细信息"""
    details = {
        "order": {},
        "plan": None,
        "payment_address": None,
        "client_session": None,
    }
    
    # 订单基本信息
    details["order"] = {
        "id": order.id,
        "order_no": order.order_no,
        "status": order.status,
        "status_label": get_status_label(order.status),
        "purchase_type": order.purchase_type,
        "chain": order.chain,
        "asset_code": order.asset_code,
        "receive_address": order.receive_address,
        "amount_crypto": str(order.amount_crypto) if order.amount_crypto else None,
        "amount_usd_locked": str(order.amount_usd_locked) if order.amount_usd_locked else None,
        "fx_rate_locked": str(order.fx_rate_locked) if order.fx_rate_locked else None,
        "marzban_username": order.marzban_username,
        "client_version": order.client_version,
        "tx_hash": order.tx_hash,
        "tx_from": order.tx_from,
        "confirm_count": order.confirm_count,
        "error_code": order.error_code,
        "error_message": order.error_message,
        "created_at": order.created_at.isoformat() if order.created_at else None,
        "updated_at": order.updated_at.isoformat() if order.updated_at else None,
        "expires_at": order.expires_at.isoformat() if order.expires_at else None,
        "paid_at": order.paid_at.isoformat() if order.paid_at else None,
        "confirmed_at": order.confirmed_at.isoformat() if order.confirmed_at else None,
        "fulfilled_at": order.fulfilled_at.isoformat() if order.fulfilled_at else None,
    }
    
    # 查询套餐信息
    if order.plan_id:
        plan = await session.get(Plan, order.plan_id)
        if plan:
            details["plan"] = {
                "id": plan.id,
                "code": plan.code,
                "name": plan.name,
                "description": plan.description,
                "traffic_bytes": plan.traffic_bytes,
                "duration_days": plan.duration_days,
                "price_usd": str(plan.price_usd) if plan.price_usd else None,
                "supported_assets": plan.supported_assets,
                "enabled": plan.enabled,
            }
    
    # 查询收款地址
    result = await session.execute(
        select(PaymentAddress).where(PaymentAddress.allocated_order_id == order.id)
    )
    payment_address = result.scalar_one_or_none()
    if payment_address:
        details["payment_address"] = {
            "id": payment_address.id,
            "chain": payment_address.chain,
            "asset_code": payment_address.asset_code,
            "address": payment_address.address,
            "status": payment_address.status,
            "last_seen_tx_hash": payment_address.last_seen_tx_hash,
        }
    
    # 查询客户端会话
    result = await session.execute(
        select(ClientSession)
        .where(ClientSession.order_id == order.id)
        .order_by(ClientSession.created_at.desc())
    )
    client_session = result.scalar_one_or_none()
    if client_session:
        details["client_session"] = {
            "id": client_session.id,
            "marzban_username": client_session.marzban_username,
            "access_token": client_session.access_token[:30] + "..." if client_session.access_token else None,
            "refresh_token": client_session.refresh_token[:30] + "..." if client_session.refresh_token else None,
            "expires_at": client_session.expires_at.isoformat() if client_session.expires_at else None,
            "revoked_at": client_session.revoked_at.isoformat() if client_session.revoked_at else None,
        }
    
    return details


def print_order_details(details: Dict[str, Any]):
    """打印订单详情"""
    order = details["order"]
    plan = details["plan"]
    payment_address = details["payment_address"]
    client_session = details["client_session"]
    
    print("\n" + "=" * 60)
    print(f"📋 订单信息")
    print("=" * 60)
    print(f"  订单ID:     {order['id']}")
    print(f"  订单号:     {order['order_no']}")
    print(f"  状态:       {order['status_label']} ({order['status']})")
    print(f"  购买类型:   {'新购' if order['purchase_type'] == 'new' else '续费'}")
    print(f"  创建时间:   {format_datetime(order.get('created_at'))}")
    print(f"  过期时间:   {format_datetime(order.get('expires_at'))}")
    
    print("\n" + "-" * 60)
    print(f"💰 支付信息")
    print("-" * 60)
    print(f"  链:         {order['chain']}")
    print(f"  资产:       {order['asset_code']}")
    print(f"  收款地址:   {order['receive_address']}")
    print(f"  应付金额:   {format_amount(order['amount_crypto'])} {order['asset_code']}")
    print(f"  锁定金额:   ${format_amount(order['amount_usd_locked'])} USD")
    print(f"  锁定汇率:   {format_amount(order['fx_rate_locked'])}")
    
    # 交易信息
    if order.get('tx_hash'):
        print(f"\n  交易哈希:   {order['tx_hash']}")
        print(f"  付款地址:   {order.get('tx_from', 'N/A')}")
        print(f"  确认数:     {order.get('confirm_count', 0)}")
        print(f"  支付时间:   {format_datetime(order.get('paid_at'))}")
        print(f"  确认时间:   {format_datetime(order.get('confirmed_at'))}")
    else:
        print(f"\n  交易哈希:   未检测到支付")
    
    # 套餐信息
    if plan:
        print("\n" + "-" * 60)
        print(f"📦 套餐信息")
        print("-" * 60)
        print(f"  套餐代码:   {plan['code']}")
        print(f"  套餐名称:   {plan['name']}")
        print(f"  流量:       {plan['traffic_bytes'] / 1024 / 1024 / 1024:.2f} GB")
        print(f"  时长:       {plan['duration_days']} 天")
        print(f"  价格:       ${plan['price_usd']} USD")
    
    # Fulfillment 信息
    print("\n" + "-" * 60)
    print(f"✅ Fulfillment 信息")
    print("-" * 60)
    if order.get('fulfilled_at'):
        print(f"  状态:       已完成")
        print(f"  完成时间:   {format_datetime(order['fulfilled_at'])}")
        print(f"  Marzban用户: {order.get('marzban_username', 'N/A')}")
    else:
        print(f"  状态:       未完成")
        if order.get('error_code'):
            print(f"  错误码:     {order['error_code']}")
            print(f"  错误信息:   {order['error_message']}")
    
    # 地址池信息
    if payment_address:
        print("\n" + "-" * 60)
        print(f"🏦 地址池信息")
        print("-" * 60)
        print(f"  地址ID:     {payment_address['id']}")
        print(f"  链:         {payment_address['chain']}")
        print(f"  地址:       {payment_address['address']}")
        print(f"  状态:       {payment_address['status']}")
        if payment_address.get('last_seen_tx_hash'):
            print(f"  最后交易:   {payment_address['last_seen_tx_hash']}")
    
    # 会话信息
    if client_session:
        print("\n" + "-" * 60)
        print(f"🔑 客户端会话")
        print("-" * 60)
        print(f"  会话ID:     {client_session['id']}")
        print(f"  用户名:     {client_session['marzban_username']}")
        print(f"  AccessToken: {client_session['access_token']}")
        print(f"  RefreshToken: {client_session['refresh_token']}")
        print(f"  过期时间:   {format_datetime(client_session.get('expires_at'))}")
        if client_session.get('revoked_at'):
            print(f"  吊销时间:   {format_datetime(client_session['revoked_at'])}")
    
    print("\n" + "=" * 60)


async def main():
    """主函数"""
    args = parse_args()
    
    # 初始化数据库
    print("🔌 连接数据库...")
    init_db()
    
    async with get_db_context() as session:
        # 查询订单
        order = await get_order_by_id_or_no(session, args.order_id)
        
        if not order:
            print(f"❌ 订单未找到: {args.order_id}")
            sys.exit(1)
        
        # 获取订单详情
        details = await get_order_details(session, order)
        
        if args.json:
            print(json.dumps(details, indent=2, ensure_ascii=False))
        else:
            print_order_details(details)


if __name__ == "__main__":
    asyncio.run(main())
