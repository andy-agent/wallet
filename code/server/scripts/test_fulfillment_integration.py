#!/usr/bin/env python3
"""
Fulfillment 服务与 Marzban 集成测试

测试完整的订单履行流程:
1. 创建新购订单并履行
2. 创建续费订单并履行
3. 验证用户生命周期

使用方法:
    cd /Users/cnyirui/git/projects/liaojiang/code/server
    python scripts/test_fulfillment_integration.py

要求:
    - 数据库已配置并运行
    - Marzban 服务可访问
    - 环境变量已设置
"""

import asyncio
import os
import sys
from datetime import datetime, timedelta, timezone
from decimal import Decimal

# 添加项目路径
sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

import ulid
import jwt
from sqlalchemy import select

# 设置测试环境
os.environ.setdefault("DATABASE_URL", "sqlite+aiosqlite:///./test_fulfillment.db")
os.environ.setdefault("JWT_SECRET", "test-jwt-secret-32chars-long!!")
os.environ.setdefault("ENCRYPTION_MASTER_KEY", "test-master-key-32chars-long!!")
os.environ.setdefault("MARZBAN_BASE_URL", os.getenv("MARZBAN_BASE_URL", "http://localhost:8000"))
os.environ.setdefault("MARZBAN_ADMIN_USERNAME", os.getenv("MARZBAN_ADMIN_USERNAME", "admin"))
os.environ.setdefault("MARZBAN_ADMIN_PASSWORD", os.getenv("MARZBAN_ADMIN_PASSWORD", "admin"))

from app.core.database import init_db, get_db_context, engine
from app.core.config import get_settings
from app.models.order import Order
from app.models.plan import Plan
from app.models.client_session import ClientSession
from app.core.state_machine import OrderStatus
from app.services.fulfillment import (
    fulfill_new_order, 
    fulfill_renew_order,
    generate_client_tokens,
    verify_client_token,
    FulfillmentResult,
    FulfillmentError
)
from app.integrations.marzban import MarzbanClient, MarzbanAPIError


