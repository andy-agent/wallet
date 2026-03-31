"""
手动真实交易测试脚本

此脚本用于在真实区块链上执行端到端测试。
由于需要真实钱包私钥，需要手动配置和触发。

使用方法:
1. 配置测试钱包私钥 (环境变量)
2. 运行脚本创建订单
3. 根据输出手动发送交易
4. 再次运行脚本验证支付结果
"""
import asyncio
import os
import sys
from decimal import Decimal
from datetime import datetime, timezone, timedelta

import httpx
from sqlalchemy import select
from sqlalchemy.ext.asyncio import AsyncSession

# 添加项目根目录到路径
sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

# 确保使用真实模式
os.environ["SOLANA_MOCK_MODE"] = "false"
os.environ["TRON_MOCK_MODE"] = "false"

from app.core.config import get_settings
from app.core.database import AsyncSessionLocal
from app.models.order import Order
from app.integrations.solana import SolanaClient
from app.workers.scanner import scan_pending_orders, confirm_seen_transactions
from app.workers.fulfillment import process_paid_orders


BASE_URL = "http://localhost:8000"


async def create_test_order(plan_id: str, asset_code: str = "SOL"):
    """创建测试订单"""
    async with httpx.AsyncClient(base_url=BASE_URL) as client:
        response = await client.post(
            "/client/v1/orders",
            json={
                "plan_id": plan_id,
                "purchase_type": "new",
                "asset_code": asset_code,
            },
            headers={
                "X-Device-ID": f"manual_test_{datetime.now().strftime('%Y%m%d%H%M%S')}",
                "X-Client-Version": "1.0.0-manual",
            }
        )
        
        if response.status_code != 200:
            print(f"创建订单失败: {response.text}")
            return None
        
        return response.json()["data"]


async def get_order_status(order_id: str):
    """获取订单状态"""
    async with httpx.AsyncClient(base_url=BASE_URL) as client:
        response = await client.get(f"/client/v1/orders/{order_id}/status")
        if response.status_code == 200:
            return response.json()["data"]
        return None


async def check_solana_payment(order):
    """检查 Solana 支付状态"""
    settings = get_settings()
    client = SolanaClient(
        rpc_url=settings.solana_rpc_url,
        mock_mode=False
    )
    
    try:
        print(f"\n检查 Solana Devnet 支付...")
        print(f"  收款地址: {order['receive_address']}")
        print(f"  期望金额: {order['amount_crypto']} SOL")
        
        # 查询地址余额
        balance = await client.get_balance(order['receive_address'])
        print(f"  当前余额: {balance} SOL")
        
        # 检测支付
        result = await client.detect_payment(
            address=order['receive_address'],
            expected_amount=Decimal(order['amount_crypto'])
        )
        
        if result and result.found:
            print(f"  ✓ 检测到支付!")
            print(f"    - 交易哈希: {result.tx_hash}")
            print(f"    - 支付金额: {result.amount} SOL")
            print(f"    - 确认数: {result.confirmations}")
            print(f"    - 状态: {result.status}")
            return True
        else:
            print(f"  ✗ 未检测到支付")
            return False
            
    except Exception as e:
        print(f"  检查失败: {e}")
        return False
    finally:
        await client.close()


async def run_worker_cycle():
    """运行一轮 worker 处理"""
    print("\n运行 Worker 处理周期...")
    try:
        await scan_pending_orders()
        print("  ✓ 扫描待支付订单完成")
        
        await confirm_seen_transactions()
        print("  ✓ 确认交易完成")
        
        await process_paid_orders()
        print("  ✓ 处理已支付订单完成")
        
    except Exception as e:
        print(f"  Worker 错误: {e}")


async def main():
    """主函数"""
    print("="*60)
    print("真实区块链手动测试工具")
    print("="*60)
    
    settings = get_settings()
    print(f"\n环境配置:")
    print(f"  Solana RPC: {settings.solana_rpc_url}")
    print(f"  Solana Mock: {settings.solana_mock_mode}")
    print(f"  Tron RPC: {settings.tron_rpc_url}")
    print(f"  Tron Mock: {settings.tron_mock_mode}")
    
    # 查询可用套餐
    print("\n" + "-"*60)
    print("获取可用套餐...")
    async with httpx.AsyncClient(base_url=BASE_URL) as client:
        response = await client.get("/client/v1/plans")
        if response.status_code != 200:
            print(f"获取套餐失败: {response.text}")
            return
        
        plans = response.json()["data"]["plans"]
        if not plans:
            print("没有可用套餐")
            return
        
        print(f"\n可用套餐 ({len(plans)} 个):")
        for i, plan in enumerate(plans[:5]):
            print(f"  {i+1}. {plan['name']} - ${plan['price_usd']} - {plan['duration_days']}天")
            print(f"     ID: {plan['id']}")
    
    # 选择套餐
    test_plan_id = plans[0]["id"]
    print(f"\n使用套餐: {plans[0]['name']} ({test_plan_id})")
    
    # 创建订单
    print("\n" + "-"*60)
    print("创建 SOL 支付订单...")
    order = await create_test_order(test_plan_id, "SOL")
    if not order:
        return
    
    print(f"\n✓ 订单创建成功!")
    print(f"  订单 ID: {order['order_id']}")
    print(f"  订单号: {order['order_no']}")
    print(f"  支付链: {order['chain']}")
    print(f"  资产: {order['asset_code']}")
    print(f"\n⬇️  请向以下地址发送 {order['amount_crypto']} SOL:")
    print(f"   {order['receive_address']}")
    print(f"\n📎 Solana Devnet 获取测试币: https://faucet.solana.com/")
    print(f"\n⏳ 发送交易后，按回车继续检测...")
    input()
    
    # 检查支付
    print("\n" + "-"*60)
    payment_found = await check_solana_payment(order)
    
    if payment_found:
        # 运行 worker 处理
        await run_worker_cycle()
        
        # 检查订单最终状态
        print("\n" + "-"*60)
        print("检查订单最终状态...")
        
        async with AsyncSessionLocal() as db:
            result = await db.execute(
                select(Order).where(Order.id == order['order_id'])
            )
            db_order = result.scalar_one_or_none()
            
            if db_order:
                print(f"\n  订单状态: {db_order.status}")
                print(f"  交易哈希: {db_order.tx_hash or 'N/A'}")
                print(f"  确认数: {db_order.confirm_count}")
                print(f"  支付时间: {db_order.paid_at}")
                print(f"  履行时间: {db_order.fulfilled_at}")
                
                if db_order.marzban_username:
                    print(f"  Marzban 用户: {db_order.marzban_username}")
            else:
                print("  订单未找到")
        
        # 通过 API 获取订单状态
        status = await get_order_status(order['order_id'])
        if status:
            print(f"\n  API 状态: {status['status']}")
    else:
        print("\n⚠️  未检测到支付，请确认交易已发送并等待确认")
    
    print("\n" + "="*60)
    print("测试完成")
    print("="*60)


if __name__ == "__main__":
    asyncio.run(main())
