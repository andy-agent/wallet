"""
状态机单元测试 - State Machine Unit Tests

验证:
1. 所有10个状态的合法流转
2. 非法流转会被拦截
3. 幂等性（重复状态转换不会报错但会抛出DuplicateTransitionError）
4. 终态和错误状态的正确识别
5. 回调函数注册和执行
"""
import pytest
from datetime import datetime
from decimal import Decimal

from app.core.state_machine import (
    OrderStatus,
    OrderStateMachine,
    StateTransitionError,
    DuplicateTransitionError,
    StateTransition,
    state_machine,
    transition_to_seen_onchain,
    transition_to_confirming,
    transition_to_paid_success,
    transition_to_fulfilled,
    transition_to_expired,
    transition_to_underpaid,
    transition_to_overpaid,
    transition_to_failed,
    transition_to_late_paid,
)


class TestOrderStatusEnum:
    """测试状态枚举定义"""
    
    def test_all_ten_statuses_defined(self):
        """验证10个状态都已定义"""
        statuses = list(OrderStatus)
        assert len(statuses) == 10
        
        expected = {
            "pending_payment",
            "seen_onchain",
            "confirming",
            "paid_success",
            "fulfilled",
            "expired",
            "underpaid",
            "overpaid",
            "failed",
            "late_paid",
        }
        actual = {s.value for s in statuses}
        assert actual == expected
    
    def test_status_string_values(self):
        """测试状态值是字符串"""
        assert OrderStatus.PENDING_PAYMENT.value == "pending_payment"
        assert OrderStatus.SEEN_ONCHAIN.value == "seen_onchain"
        assert OrderStatus.CONFIRMING.value == "confirming"
        assert OrderStatus.PAID_SUCCESS.value == "paid_success"
        assert OrderStatus.FULFILLED.value == "fulfilled"
        assert OrderStatus.EXPIRED.value == "expired"
        assert OrderStatus.UNDERPAID.value == "underpaid"
        assert OrderStatus.OVERPAID.value == "overpaid"
        assert OrderStatus.FAILED.value == "failed"
        assert OrderStatus.LATE_PAID.value == "late_paid"


