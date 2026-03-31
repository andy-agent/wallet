"""
Marzban API Client 封装

提供与 Marzban VPN 面板的 API 集成，包括用户管理、订阅生成等功能。
"""
import asyncio
from dataclasses import dataclass
from datetime import datetime
from typing import Optional, Any
import logging

import httpx

logger = logging.getLogger(__name__)


@dataclass
class User:
    """Marzban 用户数据模型"""
    username: str
    status: str  # active, limited, expired, disabled
    expire: Optional[int]  # 时间戳（秒）
    data_limit: Optional[int]  # 字节
    used_traffic: int
    subscription_url: str
    created_at: datetime


class MarzbanAPIError(Exception):
    """Marzban API 调用异常"""
    def __init__(self, message: str, status_code: Optional[int] = None, response: Optional[Any] = None):
        super().__init__(message)
        self.message = message
        self.status_code = status_code
        self.response = response


class MarzbanClient:
    """
    Marzban API 客户端
    
    支持异步操作，自动处理 token 认证和刷新。
    """
    
    def __init__(
        self,
        base_url: str,
        username: str,
        password: str
    ):
        """
        初始化 Marzban 客户端
        
        Args:
            base_url: Marzban 面板地址，如 https://marzban.example.com
            username: 管理员用户名
            password: 管理员密码
        """
        self.base_url = base_url.rstrip('/')
        self.username = username
        self.password = password
        self._token: Optional[str] = None
        self._token_lock = asyncio.Lock()
        self._client: Optional[httpx.AsyncClient] = None
    
    async def _get_client(self) -> httpx.AsyncClient:
        """获取或创建 HTTP 客户端"""
        if self._client is None or self._client.is_closed:
            self._client = httpx.AsyncClient(
                base_url=self.base_url,
                timeout=30.0,
                headers={"Accept": "application/json"}
            )
        return self._client
    
    async def _close_client(self):
        """关闭 HTTP 客户端"""
        if self._client and not self._client.is_closed:
            await self._client.aclose()
            self._client = None
    
    async def authenticate(self) -> str:
        """
        获取管理员 Token
        
        Returns:
            访问令牌字符串
            
        Raises:
            MarzbanAPIError: 认证失败
        """
        client = await self._get_client()
        
        try:
            response = await client.post(
                "/api/admin/token",
                data={
                    "grant_type": "password",
                    "username": self.username,
                    "password": self.password,
                    "scope": "",
                    "client_id": "",
                    "client_secret": ""
                },
                headers={"Content-Type": "application/x-www-form-urlencoded"}
            )
            response.raise_for_status()
            data = response.json()
            
            if "access_token" not in data:
                raise MarzbanAPIError(
                    "Invalid response: access_token not found",
                    status_code=response.status_code,
                    response=data
                )
            
            self._token = data["access_token"]
            logger.info(f"Marzban authentication successful for user: {self.username}")
            return self._token
            
        except httpx.HTTPStatusError as e:
            logger.error(f"Marzban authentication failed: {e.response.text}")
            raise MarzbanAPIError(
                f"Authentication failed: {e.response.text}",
                status_code=e.response.status_code,
                response=e.response.text
            )
        except httpx.RequestError as e:
            logger.error(f"Marzban connection error: {str(e)}")
            raise MarzbanAPIError(f"Connection error: {str(e)}")
    
    async def _ensure_token(self) -> str:
        """确保 token 有效，如无效则重新获取"""
        async with self._token_lock:
            if self._token is None:
                return await self.authenticate()
            return self._token
    
    def _get_auth_headers(self) -> dict:
        """获取认证请求头"""
        if not self._token:
            raise MarzbanAPIError("Not authenticated. Call authenticate() first.")
        return {
            "Authorization": f"Bearer {self._token}",
            "Accept": "application/json",
            "Content-Type": "application/json"
        }
    
    async def _request(
        self,
        method: str,
        path: str,
        **kwargs
    ) -> Any:
        """
        发送带认证的 HTTP 请求
        
        自动处理 token 刷新和重试
        """
        await self._ensure_token()
        client = await self._get_client()
        
        headers = self._get_auth_headers()
        if "headers" in kwargs:
            headers.update(kwargs.pop("headers"))
        
        try:
            response = await client.request(
                method=method,
                url=path,
                headers=headers,
                **kwargs
            )
            
            # Token 过期，尝试刷新并重试
            if response.status_code == 401:
                logger.warning("Marzban token expired, refreshing...")
                await self.authenticate()
                headers = self._get_auth_headers()
                response = await client.request(
                    method=method,
                    url=path,
                    headers=headers,
                    **kwargs
                )
            
            response.raise_for_status()
            
            # 处理空响应
            if response.status_code == 204 or not response.content:
                return None
                
            return response.json()
            
        except httpx.HTTPStatusError as e:
            logger.error(f"Marzban API error: {e.response.text}")
            raise MarzbanAPIError(
                f"API error: {e.response.text}",
                status_code=e.response.status_code,
                response=e.response.text
            )
        except httpx.RequestError as e:
            logger.error(f"Marzban request error: {str(e)}")
            raise MarzbanAPIError(f"Request error: {str(e)}")
    
    def _parse_user(self, data: dict) -> User:
        """解析用户数据"""
        # 处理 created_at 字段
        created_at = datetime.utcnow()
        if "created_at" in data and data["created_at"]:
            try:
                created_at = datetime.fromisoformat(data["created_at"].replace('Z', '+00:00'))
            except (ValueError, AttributeError):
                pass
        
        # 构建订阅链接
        subscription_url = ""
        if "subscription_url" in data and data["subscription_url"]:
            subscription_url = data["subscription_url"]
        elif "token" in data and data["token"]:
            subscription_url = f"{self.base_url}/sub/{data['token']}"
        
        return User(
            username=data.get("username", ""),
            status=data.get("status", "disabled"),
            expire=data.get("expire"),
            data_limit=data.get("data_limit"),
            used_traffic=data.get("used_traffic", 0),
            subscription_url=subscription_url,
            created_at=created_at
        )
    
    async def create_user(
        self,
        username: str,
        expire: Optional[int] = None,
        data_limit: Optional[int] = None,
        proxies: Optional[dict] = None
    ) -> User:
        """
        创建新用户
        
        Args:
            username: 用户名（唯一标识）
            expire: 过期时间戳（秒），None 表示永不过期
            data_limit: 流量限制（字节），None 表示无限制
            proxies: 代理配置，默认启用 vmess 和 vless
            
        Returns:
            创建的用户对象
            
        Raises:
            MarzbanAPIError: 创建失败（如用户名已存在）
        """
        # 默认代理配置
        if proxies is None:
            proxies = {
                "vmess": {},
                "vless": {}
            }
        
        payload = {
            "username": username,
            "proxies": proxies,
            "inbounds": {
                "vmess": ["VMess TCP"],
                "vless": ["VLESS TCP XTLS"]
            }
        }
        
        if expire is not None:
            payload["expire"] = expire
        
        if data_limit is not None:
            payload["data_limit"] = data_limit
        
        logger.info(f"Creating Marzban user: {username}")
        data = await self._request("POST", "/api/user", json=payload)
        user = self._parse_user(data)
        logger.info(f"Marzban user created: {username}, status: {user.status}")
        return user
    
    async def get_user(self, username: str) -> Optional[User]:
        """
        获取用户信息
        
        Args:
            username: 用户名
            
        Returns:
            用户对象，如不存在则返回 None
        """
        try:
            data = await self._request("GET", f"/api/user/{username}")
            return self._parse_user(data)
        except MarzbanAPIError as e:
            if e.status_code == 404:
                return None
            raise
    
    async def modify_user(
        self,
        username: str,
        expire: Optional[int] = None,
        data_limit: Optional[int] = None,
        status: Optional[str] = None
    ) -> User:
        """
        修改用户信息
        
        Args:
            username: 用户名
            expire: 新的过期时间戳（秒）
            data_limit: 新的流量限制（字节）
            status: 用户状态（active, limited, expired, disabled）
            
        Returns:
            更新后的用户对象
        """
        payload: dict = {}
        
        if expire is not None:
            payload["expire"] = expire
        
        if data_limit is not None:
            payload["data_limit"] = data_limit
        
        if status is not None:
            payload["status"] = status
        
        if not payload:
            # 无更新内容，直接返回当前用户
            user = await self.get_user(username)
            if user is None:
                raise MarzbanAPIError(f"User not found: {username}", status_code=404)
            return user
        
        logger.info(f"Modifying Marzban user: {username}, fields: {list(payload.keys())}")
        data = await self._request("PUT", f"/api/user/{username}", json=payload)
        user = self._parse_user(data)
        logger.info(f"Marzban user modified: {username}, status: {user.status}")
        return user
    
    async def delete_user(self, username: str) -> bool:
        """
        删除用户
        
        Args:
            username: 用户名
            
        Returns:
            是否删除成功
        """
        try:
            await self._request("DELETE", f"/api/user/{username}")
            logger.info(f"Marzban user deleted: {username}")
            return True
        except MarzbanAPIError as e:
            if e.status_code == 404:
                return False
            raise
    
    async def reset_user_traffic(self, username: str) -> User:
        """
        重置用户流量
        
        Args:
            username: 用户名
            
        Returns:
            更新后的用户对象
        """
        logger.info(f"Resetting traffic for Marzban user: {username}")
        data = await self._request("POST", f"/api/user/{username}/reset")
        return self._parse_user(data)
    
    async def revoke_user_subscription(self, username: str) -> User:
        """
        撤销用户订阅（重新生成 token）
        
        Args:
            username: 用户名
            
        Returns:
            更新后的用户对象
        """
        logger.info(f"Revoking subscription for Marzban user: {username}")
        data = await self._request("POST", f"/api/user/{username}/revoke_sub")
        return self._parse_user(data)
    
    async def get_subscription_url(self, username: str) -> str:
        """
        获取用户订阅链接
        
        Args:
            username: 用户名
            
        Returns:
            订阅链接 URL
        """
        user = await self.get_user(username)
        if user is None:
            raise MarzbanAPIError(f"User not found: {username}", status_code=404)
        return user.subscription_url
    
    async def get_subscription_content(
        self,
        username: str,
        token: str
    ) -> str:
        """
        获取订阅内容（原始配置）
        
        Args:
            username: 用户名（用于日志，不参与实际请求）
            token: 订阅 token
            
        Returns:
            订阅配置内容（base64 编码或 plain text）
        """
        client = await self._get_client()
        
        try:
            response = await client.get(
                f"/sub/{token}",
                headers={
                    "Accept": "text/plain, application/json",
                    "User-Agent": "v2rayng-payment-bridge/1.0"
                }
            )
            response.raise_for_status()
            logger.debug(f"Got subscription content for user: {username}")
            return response.text
            
        except httpx.HTTPStatusError as e:
            logger.error(f"Failed to get subscription content: {e.response.text}")
            raise MarzbanAPIError(
                f"Failed to get subscription: {e.response.text}",
                status_code=e.response.status_code
            )
        except httpx.RequestError as e:
            logger.error(f"Subscription request error: {str(e)}")
            raise MarzbanAPIError(f"Request error: {str(e)}")
    
    async def get_users(
        self,
        offset: int = 0,
        limit: int = 100
    ) -> list[User]:
        """
        获取用户列表
        
        Args:
            offset: 分页偏移
            limit: 分页大小
            
        Returns:
            用户对象列表
        """
        params = {"offset": offset, "limit": limit}
        data = await self._request("GET", "/api/users", params=params)
        
        users = []
        for user_data in data.get("users", []):
            users.append(self._parse_user(user_data))
        
        return users
    
    async def get_system_stats(self) -> dict:
        """
        获取系统统计信息
        
        Returns:
            系统统计字典
        """
        return await self._request("GET", "/api/system/stats")
    
    async def close(self):
        """关闭客户端连接"""
        await self._close_client()
    
    async def __aenter__(self):
        """异步上下文管理器入口"""
        return self
    
    async def __aexit__(self, exc_type, exc_val, exc_tb):
        """异步上下文管理器出口"""
        await self.close()


# 便捷函数：从配置创建客户端
async def get_marzban_client(
    base_url: Optional[str] = None,
    username: Optional[str] = None,
    password: Optional[str] = None
) -> MarzbanClient:
    """
    从配置创建 Marzban 客户端
    
    如果参数未提供，则从应用配置中读取。
    
    Args:
        base_url: Marzban 面板地址
        username: 管理员用户名
        password: 管理员密码
        
    Returns:
        配置好的 MarzbanClient 实例
    """
    # 延迟导入以避免循环依赖
    from app.core.config import get_settings
    
    settings = get_settings()
    
    return MarzbanClient(
        base_url=base_url or settings.marzban_base_url,
        username=username or settings.marzban_admin_username,
        password=password or settings.marzban_admin_password
    )
