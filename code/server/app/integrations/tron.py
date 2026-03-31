"""
Tron chain integration module for TRC20 USDT monitoring
支持 Mock 模式用于测试
"""
from dataclasses import dataclass
from datetime import datetime, timezone
from typing import List, Optional
import asyncio
import logging

import httpx

logger = logging.getLogger(__name__)

# USDT TRC20 合约地址
USDT_CONTRACT_MAINNET = "TR7NHqjeKQxGTCi8q8ZY4pL8otSzgjLj6t"
USDT_CONTRACT_NILE = "TXYZopYRdj2D9XRtbG411XZZ3kpm5bGnNF"

# TRC20 Transfer 事件签名
transfer_event_signature = "0xddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef"


@dataclass
class TRC20Transfer:
    """TRC20 转账记录"""
    transaction_id: str
    from_address: str
    to_address: str
    amount: float  # 已转换精度（6位小数）
    token_contract: str
    confirmations: int
    timestamp: datetime


@dataclass
class PaymentDetectionResult:
    """支付检测结果"""
    found: bool
    tx_hash: Optional[str] = None
    from_address: Optional[str] = None
    amount: Optional[float] = None
    confirmations: int = 0
    status: str = "pending"  # pending, confirmed, failed


class TronClient:
    """
    Tron 链上客户端
    
    支持功能：
    - 查询 TRC20 余额
    - 查询 TRC20 转账记录
    - 检测 USDT 到账
    - Mock 模式用于测试
    """
    
    def __init__(
        self,
        rpc_url: str,
        usdt_contract: str,
        mock_mode: bool = False
    ):
        self.rpc_url = rpc_url.rstrip('/')
        self.usdt_contract = usdt_contract
        self.mock_mode = mock_mode
        self.decimals = 6  # USDT TRC20 是 6 位小数
        
        if not mock_mode:
            self.client = httpx.AsyncClient(
                base_url=self.rpc_url,
                timeout=30.0,
                headers={
                    "Accept": "application/json",
                    "Content-Type": "application/json"
                }
            )
        else:
            self.client = None
            self._mock_transfers: List[TRC20Transfer] = []
            self._mock_balances: dict[str, float] = {}
        
        logger.info(f"TronClient initialized (mock={mock_mode}, contract={usdt_contract})")
    
    def _from_raw_amount(self, raw_amount: int) -> float:
        """将原始金额（带精度）转换为可读金额"""
        return raw_amount / (10 ** self.decimals)
    
    def _to_raw_amount(self, amount: float) -> int:
        """将可读金额转换为原始金额（带精度）"""
        return int(amount * (10 ** self.decimals))
    
    async def close(self):
        """关闭客户端连接"""
        if self.client and not self.mock_mode:
            await self.client.aclose()
    
    async def __aenter__(self):
        """异步上下文管理器入口"""
        return self
    
    async def __aexit__(self, exc_type, exc_val, exc_tb):
        """异步上下文管理器出口"""
        await self.close()
    
    async def get_trc20_balance(
        self,
        address: str,
        contract: str = None
    ) -> float:
        """
        查询 TRC20 代币余额
        
        Args:
            address: 钱包地址
            contract: 合约地址，默认使用 USDT 合约
            
        Returns:
            float: 代币余额（已转换精度）
            
        Raises:
            httpx.HTTPError: RPC 请求失败时抛出
        """
        if self.mock_mode:
            return self._mock_balances.get(address, 0.0)
        
        contract = contract or self.usdt_contract
        
        # 使用 TronGrid API 查询账户信息
        # 注意：Nile 测试网可能需要特殊处理
        url = f"/v1/accounts/{address}"
        
        try:
            response = await self.client.get(url)
            response.raise_for_status()
            data = response.json()
            
            # 处理 TronGrid v1 API 响应
            if not data.get("success", True):
                error_msg = data.get("error", "Unknown RPC error")
                logger.warning(f"RPC returned error for account {address}: {error_msg}")
                if "address not found" in error_msg.lower():
                    return 0.0
                # 对于其他错误，返回 0 而不是抛出异常
                return 0.0
            
            # 查找指定合约的余额
            account_data = data.get("data", [])
            if not account_data:
                return 0.0
                
            for trc20 in account_data[0].get("trc20", []):
                if contract in trc20:
                    raw_balance = int(trc20[contract])
                    return self._from_raw_amount(raw_balance)
            
            return 0.0
            
        except httpx.HTTPStatusError as e:
            # 400 错误通常表示地址格式错误或地址不存在
            if e.response.status_code == 400:
                logger.warning(f"Address not found or invalid: {address}")
                return 0.0
            raise
        except Exception as e:
            logger.error(f"Error getting TRC20 balance: {e}")
            raise
    
    async def _get_latest_block_number(self) -> Optional[int]:
        """
        获取当前最新区块高度
        
        Returns:
            Optional[int]: 最新区块高度，获取失败返回 None
        """
        try:
            response = await self.client.get("/walletsolidity/getnowblock")
            response.raise_for_status()
            
            data = response.json()
            block_header = data.get("block_header", {})
            raw_data = block_header.get("raw_data", {})
            block_number = raw_data.get("number")
            
            if block_number is not None:
                return int(block_number)
            return None
            
        except Exception as e:
            logger.error(f"Error getting latest block number: {e}")
            return None
    
    async def get_trc20_transfers(
        self,
        to_address: str,
        limit: int = 10
    ) -> List[TRC20Transfer]:
        """
        查询指定地址的 TRC20 转账记录
        
        Args:
            to_address: 接收地址
            limit: 返回记录数量限制
            
        Returns:
            List[TRC20Transfer]: 转账记录列表
        """
        if self.mock_mode:
            # Mock 模式下返回模拟的转账记录
            return [
                t for t in self._mock_transfers
                if t.to_address.lower() == to_address.lower()
            ][:limit]
        
        try:
            # 获取当前最新区块高度用于计算确认数
            latest_block = await self._get_latest_block_number()
            
            # 使用 TronGrid API 查询指定地址的交易
            # 尝试两种方式：1. 通过 accounts 端点 2. 通过 contracts 端点
            transfers = []
            
            # 方法 1: 查询账户的交易历史
            url = f"/v1/accounts/{to_address}/transactions/trc20"
            params = {
                "limit": limit,
                "contract_address": self.usdt_contract,
            }
            
            try:
                response = await self.client.get(url, params=params)
                response.raise_for_status()
                data = response.json()
                
                if data.get("success") and data.get("data"):
                    transfers = self._parse_transfers(data["data"], to_address, latest_block)
            except httpx.HTTPStatusError as e:
                if e.response.status_code == 400:
                    # 地址不存在或格式错误，返回空列表
                    logger.warning(f"Address not found or invalid: {to_address}")
                    return []
                # 其他 HTTP 错误，尝试方法 2
                logger.warning(f"Failed to get account transactions, trying contract endpoint: {e}")
            
            # 如果方法 1 没有结果，尝试方法 2
            if not transfers:
                url = f"/v1/contracts/{self.usdt_contract}/transactions"
                params = {
                    "limit": limit * 3,
                    "event_name": "Transfer",
                }
                
                response = await self.client.get(url, params=params)
                response.raise_for_status()
                data = response.json()
                
                if data.get("success") and data.get("data"):
                    transfers = self._parse_transfers(data["data"], to_address, latest_block)
            
            return transfers[:limit]
            
        except httpx.HTTPStatusError as e:
            if e.response.status_code == 400:
                # 400 错误通常表示地址问题，返回空列表
                logger.warning(f"Bad request for address {to_address}: {e}")
                return []
            raise
        except Exception as e:
            logger.error(f"Error getting TRC20 transfers: {e}")
            raise
    
    def _parse_transfers(
        self,
        tx_data: list,
        to_address: str,
        latest_block: Optional[int]
    ) -> List[TRC20Transfer]:
        """解析交易数据为 TRC20Transfer 列表"""
        transfers = []
        current_time = datetime.now(timezone.utc)
        
        for tx in tx_data:
            # 解析交易信息
            tx_id = tx.get("transaction_id") or tx.get("txID", "")
            
            # 获取转账详情 - 处理不同 API 格式的响应
            from_addr = tx.get("from") or tx.get("from_address", "")
            to_addr = tx.get("to") or tx.get("to_address", "")
            
            # 只返回指定地址的转入记录
            if to_addr.lower() != to_address.lower():
                continue
            
            # 解析金额 - 处理不同格式
            amount_raw = tx.get("value") or tx.get("amount", 0)
            if isinstance(amount_raw, str):
                amount_raw = int(amount_raw)
            amount = self._from_raw_amount(amount_raw)
            
            # 计算确认数
            tx_block_number = tx.get("block_number") or tx.get("blockNumber", 0)
            
            if latest_block is not None and tx_block_number:
                confirmations = max(0, latest_block - int(tx_block_number) + 1)
            else:
                confirmations = 1 if tx.get("finalResult") == "SUCCESS" else 0
            
            # 获取时间戳
            timestamp_ms = tx.get("block_timestamp") or tx.get("timestamp", 0)
            timestamp = datetime.fromtimestamp(timestamp_ms / 1000, tz=timezone.utc) if timestamp_ms else current_time
            
            transfer = TRC20Transfer(
                transaction_id=tx_id,
                from_address=from_addr,
                to_address=to_addr,
                amount=amount,
                token_contract=self.usdt_contract,
                confirmations=confirmations,
                timestamp=timestamp
            )
            transfers.append(transfer)
        
        return transfers
    
    async def detect_payment(
        self,
        address: str,
        expected_amount: float,
        contract: str = None,
        min_confirmations: int = 1,
        amount_tolerance: float = 0.01  # 1% 容差
    ) -> Optional[PaymentDetectionResult]:
        """
        检测指定地址是否收到预期的 USDT 转账
        
        Args:
            address: 接收地址
            expected_amount: 预期金额
            contract: 合约地址，默认使用 USDT 合约
            min_confirmations: 最小确认数
            amount_tolerance: 金额容差比例
            
        Returns:
            PaymentDetectionResult: 检测结果，未找到返回 None
        """
        contract = contract or self.usdt_contract
        
        # 计算容差范围
        min_amount = expected_amount * (1 - amount_tolerance)
        max_amount = expected_amount * (1 + amount_tolerance)
        
        # 获取转账记录
        transfers = await self.get_trc20_transfers(address, limit=50)
        
        for transfer in transfers:
            # 检查金额是否在容差范围内
            if not (min_amount <= transfer.amount <= max_amount):
                continue
            
            # 检查确认数
            if transfer.confirmations < min_confirmations:
                continue
            
            # 找到匹配的交易
            status = "confirmed" if transfer.confirmations >= min_confirmations else "pending"
            
            return PaymentDetectionResult(
                found=True,
                tx_hash=transfer.transaction_id,
                from_address=transfer.from_address,
                amount=transfer.amount,
                confirmations=transfer.confirmations,
                status=status
            )
        
        return PaymentDetectionResult(
            found=False,
            status="pending"
        )
    
    # ============== Mock 模式辅助方法 ==============
    
    def mock_add_transfer(self, transfer: TRC20Transfer):
        """Mock 模式：添加模拟转账记录"""
        if not self.mock_mode:
            raise RuntimeError("Only available in mock mode")
        self._mock_transfers.append(transfer)
        self._mock_transfers.sort(key=lambda x: x.timestamp, reverse=True)
    
    def mock_set_balance(self, address: str, balance: float):
        """Mock 模式：设置模拟余额"""
        if not self.mock_mode:
            raise RuntimeError("Only available in mock mode")
        self._mock_balances[address] = balance
    
    def mock_clear_transfers(self):
        """Mock 模式：清除所有模拟转账记录"""
        if not self.mock_mode:
            raise RuntimeError("Only available in mock mode")
        self._mock_transfers.clear()
    
    def mock_clear_balances(self):
        """Mock 模式：清除所有模拟余额"""
        if not self.mock_mode:
            raise RuntimeError("Only available in mock mode")
        self._mock_balances.clear()


