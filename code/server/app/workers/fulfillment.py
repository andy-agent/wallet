"""
开通任务实现模块

负责为已支付的订单开通 Marzban 账号。
所有任务都必须是幂等的。
"""
import logging
from datetime import datetime, timedelta

from sqlalchemy import select
from sqlalchemy.ext.asyncio import AsyncSession

from app.core.database import get_db_context
from app.core.state_machine import (
    OrderStatus,
    transition_to_fulfilled,
    DuplicateTransitionError,
    StateTransitionError,
)
from app.core.config import get_settings
from app.models.order import Order
from app.models.plan import Plan
from app.integrations.marzban import MarzbanClient, get_marzban_client, MarzbanAPIError

logger = logging.getLogger(__name__)


# ==================== Fulfillment Service ====================

class FulfillmentService:
    """
    订单开通服务
    
    负责调用 Marzban API 为用户开通 VPN 账号。
    支持新购和续费两种场景。
    """
    
    def __init__(self, marzban_client: MarzbanClient):
        self.marzban = marzban_client
    
    async def fulfill_order(self, order: Order, plan: Plan) -> dict:
        """
        开通订单
        
        Args:
            order: 订单对象
            plan: 套餐对象
            
        Returns:
            dict: 开通结果，包含 username 和 subscription_url
            
        Raises:
            MarzbanAPIError: Marzban API 调用失败
            ValueError: 参数错误
        """
        if order.purchase_type == "new":
            return await self._create_new_user(order, plan)
        elif order.purchase_type == "renew":
            return await self._renew_user(order, plan)
        else:
            raise ValueError(f"Unknown purchase_type: {order.purchase_type}")
    
    async def _create_new_user(self, order: Order, plan: Plan) -> dict:
        """
        创建新用户
        
        Args:
            order: 订单对象
            plan: 套餐对象
            
        Returns:
            dict: 创建结果
        """
        # 生成用户名：使用订单号或设备ID+时间戳
        username = self._generate_username(order)
        
        # 计算过期时间
        expire_timestamp = self._calculate_expire_timestamp(plan.duration_days)
        
        # 转换流量限制为字节
        data_limit_bytes = plan.data_limit_gb * (1024 ** 3) if plan.data_limit_gb else None
        
        logger.info(f"Creating new Marzban user: {username}")
        
        # 调用 Marzban API 创建用户
        user = await self.marzban.create_user(
            username=username,
            expire=expire_timestamp,
            data_limit=data_limit_bytes
        )
        
        return {
            "username": user.username,
            "subscription_url": user.subscription_url,
            "expire": user.expire,
            "data_limit": user.data_limit,
        }
    
    async def _renew_user(self, order: Order, plan: Plan) -> dict:
        """
        续费用户
        
        Args:
            order: 订单对象
            plan: 套餐对象
            
        Returns:
            dict: 续费结果
        """
        # 续费需要关联已有用户名
        username = order.marzban_username
        if not username:
            # 如果没有关联用户名，尝试创建新用户
            logger.warning(f"Renew order {order.order_no} has no marzban_username, creating new user")
            return await self._create_new_user(order, plan)
        
        # 获取现有用户信息
        existing_user = await self.marzban.get_user(username)
        if not existing_user:
            logger.warning(f"User {username} not found in Marzban, creating new user")
            return await self._create_new_user(order, plan)
        
        # 计算新的过期时间
        # 如果用户已过期，从当前时间开始计算；否则从原过期时间延长
        now_timestamp = int(datetime.utcnow().timestamp())
        if existing_user.expire and existing_user.expire > now_timestamp:
            # 未过期，延长
            new_expire = existing_user.expire + (plan.duration_days * 86400)
        else:
            # 已过期或永不过期，从当前时间计算
            new_expire = self._calculate_expire_timestamp(plan.duration_days)
        
        # 计算新的流量限制
        current_limit = existing_user.data_limit or 0
        new_data_limit = None
        if plan.data_limit_gb:
            additional_bytes = plan.data_limit_gb * (1024 ** 3)
            new_data_limit = current_limit + additional_bytes
        
        logger.info(f"Renewing Marzban user: {username}")
        
        # 调用 Marzban API 修改用户
        user = await self.marzban.modify_user(
            username=username,
            expire=new_expire,
            data_limit=new_data_limit,
            status="active"  # 确保用户状态为 active
        )
        
        return {
            "username": user.username,
            "subscription_url": user.subscription_url,
            "expire": user.expire,
            "data_limit": user.data_limit,
        }
    
    def _generate_username(self, order: Order) -> str:
        """
        生成 Marzban 用户名
        
        格式: {设备ID前8位}_{时间戳后6位}
        
        Args:
            order: 订单对象
            
        Returns:
            str: 用户名
        """
        device_prefix = order.client_device_id[:8] if order.client_device_id else "user"
        time_suffix = str(int(datetime.utcnow().timestamp()))[-6:]
        return f"{device_prefix}_{time_suffix}"
    
    def _calculate_expire_timestamp(self, duration_days: int) -> int:
        """
        计算过期时间戳
        
        Args:
            duration_days: 套餐时长（天）
            
        Returns:
            int: Unix 时间戳（秒）
        """
        expire_date = datetime.utcnow() + timedelta(days=duration_days)
        return int(expire_date.timestamp())


