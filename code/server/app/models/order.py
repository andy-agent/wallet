"""
Order model - 订单
"""
from sqlalchemy import Column, String, Integer, BigInteger, Numeric, DateTime, ForeignKey, func, Index
from sqlalchemy.orm import relationship

from app.core.database import Base


class Order(Base):
    __tablename__ = "orders"
    
    # 主键
    id = Column(String(32), primary_key=True)  # ULID
    order_no = Column(String(32), unique=True, nullable=False, index=True)  # 展示用
    
    # 购买类型
    purchase_type = Column(String(10), nullable=False)  # new, renew
    
    # 关联
    plan_id = Column(String(32), ForeignKey("plans.id"), nullable=False)
    user_id = Column(String(32), ForeignKey("users.id"), nullable=False)  # 关联用户
    client_user_id = Column(String(32), nullable=True)  # 续费时关联
    marzban_username = Column(String(64), nullable=True, index=True)
    
    # 支付信息
    chain = Column(String(20), nullable=False)  # solana, tron
    asset_code = Column(String(20), nullable=False)  # SOL, USDT_TRC20
    receive_address = Column(String(64), nullable=False, index=True)
    amount_crypto = Column(Numeric(36, 18), nullable=False)  # 字符串存高精度
    amount_usd_locked = Column(Numeric(10, 2), nullable=False)
    fx_rate_locked = Column(Numeric(18, 8), nullable=False)
    
    # 状态（10个状态）
    status = Column(String(20), nullable=False, default="pending_payment", index=True)
    expires_at = Column(DateTime(timezone=True), nullable=False)
    
    # 链上信息
    tx_hash = Column(String(128), nullable=True, unique=True)
    tx_from = Column(String(64), nullable=True)
    confirm_count = Column(Integer, default=0)
    paid_at = Column(DateTime(timezone=True), nullable=True)
    confirmed_at = Column(DateTime(timezone=True), nullable=True)
    fulfilled_at = Column(DateTime(timezone=True), nullable=True)
    
    # 客户端信息
    client_version = Column(String(16), nullable=False)
    
    # 错误信息
    error_code = Column(String(32), nullable=True)
    error_message = Column(String(256), nullable=True)
    
    # 时间戳
    created_at = Column(DateTime(timezone=True), server_default=func.now())
    updated_at = Column(DateTime(timezone=True), onupdate=func.now())
    
    # 关系
    plan = relationship("Plan", back_populates="orders")
    user = relationship("User", back_populates="orders")
    
    def __repr__(self):
        return f"<Order {self.order_no}: {self.status}>"


# 复合索引
Index("idx_orders_user_plan_created", 
      Order.user_id, Order.plan_id, Order.created_at,
      postgresql_where=Order.status.in_(["pending_payment"]))

Index("idx_orders_status_created",
      Order.status, Order.created_at)

Index("idx_orders_fulfilled",
      Order.id, unique=True,
      postgresql_where=Order.status == "fulfilled")