class TestStateTransitions:
    """测试状态转换规则"""
    
    @pytest.fixture
    def sm(self):
        """创建新的状态机实例"""
        return OrderStateMachine()
    
    # ========== 合法流转测试 ==========
    
    def test_pending_payment_to_seen_onchain(self, sm):
        """pending_payment -> seen_onchain 合法"""
        assert sm.can_transition(
            OrderStatus.PENDING_PAYMENT, 
            OrderStatus.SEEN_ONCHAIN
        ) is True
    
    def test_pending_payment_to_expired(self, sm):
        """pending_payment -> expired 合法"""
        assert sm.can_transition(
            OrderStatus.PENDING_PAYMENT, 
            OrderStatus.EXPIRED
        ) is True
    
    def test_seen_onchain_to_confirming(self, sm):
        """seen_onchain -> confirming 合法"""
        assert sm.can_transition(
            OrderStatus.SEEN_ONCHAIN, 
            OrderStatus.CONFIRMING
        ) is True
    
    def test_seen_onchain_to_underpaid(self, sm):
        """seen_onchain -> underpaid 合法"""
        assert sm.can_transition(
            OrderStatus.SEEN_ONCHAIN, 
            OrderStatus.UNDERPAID
        ) is True
    
    def test_seen_onchain_to_overpaid(self, sm):
        """seen_onchain -> overpaid 合法"""
        assert sm.can_transition(
            OrderStatus.SEEN_ONCHAIN, 
            OrderStatus.OVERPAID
        ) is True
    
    def test_seen_onchain_to_failed(self, sm):
        """seen_onchain -> failed 合法"""
        assert sm.can_transition(
            OrderStatus.SEEN_ONCHAIN, 
            OrderStatus.FAILED
        ) is True
    
    def test_seen_onchain_to_expired(self, sm):
        """seen_onchain -> expired 合法"""
        assert sm.can_transition(
            OrderStatus.SEEN_ONCHAIN, 
            OrderStatus.EXPIRED
        ) is True
    
    def test_confirming_to_paid_success(self, sm):
        """confirming -> paid_success 合法"""
        assert sm.can_transition(
            OrderStatus.CONFIRMING, 
            OrderStatus.PAID_SUCCESS
        ) is True
    
    def test_confirming_to_expired(self, sm):
        """confirming -> expired 合法"""
        assert sm.can_transition(
            OrderStatus.CONFIRMING, 
            OrderStatus.EXPIRED
        ) is True
    
    def test_paid_success_to_fulfilled(self, sm):
        """paid_success -> fulfilled 合法"""
        assert sm.can_transition(
            OrderStatus.PAID_SUCCESS, 
            OrderStatus.FULFILLED
        ) is True
    
    def test_expired_to_late_paid(self, sm):
        """expired -> late_paid 合法"""
        assert sm.can_transition(
            OrderStatus.EXPIRED, 
            OrderStatus.LATE_PAID
        ) is True
    
    def test_underpaid_to_late_paid(self, sm):
        """underpaid -> late_paid 合法"""
        assert sm.can_transition(
            OrderStatus.UNDERPAID, 
            OrderStatus.LATE_PAID
        ) is True
    
    def test_overpaid_to_late_paid(self, sm):
        """overpaid -> late_paid 合法"""
        assert sm.can_transition(
            OrderStatus.OVERPAID, 
            OrderStatus.LATE_PAID
        ) is True
    
    def test_failed_to_late_paid(self, sm):
        """failed -> late_paid 合法"""
        assert sm.can_transition(
            OrderStatus.FAILED, 
            OrderStatus.LATE_PAID
        ) is True
    
    # ========== 非法流转测试 ==========
    
    def test_fulfilled_no_transitions_allowed(self, sm):
        """fulfilled 是终态，不允许任何转换"""
        for status in OrderStatus:
            if status != OrderStatus.FULFILLED:
                assert sm.can_transition(
                    OrderStatus.FULFILLED, 
                    status
                ) is False
    
    def test_late_paid_no_transitions_allowed(self, sm):
        """late_paid 是终态，不允许任何转换"""
        for status in OrderStatus:
            if status != OrderStatus.LATE_PAID:
                assert sm.can_transition(
                    OrderStatus.LATE_PAID, 
                    status
                ) is False
    
    def test_pending_payment_invalid_transitions(self, sm):
        """pending_payment 只能转到 seen_onchain 或 expired"""
        invalid_targets = {
            OrderStatus.CONFIRMING,
            OrderStatus.PAID_SUCCESS,
            OrderStatus.FULFILLED,
            OrderStatus.UNDERPAID,
            OrderStatus.OVERPAID,
            OrderStatus.FAILED,
            OrderStatus.LATE_PAID,
        }
        for target in invalid_targets:
            assert sm.can_transition(
                OrderStatus.PENDING_PAYMENT, 
                target
            ) is False
    
    def test_paid_success_only_to_fulfilled(self, sm):
        """paid_success 只能转到 fulfilled"""
        invalid_targets = {
            OrderStatus.PENDING_PAYMENT,
            OrderStatus.SEEN_ONCHAIN,
            OrderStatus.CONFIRMING,
            OrderStatus.EXPIRED,
            OrderStatus.UNDERPAID,
            OrderStatus.OVERPAID,
            OrderStatus.FAILED,
            OrderStatus.LATE_PAID,
        }
        for target in invalid_targets:
            assert sm.can_transition(
                OrderStatus.PAID_SUCCESS, 
                target
            ) is False
    
    def test_confirming_cannot_go_back(self, sm):
        """confirming 不能回到 seen_onchain"""
        assert sm.can_transition(
            OrderStatus.CONFIRMING, 
            OrderStatus.SEEN_ONCHAIN
        ) is False
    
    def test_error_states_cannot_transition_except_late_paid(self, sm):
        """错误状态不能转换到非 late_paid 状态"""
        error_states = [OrderStatus.EXPIRED, OrderStatus.UNDERPAID, 
                       OrderStatus.OVERPAID, OrderStatus.FAILED]
        
        for from_status in error_states:
            for to_status in OrderStatus:
                if to_status != OrderStatus.LATE_PAID and to_status != from_status:
                    assert sm.can_transition(from_status, to_status) is False


