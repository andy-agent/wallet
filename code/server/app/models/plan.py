"""
Plan model - 套餐
"""
from sqlalchemy import Column, String, Integer, BigInteger, Numeric, Boolean, DateTime, func
from sqlalchemy.dialects.postgresql import ARRAY
from sqlalchemy.orm import relationship

from app.core.database import Base


class Plan(Base):
    __tablename__ = "plans"
    
    id = Column(String(32), primary_key=True)
    code = Column(String(32), unique=True, nullable=False, index=True)
    name = Column(String(128), nullable=False)
    description = Column(String(512), default="")
    
    # 套餐内容
    traffic_bytes = Column(BigInteger, nullable=False)
    duration_days = Column(Integer, nullable=False)
    price_usd = Column(Numeric(10, 2), nullable=False)
    
    # 支持的支付方式
    supported_assets = Column(ARRAY(String(20)), default=["SOL", "USDT_TRC20"])
    
    # 状态
    enabled = Column(Boolean, default=True, index=True)
    sort_order = Column(Integer, default=0)
    
    # 时间戳
    created_at = Column(DateTime(timezone=True), server_default=func.now())
    updated_at = Column(DateTime(timezone=True), onupdate=func.now())
    
    # 关系
    orders = relationship("Order", back_populates="plan")
    
    def __repr__(self):
        return f"<Plan {self.code}: {self.name}>"
