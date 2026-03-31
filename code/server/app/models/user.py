"""
User model - 用户模型
"""
from sqlalchemy import Column, String, Boolean, DateTime, func
from sqlalchemy.orm import relationship

from app.core.database import Base


class User(Base):
    __tablename__ = "users"
    
    # 主键
    id = Column(String(32), primary_key=True)  # ULID
    
    # 登录信息
    username = Column(String(64), unique=True, nullable=False, index=True)
    password_hash = Column(String(256), nullable=False)
    
    # 可选信息
    email = Column(String(256), nullable=True, index=True)
    
    # 状态
    is_active = Column(Boolean, default=True, nullable=False)
    
    # 时间戳
    created_at = Column(DateTime(timezone=True), server_default=func.now())
    updated_at = Column(DateTime(timezone=True), onupdate=func.now())
    
    # 关系
    orders = relationship("Order", back_populates="user")
    client_sessions = relationship("ClientSession", back_populates="user")
    
    def __repr__(self):
        return f"<User {self.username}: {'active' if self.is_active else 'inactive'}>"