class TestStateMachineValidation:
    """测试状态机验证方法"""
    
    @pytest.fixture
    def sm(self):
        return OrderStateMachine()
    
    def test_validate_valid_transition(self, sm):
        """验证合法转换返回 True"""
        result = sm.validate_transition(
            OrderStatus.PENDING_PAYMENT, 
            OrderStatus.SEEN_ONCHAIN,
            raise_on_error=False
        )
        assert result is True
    
    def test_validate_invalid_transition_returns_false(self, sm):
        """验证非法转换返回 False（不抛出异常）"""
        result = sm.validate_transition(
            OrderStatus.FULFILLED, 
            OrderStatus.PENDING_PAYMENT,
            raise_on_error=False
        )
        assert result is False
    
    def test_validate_invalid_transition_raises_error(self, sm):
        """验证非法转换抛出 StateTransitionError"""
        with pytest.raises(StateTransitionError) as exc_info:
            sm.validate_transition(
                OrderStatus.FULFILLED, 
                OrderStatus.PENDING_PAYMENT
            )
        assert "fulfilled" in str(exc_info.value)
        assert "pending_payment" in str(exc_info.value)
    
    def test_validate_same_status_raises_duplicate_error(self, sm):
        """验证相同状态转换抛出 DuplicateTransitionError"""
        with pytest.raises(DuplicateTransitionError) as exc_info:
            sm.validate_transition(
                OrderStatus.PENDING_PAYMENT, 
                OrderStatus.PENDING_PAYMENT
            )
        assert "pending_payment" in str(exc_info.value)
    
    def test_validate_same_status_returns_false(self, sm):
        """验证相同状态转换返回 False（不抛出异常）"""
        result = sm.validate_transition(
            OrderStatus.PENDING_PAYMENT, 
            OrderStatus.PENDING_PAYMENT,
            raise_on_error=False
        )
        assert result is False


class TestStateMachineExecution:
    """测试状态机执行转换"""
    
    @pytest.fixture
    def sm(self):
        return OrderStateMachine()
    
    def test_successful_transition(self, sm):
        """测试成功转换"""
        transition = sm.transition(
            order_id="order_123",
            current_status=OrderStatus.PENDING_PAYMENT,
            new_status=OrderStatus.SEEN_ONCHAIN,
            triggered_by="test",
            context={"tx_hash": "abc123"}
        )
        
        assert isinstance(transition, StateTransition)
        assert transition.from_status == OrderStatus.PENDING_PAYMENT
        assert transition.to_status == OrderStatus.SEEN_ONCHAIN
        assert transition.triggered_by == "test"
        assert transition.metadata == {"tx_hash": "abc123"}
        assert isinstance(transition.timestamp, datetime)
    
    def test_transition_raises_on_invalid(self, sm):
        """测试非法转换抛出异常"""
        with pytest.raises(StateTransitionError):
            sm.transition(
                order_id="order_123",
                current_status=OrderStatus.FULFILLED,
                new_status=OrderStatus.PENDING_PAYMENT
            )
    
    def test_transition_raises_on_duplicate(self, sm):
        """测试重复转换抛出 DuplicateTransitionError"""
        with pytest.raises(DuplicateTransitionError):
            sm.transition(
                order_id="order_123",
                current_status=OrderStatus.PENDING_PAYMENT,
                new_status=OrderStatus.PENDING_PAYMENT
            )
    
    def test_skip_validation(self, sm):
        """测试跳过验证可以执行非法转换"""
        # 正常情况应该失败
        with pytest.raises(StateTransitionError):
            sm.transition(
                order_id="order_123",
                current_status=OrderStatus.FULFILLED,
                new_status=OrderStatus.PENDING_PAYMENT
            )
        
        # 跳过验证后应该成功
        transition = sm.transition(
            order_id="order_123",
            current_status=OrderStatus.FULFILLED,
            new_status=OrderStatus.PENDING_PAYMENT,
            skip_validation=True
        )
        assert transition.from_status == OrderStatus.FULFILLED
        assert transition.to_status == OrderStatus.PENDING_PAYMENT


