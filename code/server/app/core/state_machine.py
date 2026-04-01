"""
订单状态机 - Order State Machine

状态定义 (10个状态):
- pending_payment: 待支付
- seen_onchain: 已发现交易
- confirming: 确认中
- paid_success: 已确认且金额匹配
- fulfilled: 已完成（已开通账号）
- expired: 超时
- underpaid: 少付
- overpaid: 多付
- failed: 支付校验失败
- late_paid: 过期后到账

状态转换规则:
pending_payment → seen_onchain (检测到交易)
pending_payment → expired (超时15分钟)
seen_onchain → confirming (确认中)
seen_onchain → underpaid/overpaid/failed (金额/币种错误)
confirming → paid_success (达到确认数)
confirming → expired (超时)
paid_success → fulfilled (开通账号成功)
any → late_paid (过期后检测到支付)
"""
from enum import Enum
from typing import Dict, Set, Optional, Callable, Any, List
from dataclasses import dataclass
from datetime import datetime, timezone
import logging
from typing import TYPE_CHECKING

if TYPE_CHECKING:
    from app.services.websocket import notify_order_status_changed

logger = logging.getLogger(__name__)


class OrderStatus(str, Enum):
    """订单状态枚举"""
    PENDING_PAYMENT = "pending_payment"      # 待支付
    SEEN_ONCHAIN = "seen_onchain"            # 已发现交易
    CONFIRMING = "confirming"                # 确认中
    PAID_SUCCESS = "paid_success"            # 已确认且金额匹配
    FULFILLED = "fulfilled"                  # 已完成（已开通账号）
    EXPIRED = "expired"                      # 超时
    UNDERPAID = "underpaid"                  # 少付
    OVERPAID = "overpaid"                    # 多付
    FAILED = "failed"                        # 支付校验失败
    LATE_PAID = "late_paid"                  # 过期后到账


class StateTransitionError(Exception):
    """状态转换错误"""
    def __init__(self, from_status: str, to_status: str, message: str = ""):
        self.from_status = from_status
        self.to_status = to_status
        self.message = message
        super().__init__(f"Invalid transition from '{from_status}' to '{to_status}': {message}")


class DuplicateTransitionError(Exception):
    """重复状态转换错误（幂等性保护）"""
    def __init__(self, order_id: str, status: str):
        self.order_id = order_id
        self.status = status
        super().__init__(f"Order {order_id} already in status '{status}'")


@dataclass
class StateTransition:
    """状态转换记录"""
    from_status: OrderStatus
    to_status: OrderStatus
    timestamp: datetime
    triggered_by: str  # 触发来源: system, manual, webhook, worker
    metadata: Optional[Dict[str, Any]] = None


