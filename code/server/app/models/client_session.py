"""
ClientSession model - 客户端会话
"""
from sqlalchemy import Column, String, DateTime, ForeignKey, func
from sqlalchemy.orm import relationship

from app.core.database import Base


class ClientSession(Base):
    __tablename__ = "client_sessions"
    
    id = Column(String(32), primary_key=True)
    
    # 关联
    order_id = Column(String(32), ForeignKey("orders.id"), nullable=True)  # 可选，创建订单后关联
    user_id = Column(String(32), ForeignKey("users.id"), nullable=False)  # 关联用户
    marzban_username = Column(String(64), nullable=True, index=True)  # 可选，订单完成后设置
    
    # Token
    access_token = Column(String(512), nullable=False)
    refresh_token = Column(String(512), nullable=False)
    expires_at = Column(DateTime(timezone=True), nullable=False, index=True)
    
    # 状态
    revoked_at = Column(DateTime(timezone=True), nullable=True)
    
    # 时间戳
    created_at = Column(DateTime(timezone=True), server_default=func.now())
    
    # 关系
    user = relationship("User", back_populates="client_sessions")
    
    def __repr__(self):
        return f"<ClientSession {self.user_id}: {self.expires_at}>"
    
    @property
    def is_active(self):
        from datetime import datetime
        return self.revoked_at is None and self.expires_at > datetime.utcnow()