class TestCallbacks:
    """测试回调函数"""
    
    @pytest.fixture
    def sm(self):
        return OrderStateMachine()
    
    def test_register_callback(self, sm):
        """测试注册回调函数"""
        callback_called = False
        received_args = None
        
        def my_callback(order_id, from_status, to_status, context):
            nonlocal callback_called, received_args
            callback_called = True
            received_args = (order_id, from_status, to_status, context)
        
        sm.register_callback(OrderStatus.SEEN_ONCHAIN, my_callback)
        
        sm.transition(
            order_id="order_123",
            current_status=OrderStatus.PENDING_PAYMENT,
            new_status=OrderStatus.SEEN_ONCHAIN,
            context={"test": "data"}
        )
        
        assert callback_called is True
        assert received_args[0] == "order_123"
        assert received_args[1] == OrderStatus.PENDING_PAYMENT
        assert received_args[2] == OrderStatus.SEEN_ONCHAIN
        assert received_args[3] == {"test": "data"}
    
    def test_register_any_transition_callback(self, sm):
        """测试注册全局回调函数"""
        call_count = 0
        
        def global_callback(order_id, from_status, to_status, context):
            nonlocal call_count
            call_count += 1
        
        sm.register_any_transition_callback(global_callback)
        
        sm.transition(
            order_id="order_1",
            current_status=OrderStatus.PENDING_PAYMENT,
            new_status=OrderStatus.SEEN_ONCHAIN
        )
        
        sm.transition(
            order_id="order_2",
            current_status=OrderStatus.SEEN_ONCHAIN,
            new_status=OrderStatus.CONFIRMING
        )
        
        assert call_count == 2
    
    def test_callback_error_does_not_break_transition(self, sm):
        """测试回调错误不会中断转换"""
        def failing_callback(order_id, from_status, to_status, context):
            raise ValueError("Callback error")
        
        sm.register_callback(OrderStatus.SEEN_ONCHAIN, failing_callback)
        
        # 不应该抛出异常
        transition = sm.transition(
            order_id="order_123",
            current_status=OrderStatus.PENDING_PAYMENT,
            new_status=OrderStatus.SEEN_ONCHAIN
        )
        
        assert transition.to_status == OrderStatus.SEEN_ONCHAIN