class OrderStateMachine:
    """
    订单状态机
    
    负责管理订单状态的转换规则、验证和幂等性控制
    """
    
    # 状态转换图: {当前状态: {目标状态: 是否允许}}
    TRANSITIONS: Dict[OrderStatus, Set[OrderStatus]] = {
        OrderStatus.PENDING_PAYMENT: {
            OrderStatus.SEEN_ONCHAIN,   # 检测到交易
            OrderStatus.EXPIRED,        # 超时15分钟
        },
        OrderStatus.SEEN_ONCHAIN: {
            OrderStatus.CONFIRMING,     # 确认中
            OrderStatus.PAID_SUCCESS,   # 达到确认数（确认数足够时直接成功）
            OrderStatus.UNDERPAID,      # 少付
            OrderStatus.OVERPAID,       # 多付
            OrderStatus.FAILED,         # 金额/币种错误
            OrderStatus.EXPIRED,        # 超时
        },
        OrderStatus.CONFIRMING: {
            OrderStatus.PAID_SUCCESS,   # 达到确认数
            OrderStatus.EXPIRED,        # 超时
        },
        OrderStatus.PAID_SUCCESS: {
            OrderStatus.FULFILLED,      # 开通账号成功
        },
        # 终态不允许再转换（除了 late_paid 特殊情况）
        OrderStatus.FULFILLED: set(),
        OrderStatus.EXPIRED: {OrderStatus.LATE_PAID},  # 过期后检测到支付
        OrderStatus.UNDERPAID: {OrderStatus.LATE_PAID},
        OrderStatus.OVERPAID: {OrderStatus.LATE_PAID},
        OrderStatus.FAILED: {OrderStatus.LATE_PAID},
        OrderStatus.LATE_PAID: set(),  # 终态
    }
    
    # 终态列表
    TERMINAL_STATES: Set[OrderStatus] = {
        OrderStatus.FULFILLED,
        OrderStatus.LATE_PAID,
    }
    
    # 失败/异常状态
    ERROR_STATES: Set[OrderStatus] = {
        OrderStatus.EXPIRED,
        OrderStatus.UNDERPAID,
        OrderStatus.OVERPAID,
        OrderStatus.FAILED,
    }
    
    # 可支付状态（用户还未支付）
    PAYABLE_STATES: Set[OrderStatus] = {
        OrderStatus.PENDING_PAYMENT,
    }
    
    # 需要确认的状态
    REQUIRES_CONFIRMATION: Set[OrderStatus] = {
        OrderStatus.SEEN_ONCHAIN,
        OrderStatus.CONFIRMING,
    }
    
    def __init__(self):
        # 状态转换回调 {目标状态: [callback]}
        self._callbacks: Dict[OrderStatus, List[Callable]] = {}
        # 全局转换回调
        self._any_transition_callbacks: List[Callable] = []
    
    def register_callback(
        self, 
        to_status: OrderStatus, 
        callback: Callable[[str, OrderStatus, OrderStatus, Dict], None]
    ) -> None:
        """
        注册状态转换回调函数
        
        Args:
            to_status: 目标状态
            callback: 回调函数签名 (order_id, from_status, to_status, context)
        """
        if to_status not in self._callbacks:
            self._callbacks[to_status] = []
        self._callbacks[to_status].append(callback)
        logger.debug(f"Registered callback for transition to {to_status}")
    
    def register_any_transition_callback(
        self,
        callback: Callable[[str, OrderStatus, OrderStatus, Dict], None]
    ) -> None:
        """注册全局状态转换回调"""
        self._any_transition_callbacks.append(callback)
    
    def can_transition(self, from_status: OrderStatus, to_status: OrderStatus) -> bool:
        """
        检查状态转换是否允许
        
        Args:
            from_status: 当前状态
            to_status: 目标状态
            
        Returns:
            bool: 是否允许转换
        """
        # late_paid 特殊处理: 任何状态都可以转换到 late_paid（如果是过期后支付）
        if to_status == OrderStatus.LATE_PAID:
            return from_status in {
                OrderStatus.EXPIRED,
                OrderStatus.UNDERPAID,
                OrderStatus.OVERPAID,
                OrderStatus.FAILED,
            }
        
        allowed = self.TRANSITIONS.get(from_status, set())
        return to_status in allowed
    
    def get_allowed_transitions(self, from_status: OrderStatus) -> Set[OrderStatus]:
        """获取从当前状态允许的所有转换目标"""
        return self.TRANSITIONS.get(from_status, set()).copy()
    
    def validate_transition(
        self, 
        from_status: OrderStatus, 
        to_status: OrderStatus,
        raise_on_error: bool = True
    ) -> bool:
        """
        验证状态转换
        
        Args:
            from_status: 当前状态
            to_status: 目标状态
            raise_on_error: 验证失败时是否抛出异常
            
        Returns:
            bool: 是否有效
            
        Raises:
            StateTransitionError: 如果转换无效且 raise_on_error=True
        """
        # 相同状态检查（幂等性）
        if from_status == to_status:
            if raise_on_error:
                raise DuplicateTransitionError("unknown", to_status.value)
            return False
        
        # 检查转换规则
        if not self.can_transition(from_status, to_status):
            if raise_on_error:
                raise StateTransitionError(
                    from_status.value, 
                    to_status.value,
                    f"Transition not allowed from '{from_status.value}'"
                )
            return False
        
        return True
    
    def transition(
        self,
        order_id: str,
        current_status: OrderStatus,
        new_status: OrderStatus,
        triggered_by: str = "system",
        context: Optional[Dict[str, Any]] = None,
        skip_validation: bool = False
    ) -> StateTransition:
        """
        执行状态转换
        
        Args:
            order_id: 订单ID
            current_status: 当前状态
            new_status: 目标状态
            triggered_by: 触发来源
            context: 上下文数据
            skip_validation: 跳过验证（仅用于测试/修复）
            
        Returns:
            StateTransition: 转换记录
            
        Raises:
            StateTransitionError: 转换无效
            DuplicateTransitionError: 重复转换
        """
        context = context or {}
        
        # 幂等性检查：如果已经在目标状态，报错
        if current_status == new_status:
            raise DuplicateTransitionError(order_id, new_status.value)
        
        # 验证转换
        if not skip_validation:
            self.validate_transition(current_status, new_status)
        
        # 执行转换
        transition = StateTransition(
            from_status=current_status,
            to_status=new_status,
            timestamp=datetime.now(timezone.utc),
            triggered_by=triggered_by,
            metadata=context
        )
        
        logger.info(
            f"Order {order_id} state transition: {current_status.value} -> {new_status.value} "
            f"(by: {triggered_by})"
        )
        
        # 执行回调
        self._execute_callbacks(order_id, current_status, new_status, context)
        
        return transition
    
    def _execute_callbacks(
        self,
        order_id: str,
        from_status: OrderStatus,
        to_status: OrderStatus,
        context: Dict[str, Any]
    ) -> None:
        """执行状态转换回调"""
        # 全局回调
        for callback in self._any_transition_callbacks:
            try:
                callback(order_id, from_status, to_status, context)
            except Exception as e:
                logger.error(f"Global callback error: {e}")
        
        # 特定状态回调
        callbacks = self._callbacks.get(to_status, [])
        for callback in callbacks:
            try:
                callback(order_id, from_status, to_status, context)
            except Exception as e:
                logger.error(f"Callback error for {to_status}: {e}")
    
    def is_terminal(self, status: OrderStatus) -> bool:
        """检查是否为终态"""
        return status in self.TERMINAL_STATES
    
    def is_error(self, status: OrderStatus) -> bool:
        """检查是否为错误/异常状态"""
        return status in self.ERROR_STATES
    
    def is_payable(self, status: OrderStatus) -> bool:
        """检查是否可支付（等待用户支付）"""
        return status in self.PAYABLE_STATES
    
    def requires_confirmation(self, status: OrderStatus) -> bool:
        """检查是否需要链上确认"""
        return status in self.REQUIRES_CONFIRMATION
    
    def get_status_label(self, status: OrderStatus) -> str:
        """获取状态中文标签"""
        labels = {
            OrderStatus.PENDING_PAYMENT: "待支付",
            OrderStatus.SEEN_ONCHAIN: "已发现交易",
            OrderStatus.CONFIRMING: "确认中",
            OrderStatus.PAID_SUCCESS: "已确认且金额匹配",
            OrderStatus.FULFILLED: "已完成",
            OrderStatus.EXPIRED: "超时",
            OrderStatus.UNDERPAID: "少付",
            OrderStatus.OVERPAID: "多付",
            OrderStatus.FAILED: "支付校验失败",
            OrderStatus.LATE_PAID: "过期后到账",
        }
        return labels.get(status, status.value)