# ============== 便捷工厂函数 ==============

def create_tron_client(
    rpc_url: str = None,
    usdt_contract: str = None,
    mock_mode: bool = False,
    use_mainnet: bool = False
) -> TronClient:
    """
    创建 TronClient 实例
    
    Args:
        rpc_url: RPC 节点 URL，默认使用 TronGrid
        usdt_contract: USDT 合约地址
        mock_mode: 是否使用 Mock 模式
        use_mainnet: 是否使用主网（影响默认合约地址）
        
    Returns:
        TronClient: 客户端实例
    """
    if rpc_url is None:
        rpc_url = "https://api.trongrid.io" if use_mainnet else "https://nile.trongrid.io"
    
    if usdt_contract is None:
        usdt_contract = USDT_CONTRACT_MAINNET if use_mainnet else USDT_CONTRACT_NILE
    
    return TronClient(
        rpc_url=rpc_url,
        usdt_contract=usdt_contract,
        mock_mode=mock_mode
    )


# ============== 配置集成 ==============

async def get_tron_client_from_config() -> TronClient:
    """
    从应用配置创建 TronClient
    
    需要确保配置已加载
    """
    try:
        from app.core.config import get_settings
        settings = get_settings()
        
        return TronClient(
            rpc_url=settings.tron_rpc_url,
            usdt_contract=settings.tron_usdt_contract,
            mock_mode=settings.tron_mock_mode
        )
    except ImportError:
        # 配置未加载时使用默认值
        return create_tron_client()