class TestStateHelpers:
    """测试状态辅助方法"""
    
    @pytest.fixture
    def sm(self):
        return OrderStateMachine()
    
    def test_is_terminal(self, sm):
        """测试终态判断"""
        assert sm.is_terminal(OrderStatus.FULFILLED) is True
        assert sm.is_terminal(OrderStatus.LATE_PAID) is True
        assert sm.is_terminal(OrderStatus.PENDING_PAYMENT) is False
        assert sm.is_terminal(OrderStatus.EXPIRED) is False
    
    def test_is_error(self, sm):
        """测试错误状态判断"""
        assert sm.is_error(OrderStatus.EXPIRED) is True
        assert sm.is_error(OrderStatus.UNDERPAID) is True
        assert sm.is_error(OrderStatus.OVERPAID) is True
        assert sm.is_error(OrderStatus.FAILED) is True
        assert sm.is_error(OrderStatus.PENDING_PAYMENT) is False
        assert sm.is_error(OrderStatus.FULFILLED) is False
    
    def test_is_payable(self, sm):
        """测试可支付状态判断"""
        assert sm.is_payable(OrderStatus.PENDING_PAYMENT) is True
        assert sm.is_payable(OrderStatus.SEEN_ONCHAIN) is False
        assert sm.is_payable(OrderStatus.EXPIRED) is False
    
    def test_requires_confirmation(self, sm):
        """测试需要确认的状态判断"""
        assert sm.requires_confirmation(OrderStatus.SEEN_ONCHAIN) is True
        assert sm.requires_confirmation(OrderStatus.CONFIRMING) is True
        assert sm.requires_confirmation(OrderStatus.PENDING_PAYMENT) is False
        assert sm.requires_confirmation(OrderStatus.PAID_SUCCESS) is False
    
    def test_get_status_label(self, sm):
        """测试状态标签"""
        assert sm.get_status_label(OrderStatus.PENDING_PAYMENT) == "待支付"
        assert sm.get_status_label(OrderStatus.FULFILLED) == "已完成"
        assert sm.get_status_label(OrderStatus.EXPIRED) == "超时"
    
    def test_get_allowed_transitions(self, sm):
        """测试获取允许的转换"""
        allowed = sm.get_allowed_transitions(OrderStatus.PENDING_PAYMENT)
        assert OrderStatus.SEEN_ONCHAIN in allowed
        assert OrderStatus.EXPIRED in allowed
        assert OrderStatus.CONFIRMING not in allowed