# 全局状态机实例
state_machine = OrderStateMachine()


# ============ 便捷转换函数（用于业务代码） ============

def transition_to_seen_onchain(
    order_id: str,
    current_status: OrderStatus,
    tx_hash: str,
    tx_from: str,
    amount: str,
    triggered_by: str = "worker"
) -> StateTransition:
    """转换到 seen_onchain 状态"""
    return state_machine.transition(
        order_id=order_id,
        current_status=current_status,
        new_status=OrderStatus.SEEN_ONCHAIN,
        triggered_by=triggered_by,
        context={
            "tx_hash": tx_hash,
            "tx_from": tx_from,
            "amount": amount,
        }
    )


def transition_to_confirming(
    order_id: str,
    current_status: OrderStatus,
    confirm_count: int = 0,
    triggered_by: str = "worker"
) -> StateTransition:
    """转换到 confirming 状态"""
    return state_machine.transition(
        order_id=order_id,
        current_status=current_status,
        new_status=OrderStatus.CONFIRMING,
        triggered_by=triggered_by,
        context={"confirm_count": confirm_count}
    )


def transition_to_paid_success(
    order_id: str,
    current_status: OrderStatus,
    confirm_count: int,
    paid_at: datetime,
    triggered_by: str = "worker"
) -> StateTransition:
    """转换到 paid_success 状态"""
    return state_machine.transition(
        order_id=order_id,
        current_status=current_status,
        new_status=OrderStatus.PAID_SUCCESS,
        triggered_by=triggered_by,
        context={
            "confirm_count": confirm_count,
            "paid_at": paid_at.isoformat(),
        }
    )


