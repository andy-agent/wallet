"""
PaymentAddress model - 收款地址池
"""
from sqlalchemy import Column, String, Integer, DateTime, ForeignKey, func
import enum

from app.core.database import Base


class AddressStatus(str, enum.Enum):
    AVAILABLE = "available"    # 可用
    ALLOCATED = "allocated"    # 已分配
    EXPIRED = "expired"        # 过期
    SWEPT = "swept"           # 已归集
    DISABLED = "disabled"      # 禁用


class PaymentAddress(Base):
    __tablename__ = "payment_addresses"
    
    id = Column(Integer, primary_key=True)
    
    # 链信息
    chain = Column(String(20), nullable=False, index=True)  # solana, tron
    asset_code = Column(String(20), nullable=False)  # SOL, USDT_TRC20
    
    # 地址
    address = Column(String(64), unique=True, nullable=False, index=True)
    encrypted_private_key = Column(String(512), nullable=True)
    
    # 状态 (使用 String 类型存储枚举值)
    status = Column(String(20), default=AddressStatus.AVAILABLE.value, index=True)
    
    # 分配信息
    allocated_order_id = Column(String(32), ForeignKey("orders.id"), nullable=True)
    allocated_at = Column(DateTime(timezone=True), nullable=True)
    
    # 最后交易
    last_seen_tx_hash = Column(String(128), nullable=True)
    
    # 时间戳
    created_at = Column(DateTime(timezone=True), server_default=func.now())
    updated_at = Column(DateTime(timezone=True), onupdate=func.now())
    
    def __repr__(self):
        return f"<PaymentAddress {self.chain}:{self.address[:8]}... status={self.status}>"