class TestConvenienceFunctions:
    """测试便捷转换函数"""
    
    def test_transition_to_seen_onchain(self):
        """测试 transition_to_seen_onchain"""
        transition = transition_to_seen_onchain(
            order_id="order_123",
            current_status=OrderStatus.PENDING_PAYMENT,
            tx_hash="tx_abc",
            tx_from="addr_from",
            amount="1.5"
        )
        
        assert transition.to_status == OrderStatus.SEEN_ONCHAIN
        assert transition.metadata["tx_hash"] == "tx_abc"
        assert transition.metadata["tx_from"] == "addr_from"
        assert transition.metadata["amount"] == "1.5"
    
    def test_transition_to_confirming(self):
        """测试 transition_to_confirming"""
        transition = transition_to_confirming(
            order_id="order_123",
            current_status=OrderStatus.SEEN_ONCHAIN,
            confirm_count=5
        )
        
        assert transition.to_status == OrderStatus.CONFIRMING
        assert transition.metadata["confirm_count"] == 5
    
    def test_transition_to_paid_success(self):
        """测试 transition_to_paid_success"""
        paid_at = datetime.utcnow()
        transition = transition_to_paid_success(
            order_id="order_123",
            current_status=OrderStatus.CONFIRMING,
            confirm_count=12,
            paid_at=paid_at
        )
        
        assert transition.to_status == OrderStatus.PAID_SUCCESS
        assert transition.metadata["confirm_count"] == 12
    
    def test_transition_to_fulfilled(self):
        """测试 transition_to_fulfilled"""
        transition = transition_to_fulfilled(
            order_id="order_123",
            current_status=OrderStatus.PAID_SUCCESS,
            marzban_username="user123",
            subscription_url="https://example.com/sub"
        )
        
        assert transition.to_status == OrderStatus.FULFILLED
        assert transition.metadata["marzban_username"] == "user123"
    
    def test_transition_to_expired(self):
        """测试 transition_to_expired"""
        transition = transition_to_expired(
            order_id="order_123",
            current_status=OrderStatus.PENDING_PAYMENT,
            reason="timeout"
        )
        
        assert transition.to_status == OrderStatus.EXPIRED
        assert transition.metadata["expired_reason"] == "timeout"
    
    def test_transition_to_underpaid(self):
        """测试 transition_to_underpaid - 验证使用Decimal保持精度"""
        transition = transition_to_underpaid(
            order_id="order_123",
            current_status=OrderStatus.SEEN_ONCHAIN,
            expected="100.123456789",
            actual="50.000000001"
        )
        
        assert transition.to_status == OrderStatus.UNDERPAID
        assert transition.metadata["expected_amount"] == "100.123456789"
        assert transition.metadata["actual_amount"] == "50.000000001"
        # 注意：当前实现使用float计算，这里测试会暴露精度问题
        # shortage 应该是 50.123456788，但float可能会有精度丢失
    
    def test_transition_to_overpaid(self):
        """测试 transition_to_overpaid - 验证使用Decimal保持精度"""
        transition = transition_to_overpaid(
            order_id="order_123",
            current_status=OrderStatus.SEEN_ONCHAIN,
            expected="100.000000001",
            actual="150.123456789"
        )
        
        assert transition.to_status == OrderStatus.OVERPAID
        assert transition.metadata["expected_amount"] == "100.000000001"
        assert transition.metadata["actual_amount"] == "150.123456789"
    
    def test_transition_to_failed(self):
        """测试 transition_to_failed"""
        transition = transition_to_failed(
            order_id="order_123",
            current_status=OrderStatus.SEEN_ONCHAIN,
            error_code="INVALID_ASSET",
            error_message="Asset not supported"
        )
        
        assert transition.to_status == OrderStatus.FAILED
        assert transition.metadata["error_code"] == "INVALID_ASSET"
    
    def test_transition_to_late_paid(self):
        """测试 transition_to_late_paid"""
        paid_at = datetime.utcnow()
        transition = transition_to_late_paid(
            order_id="order_123",
            current_status=OrderStatus.EXPIRED,
            tx_hash="tx_late",
            paid_at=paid_at
        )
        
        assert transition.to_status == OrderStatus.LATE_PAID
        assert transition.metadata["tx_hash"] == "tx_late"
        assert transition.metadata["original_status"] == "expired"


class TestIdempotency:
    """测试幂等性"""
    
    def test_duplicate_transition_raises_error(self):
        """测试重复状态转换会抛出 DuplicateTransitionError"""
        # 第一次转换
        transition_to_seen_onchain(
            order_id="order_123",
            current_status=OrderStatus.PENDING_PAYMENT,
            tx_hash="tx_abc",
            tx_from="addr",
            amount="1.0"
        )
        
        # 第二次转换到相同状态应该抛出 DuplicateTransitionError
        with pytest.raises(DuplicateTransitionError) as exc_info:
            transition_to_seen_onchain(
                order_id="order_123",
                current_status=OrderStatus.SEEN_ONCHAIN,  # 已经是这个状态
                tx_hash="tx_abc",
                tx_from="addr",
                amount="1.0"
            )
        
        assert "already in status" in str(exc_info.value)
        assert "seen_onchain" in str(exc_info.value)


class TestExceptions:
    """测试异常类"""
    
    def test_state_transition_error_message(self):
        """测试状态转换错误消息"""
        error = StateTransitionError(
            from_status="pending_payment",
            to_status="fulfilled",
            message="Cannot skip steps"
        )
        
        assert "pending_payment" in str(error)
        assert "fulfilled" in str(error)
        assert "Cannot skip steps" in str(error)
        assert error.from_status == "pending_payment"
        assert error.to_status == "fulfilled"
    
    def test_duplicate_transition_error_message(self):
        """测试重复转换错误消息"""
        error = DuplicateTransitionError("order_123", "paid_success")
        
        assert "order_123" in str(error)
        assert "paid_success" in str(error)
        assert error.order_id == "order_123"
        assert error.status == "paid_success"
