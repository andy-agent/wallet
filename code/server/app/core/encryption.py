"""
Encryption utilities for sensitive data

使用 Fernet 对称加密来保护私钥等敏感信息。
"""
import base64
import hashlib
import logging
from typing import Optional

from cryptography.fernet import Fernet
from cryptography.hazmat.primitives import hashes
from cryptography.hazmat.primitives.kdf.pbkdf2 import PBKDF2HMAC

from app.core.config import get_settings

logger = logging.getLogger(__name__)

# 缓存 Fernet 实例
_fernet_instance: Optional[Fernet] = None


def _get_fernet() -> Fernet:
    """获取或创建 Fernet 实例（使用 master key）"""
    global _fernet_instance
    
    if _fernet_instance is None:
        settings = get_settings()
        master_key = settings.encryption_master_key
        
        if not master_key:
            raise ValueError("ENCRYPTION_MASTER_KEY is not configured")
        
        # 使用 PBKDF2 从 master key 派生密钥
        # 注意：在生产环境中，salt 应该固定或安全存储
        kdf = PBKDF2HMAC(
            algorithm=hashes.SHA256(),
            length=32,
            salt=hashlib.sha256(b"payment_bridge_salt_v1").digest()[:16],
            iterations=100000,
        )
        key = base64.urlsafe_b64encode(kdf.derive(master_key.encode()))
        _fernet_instance = Fernet(key)
    
    return _fernet_instance


def clear_fernet_cache():
    """清除 Fernet 缓存（用于测试）"""
    global _fernet_instance
    _fernet_instance = None


def encrypt_private_key(private_key: str) -> str:
    """
    加密私钥
    
    Args:
        private_key: 原始私钥字符串
        
    Returns:
        加密后的 base64 字符串
    """
    try:
        fernet = _get_fernet()
        encrypted = fernet.encrypt(private_key.encode())
        return base64.urlsafe_b64encode(encrypted).decode()
    except Exception as e:
        logger.error(f"Failed to encrypt private key: {e}")
        raise


def decrypt_private_key(encrypted_key: str) -> str:
    """
    解密私钥
    
    Args:
        encrypted_key: 加密的私钥（base64 编码）
        
    Returns:
        原始私钥字符串
    """
    try:
        fernet = _get_fernet()
        # 先解码 base64 得到 Fernet 加密的数据
        encrypted_data = base64.urlsafe_b64decode(encrypted_key.encode())
        # 再用 Fernet 解密
        decrypted = fernet.decrypt(encrypted_data)
        return decrypted.decode()
    except Exception as e:
        logger.error(f"Failed to decrypt private key: {e}")
        raise


def generate_encryption_key() -> str:
    """
    生成新的 Fernet 密钥
    
    Returns:
        新的密钥字符串（可用于 ENCRYPTION_MASTER_KEY）
    """
    return Fernet.generate_key().decode()


# 兼容性别名
encrypt_sensitive = encrypt_private_key
decrypt_sensitive = decrypt_private_key
