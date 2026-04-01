"""
SweepRecord model - 资金归集记录
"""
import enum
from sqlalchemy import Column, String, Integer, Numeric, DateTime, ForeignKey, func, Index

from app.core.database import Base


class SweepStatus(str, enum.Enum):
    PENDING = "pending"      # 待处理
    PROCESSING = "processing"  # 处理中
    COMPLETED = "completed"   # 已完成
    FAILED = "failed"        # 失败
    RETRYING = "retrying"    # 重试中


class SweepRecord(Base):
    __tablename__ = "sweep_records"
    
    id = Column(Integer, primary_key=True)
    
    # 关联信息
    address_id = Column(Integer, ForeignKey("payment_addresses.id"), nullable=False, index=True)
    order_id = Column(String(32), ForeignKey("orders.id"), nullable=True, index=True)
    
    # 地址信息
    chain = Column(String(20), nullable=False, index=True)  # solana, tron
    asset_code = Column(String(20), nullable=False)  # SOL, USDT_TRC20
    from_address = Column(String(64), nullable=False, index=True)
    to_address = Column(String(64), nullable=False)  # 主钱包地址
    
    # 金额信息
    amount = Column(Numeric(36, 18), nullable=False)  # 归集金额（加密货币单位）
    amount_usd = Column(Numeric(10, 2), nullable=True)  # 对应的美元价值
    
    # 手续费信息
    fee_amount = Column(Numeric(36, 18), nullable=True)  # 实际支付的手续费
    fee_asset = Column(String(20), nullable=True)  # 手续费代币（SOL/TRX）
    
    # 交易信息
    tx_hash = Column(String(128), nullable=True, unique=True, index=True)
    
    # 状态
    status = Column(String(20), default=SweepStatus.PENDING.value, nullable=False, index=True)
    retry_count = Column(Integer, default=0)  # 重试次数
    error_message = Column(String(512), nullable=True)  # 失败原因
    
    # 时间戳
    created_at = Column(DateTime(timezone=True), server_default=func.now())
    started_at = Column(DateTime(timezone=True), nullable=True)  # 开始处理时间
    completed_at = Column(DateTime(timezone=True), nullable=True)  # 完成时间
    
    def __repr__(self):
        return f"<SweepRecord {self.chain}:{self.from_address[:8]}... -> {self.to_address[:8]}... status={self.status}>"


# 复合索引
Index("idx_sweep_records_status_created",
      SweepRecord.status, SweepRecord.created_at)

Index("idx_sweep_records_address_status",
      SweepRecord.address_id, SweepRecord.status)

Index("idx_sweep_records_pending",
      SweepRecord.id, unique=True,
      postgresql_where=SweepRecord.status.in_(["pending", "retrying"]))