# ==================== Task: Fulfill Paid Orders ====================

async def fulfill_paid_orders():
    """
    开通已支付的订单
    
    执行频率: 每5秒
    
    流程:
    1. 查询 paid_success 状态的订单
    2. 调用 FulfillmentService 开通账号
    3. 成功后更新为 fulfilled 状态
    
    幂等性保证:
    - 使用数据库事务
    - 状态转换使用状态机的幂等性检查
    - Marzban 用户名唯一性保证
    """
    logger.debug("Starting fulfill_paid_orders task")
    
    async with get_db_context() as session:
        try:
            # 查询 paid_success 状态的订单
            stmt = (
                select(Order)
                .where(Order.status == OrderStatus.PAID_SUCCESS.value)
                .limit(50)  # 批次处理
            )
            result = await session.execute(stmt)
            orders = result.scalars().all()
            
            if not orders:
                logger.debug("No paid orders to fulfill")
                return
            
            logger.info(f"Found {len(orders)} paid orders to fulfill")
            
            # 创建 Marzban 客户端
            marzban_client = await get_marzban_client()
            fulfillment_service = FulfillmentService(marzban_client)
            
            fulfilled_count = 0
            failed_count = 0
            
            for order in orders:
                try:
                    success = await _fulfill_single_order(
                        session, order, fulfillment_service
                    )
                    if success:
                        fulfilled_count += 1
                    else:
                        failed_count += 1
                        
                except DuplicateTransitionError:
                    logger.debug(f"Order {order.id} already fulfilled")
                    fulfilled_count += 1
                except Exception as e:
                    logger.error(f"Error fulfilling order {order.id}: {e}")
                    failed_count += 1
                    continue
            
            await session.commit()
            
            # 关闭 Marzban 客户端
            await marzban_client.close()
            
            logger.info(
                f"Completed fulfill_paid_orders: "
                f"fulfilled={fulfilled_count}, failed={failed_count}"
            )
            
        except Exception as e:
            logger.error(f"Error in fulfill_paid_orders: {e}", exc_info=True)
            await session.rollback()
            raise


async def _fulfill_single_order(
    session: AsyncSession,
    order: Order,
    fulfillment_service: FulfillmentService
) -> bool:
    """
    开通单个订单
    
    Args:
        session: 数据库会话
        order: 订单对象
        fulfillment_service: 开通服务
        
    Returns:
        bool: 是否成功
    """
    # 获取套餐信息
    from app.models.plan import Plan
    stmt = select(Plan).where(Plan.id == order.plan_id)
    result = await session.execute(stmt)
    plan = result.scalar_one_or_none()
    
    if not plan:
        logger.error(f"Plan not found for order {order.order_no}: {order.plan_id}")
        order.error_code = "PLAN_NOT_FOUND"
        order.error_message = f"Plan {order.plan_id} not found"
        await session.flush()
        return False
    
    try:
        # 调用开通服务
        result = await fulfillment_service.fulfill_order(order, plan)
        
        # 执行状态转换
        transition_to_fulfilled(
            order_id=order.id,
            current_status=OrderStatus(order.status),
            marzban_username=result["username"],
            subscription_url=result["subscription_url"],
            triggered_by="system"
        )
        
        # 更新订单信息
        order.status = OrderStatus.FULFILLED.value
        order.marzban_username = result["username"]
        order.fulfilled_at = datetime.utcnow()
        
        await session.flush()
        
        logger.info(
            f"Order {order.order_no} fulfilled: "
            f"username={result['username']}, "
            f"expire={result.get('expire')}"
        )
        
        return True
        
    except MarzbanAPIError as e:
        # Marzban API 错误，记录但不改变状态（下次重试）
        logger.error(f"Marzban API error for order {order.order_no}: {e}")
        order.error_code = "MARZBAN_API_ERROR"
        order.error_message = str(e)[:256]
        await session.flush()
        return False
        
    except StateTransitionError as e:
        # 状态转换错误
        logger.error(f"State transition error for order {order.order_no}: {e}")
        order.error_code = "STATE_TRANSITION_ERROR"
        order.error_message = str(e)[:256]
        await session.flush()
        return False
        
    except Exception as e:
        # 其他错误
        logger.error(f"Unexpected error fulfilling order {order.order_no}: {e}")
        order.error_code = "FULFILLMENT_ERROR"
        order.error_message = str(e)[:256]
        await session.flush()
        return False