class FulfillmentIntegrationTest:
    """Fulfillment 集成测试类"""
    
    def __init__(self):
        self.test_plan: Plan = None
        self.test_order_new: Order = None
        self.test_order_renew: Order = None
        self.fulfillment_result: FulfillmentResult = None
        self.results = []
        self.marzban_client: MarzbanClient = None
        
    def log(self, message: str, level: str = "INFO"):
        """记录日志"""
        timestamp = datetime.now().strftime("%Y-%m-%d %H:%M:%S")
        print(f"[{timestamp}] [{level}] {message}")
        self.results.append({"time": timestamp, "level": level, "message": message})
        
    async def setup(self):
        """测试前准备"""
        self.log("")
        self.log("=" * 60)
        self.log("初始化测试环境")
        self.log("=" * 60)
        
        # 初始化数据库
        await init_db()
        self.log("数据库初始化完成")
        
        # 创建测试套餐
        async with get_db_context() as session:
            self.test_plan = Plan(
                id=str(ulid.new().str),
                code="test-plan-30d-10gb",
                name="Test Plan 30 Days 10GB",
                description="Test plan for integration testing",
                traffic_bytes=10 * 1024 * 1024 * 1024,  # 10GB
                duration_days=30,
                price_usd=Decimal("9.99"),
                supported_assets=["SOL", "USDT_TRC20"],
                enabled=True,
                sort_order=1,
            )
            session.add(self.test_plan)
            await session.commit()
            self.log(f"测试套餐创建完成: {self.test_plan.code}")
            
        # 测试 Marzban 连接
        try:
            settings = get_settings()
            self.marzban_client = MarzbanClient(
                base_url=settings.marzban_base_url,
                username=settings.marzban_admin_username,
                password=settings.marzban_admin_password
            )
            await self.marzban_client.authenticate()
            self.log(f"Marzban 连接成功: {settings.marzban_base_url}")
        except Exception as e:
            self.log(f"Marzban 连接失败: {e}", "ERROR")
            raise
            
    async def test_create_new_order(self) -> bool:
        """测试新购订单履行"""
        self.log("")
        self.log("-" * 60)
        self.log("测试 1: 新购订单履行")
        self.log("-" * 60)
        
        try:
            # 创建新购订单
            order_id = str(ulid.new().str)
            order_no = f"ORD{ulid.new().str[:16].upper()}"
            
            async with get_db_context() as session:
                self.test_order_new = Order(
                    id=order_id,
                    order_no=order_no,
                    purchase_type="new",
                    plan_id=self.test_plan.id,
                    client_user_id=None,
                    marzban_username=None,
                    chain="solana",
                    asset_code="SOL",
                    receive_address="TestSolanaAddress123",
                    amount_crypto=Decimal("0.1"),
                    amount_usd_locked=Decimal("9.99"),
                    fx_rate_locked=Decimal("99.90"),
                    status=OrderStatus.PAID_SUCCESS.value,
                    expires_at=datetime.now(timezone.utc) + timedelta(minutes=15),
                    client_device_id="test-device-123",
                    client_version="1.0.0",
                )
                session.add(self.test_order_new)
                await session.commit()
                
            self.log(f"创建新购订单: {order_no}")
            self.log(f"  - 订单ID: {order_id}")
            self.log(f"  - 套餐: {self.test_plan.code}")
            self.log(f"  - 状态: {self.test_order_new.status}")
            
            # 履行订单
            self.log("开始履行新购订单...")
            result = await fulfill_new_order(order_id)
            self.fulfillment_result = result
            
            self.log(f"订单履行成功 ✓")
            self.log(f"  - Marzban 用户名: {result.marzban_username}")
            self.log(f"  - 订阅链接: {result.subscription_url}")
            self.log(f"  - Access Token: {result.access_token[:30]}...")
            self.log(f"  - 过期时间: {result.expires_at.isoformat()}")
            
            # 验证订单状态
            async with get_db_context() as session:
                order = await session.get(Order, order_id)
                if order.status == OrderStatus.FULFILLED.value:
                    self.log(f"  - 订单状态: {order.status} ✓")
                else:
                    self.log(f"  - 订单状态: {order.status} (预期: fulfilled)", "ERROR")
                    return False
                    
            # 验证 Marzban 用户
            user = await self.marzban_client.get_user(result.marzban_username)
            if user:
                self.log(f"  - Marzban 用户验证: 存在 ✓")
                self.log(f"    - 状态: {user.status}")
                self.log(f"    - 过期时间: {datetime.fromtimestamp(user.expire).isoformat() if user.expire else 'N/A'}")
                self.log(f"    - 流量限制: {user.data_limit / 1024 / 1024 / 1024:.2f} GB")
            else:
                self.log(f"  - Marzban 用户验证: 不存在 ✗", "ERROR")
                return False
                
            return True
            
        except FulfillmentError as e:
            self.log(f"履行失败: [{e.error_code}] {e.error_message}", "ERROR")
            return False
        except Exception as e:
            self.log(f"履行异常: {str(e)}", "ERROR")
            import traceback
            self.log(traceback.format_exc(), "ERROR")
            return False
            
    async def test_renew_order(self) -> bool:
        """测试续费订单履行"""
        self.log("")
        self.log("-" * 60)
        self.log("测试 2: 续费订单履行")
        self.log("-" * 60)
        
        if not self.fulfillment_result:
            self.log("没有新购履行结果，无法测试续费", "ERROR")
            return False
            
        try:
            # 获取原用户信息
            username = self.fulfillment_result.marzban_username
            original_user = await self.marzban_client.get_user(username)
            
            self.log(f"原用户信息:")
            self.log(f"  - 用户名: {original_user.username}")
            self.log(f"  - 当前过期时间: {datetime.fromtimestamp(original_user.expire).isoformat()}")
            self.log(f"  - 当前流量限制: {original_user.data_limit / 1024 / 1024 / 1024:.2f} GB")
            
            # 创建续费订单
            order_id = str(ulid.new().str)
            order_no = f"ORD{ulid.new().str[:16].upper()}"
            
            async with get_db_context() as session:
                self.test_order_renew = Order(
                    id=order_id,
                    order_no=order_no,
                    purchase_type="renew",
                    plan_id=self.test_plan.id,
                    client_user_id=username,
                    marzban_username=username,
                    chain="solana",
                    asset_code="SOL",
                    receive_address="TestSolanaAddress456",
                    amount_crypto=Decimal("0.1"),
                    amount_usd_locked=Decimal("9.99"),
                    fx_rate_locked=Decimal("99.90"),
                    status=OrderStatus.PAID_SUCCESS.value,
                    expires_at=datetime.now(timezone.utc) + timedelta(minutes=15),
                    client_device_id="test-device-123",
                    client_version="1.0.0",
                )
                session.add(self.test_order_renew)
                await session.commit()
                
            self.log(f"创建续费订单: {order_no}")
            self.log(f"  - 订单ID: {order_id}")
            self.log(f"  - 关联用户: {username}")
            
            # 使用 access_token 履行续费订单
            self.log("开始履行续费订单...")
            result = await fulfill_renew_order(order_id, self.fulfillment_result.access_token)
            
            self.log(f"续费履行成功 ✓")
            self.log(f"  - Marzban 用户名: {result.marzban_username}")
            self.log(f"  - 新 Access Token: {result.access_token[:30]}...")
            
            # 验证更新后的用户
            updated_user = await self.marzban_client.get_user(username)
            self.log(f"更新后用户信息:")
            self.log(f"  - 新过期时间: {datetime.fromtimestamp(updated_user.expire).isoformat()}")
            self.log(f"  - 新流量限制: {updated_user.data_limit / 1024 / 1024 / 1024:.2f} GB")
            
            # 验证续费效果
            if updated_user.expire > original_user.expire:
                self.log(f"  - 过期时间延长: ✓")
            else:
                self.log(f"  - 过期时间延长: ✗", "ERROR")
                return False
                
            if updated_user.data_limit > original_user.data_limit:
                self.log(f"  - 流量增加: ✓")
            else:
                self.log(f"  - 流量增加: ✗", "ERROR")
                return False
                
            return True
            
        except FulfillmentError as e:
            self.log(f"续费履行失败: [{e.error_code}] {e.error_message}", "ERROR")
            return False
        except Exception as e:
            self.log(f"续费履行异常: {str(e)}", "ERROR")
            import traceback
            self.log(traceback.format_exc(), "ERROR")
            return False
            
    async def test_token_validation(self) -> bool:
        """测试 Token 验证"""
        self.log("")
        self.log("-" * 60)
        self.log("测试 3: Token 验证")
        self.log("-" * 60)
        
        if not self.fulfillment_result:
            self.log("没有履行结果，无法测试 Token", "ERROR")
            return False
            
        try:
            # 验证 access token
            username = verify_client_token(
                self.fulfillment_result.access_token, 
                expected_type="access"
            )
            self.log(f"Access Token 验证: ✓")
            self.log(f"  - 用户名: {username}")
            
            # 验证 refresh token
            username = verify_client_token(
                self.fulfillment_result.refresh_token,
                expected_type="refresh"
            )
            self.log(f"Refresh Token 验证: ✓")
            self.log(f"  - 用户名: {username}")
            
            return True
            
        except FulfillmentError as e:
            self.log(f"Token 验证失败: {e.error_message}", "ERROR")
            return False
        except Exception as e:
            self.log(f"Token 验证异常: {str(e)}", "ERROR")
            return False
            
    async def cleanup(self):
        """清理测试数据"""
        self.log("")
        self.log("-" * 60)
        self.log("清理测试数据")
        self.log("-" * 60)
        
        try:
            # 删除测试用户
            if self.fulfillment_result:
                username = self.fulfillment_result.marzban_username
                try:
                    await self.marzban_client.delete_user(username)
                    self.log(f"删除 Marzban 用户: {username} ✓")
                except Exception as e:
                    self.log(f"删除 Marzban 用户失败: {e}", "WARNING")
                    
            # 关闭 Marzban 客户端
            if self.marzban_client:
                await self.marzban_client.close()
                self.log("Marzban 客户端已关闭")
                
            self.log("清理完成")
            
        except Exception as e:
            self.log(f"清理异常: {e}", "WARNING")
            
    def print_summary(self):
        """打印测试摘要"""
        self.log("")
        self.log("=" * 60)
        self.log("测试摘要")
        self.log("=" * 60)
        
        errors = [r for r in self.results if r["level"] == "ERROR"]
        warnings = [r for r in self.results if r["level"] == "WARNING"]
        
        total = len([r for r in self.results if r["message"].startswith("测试 ")])
        passed = total - len(errors)
        
        self.log(f"测试结果:")
        self.log(f"  - 通过: {passed}")
        self.log(f"  - 失败: {len(errors)}")
        self.log(f"  - 警告: {len(warnings)}")
        
        if errors:
            self.log("")
            self.log("错误详情:")
            for err in errors:
                self.log(f"  - {err['message']}", "ERROR")
                
        if not errors:
            self.log("")
            self.log("所有测试通过! ✓")
        else:
            self.log("")
            self.log("测试完成，但存在错误", "WARNING")


async def main():
    """主函数"""
    # 加载 .env 文件
    env_path = os.path.join(os.path.dirname(os.path.dirname(os.path.abspath(__file__))), ".env")
    if os.path.exists(env_path):
        print(f"加载环境变量: {env_path}")
        with open(env_path) as f:
            for line in f:
                line = line.strip()
                if line and not line.startswith("#") and "=" in line:
                    key, value = line.split("=", 1)
                    os.environ.setdefault(key, value)
    
    test = FulfillmentIntegrationTest()
    success = True
    
    try:
        await test.setup()
        
        # 运行测试
        if not await test.test_create_new_order():
            success = False
            
        if not await test.test_renew_order():
            success = False
            
        if not await test.test_token_validation():
            success = False
            
    except Exception as e:
        test.log(f"测试过程中发生异常: {str(e)}", "ERROR")
        import traceback
        test.log(traceback.format_exc(), "ERROR")
        success = False
    finally:
        await test.cleanup()
        test.print_summary()
    
    return 0 if success else 1


if __name__ == "__main__":
    exit_code = asyncio.run(main())
    sys.exit(exit_code)
