"""
AuditLog model - 审计日志
"""
from sqlalchemy import Column, String, DateTime, Text, func, Enum as SQLEnum
import enum

from app.core.database import Base


class OperatorType(str, enum.Enum):
    SYSTEM = "system"
    ADMIN = "admin"
    WORKER = "worker"
    CLIENT = "client"


class AuditLog(Base):
    __tablename__ = "audit_logs"
    
    id = Column(String(32), primary_key=True)
    
    # 实体信息
    entity_type = Column(String(32), nullable=False, index=True)  # order, payment, user
    entity_id = Column(String(32), nullable=False, index=True)
    
    # 操作
    action = Column(String(64), nullable=False)  # created, updated, paid, fulfilled, etc.
    
    # 操作者
    operator_type = Column(SQLEnum(OperatorType), nullable=False)
    operator_id = Column(String(64), nullable=True)  # user_id, admin_id, worker_name
    
    # 详情
    payload_json = Column(Text, nullable=True)  # JSON 格式存储详情
    
    # IP 信息（客户端操作时）
    client_ip = Column(String(64), nullable=True)
    user_agent = Column(String(512), nullable=True)
    
    # 时间戳
    created_at = Column(DateTime(timezone=True), server_default=func.now(), index=True)
    
    def __repr__(self):
        return f"<AuditLog {self.entity_type}:{self.entity_id} {self.action}>"