def transition_to_fulfilled(
    order_id: str,
    current_status: OrderStatus,
    marzban_username: str,
    subscription_url: str,
    triggered_by: str = "system"
) -> StateTransition:
    """转换到 fulfilled 状态"""
    return state_machine.transition(
        order_id=order_id,
        current_status=current_status,
        new_status=OrderStatus.FULFILLED,
        triggered_by=triggered_by,
        context={
            "marzban_username": marzban_username,
            "subscription_url": subscription_url,
        }
    )


def transition_to_expired(
    order_id: str,
    current_status: OrderStatus,
    reason: str = "timeout",
    triggered_by: str = "worker"
) -> StateTransition:
    """转换到 expired 状态"""
    return state_machine.transition(
        order_id=order_id,
        current_status=current_status,
        new_status=OrderStatus.EXPIRED,
        triggered_by=triggered_by,
        context={"expired_reason": reason}
    )


def transition_to_underpaid(
    order_id: str,
    current_status: OrderStatus,
    expected: str,
    actual: str,
    triggered_by: str = "worker"
) -> StateTransition:
    """转换到 underpaid 状态（少付）"""
    return state_machine.transition(
        order_id=order_id,
        current_status=current_status,
        new_status=OrderStatus.UNDERPAID,
        triggered_by=triggered_by,
        context={
            "expected_amount": expected,
            "actual_amount": actual,
            "shortage": str(float(expected) - float(actual)),
        }
    )


def transition_to_overpaid(
    order_id: str,
    current_status: OrderStatus,
    expected: str,
    actual: str,
    triggered_by: str = "worker"
) -> StateTransition:
    """转换到 overpaid 状态（多付）"""
    return state_machine.transition(
        order_id=order_id,
        current_status=current_status,
        new_status=OrderStatus.OVERPAID,
        triggered_by=triggered_by,
        context={
            "expected_amount": expected,
            "actual_amount": actual,
            "overage": str(float(actual) - float(expected)),
        }
    )


def transition_to_failed(
    order_id: str,
    current_status: OrderStatus,
    error_code: str,
    error_message: str,
    triggered_by: str = "worker"
) -> StateTransition:
    """转换到 failed 状态（支付校验失败）"""
    return state_machine.transition(
        order_id=order_id,
        current_status=current_status,
        new_status=OrderStatus.FAILED,
        triggered_by=triggered_by,
        context={
            "error_code": error_code,
            "error_message": error_message,
        }
    )


def transition_to_late_paid(
    order_id: str,
    current_status: OrderStatus,
    tx_hash: str,
    paid_at: datetime,
    triggered_by: str = "worker"
) -> StateTransition:
    """
    转换到 late_paid 状态（过期后到账）
    
    这是一个特殊状态，从 expired/underpaid/overpaid/failed 都可以转换过来
    """
    return state_machine.transition(
        order_id=order_id,
        current_status=current_status,
        new_status=OrderStatus.LATE_PAID,
        triggered_by=triggered_by,
        context={
            "tx_hash": tx_hash,
            "paid_at": paid_at.isoformat(),
            "original_status": current_status.value,
        }
    )
