"""
Tron chain integration module for TRC20 USDT monitoring
支持 Mock 模式用于测试
"""
from dataclasses import dataclass
from datetime import datetime
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
        """
        if self.mock_mode:
            return self._mock_balances.get(address, 0.0)
        
        contract = contract or self.usdt_contract
        
        try:
            # 使用 TronGrid API 查询账户信息
            url = f"/v1/accounts/{address}"
            response = await self.client.get(url)
            response.raise_for_status()
            
            data = response.json()
            
            if not data.get("success"):
                logger.warning(f"Failed to get account info: {data}")
                return 0.0
            
            # 查找指定合约的余额
            for trc20 in data.get("data", [{}])[0].get("trc20", []):
                if contract in trc20:
                    raw_balance = int(trc20[contract])
                    return self._from_raw_amount(raw_balance)
            
            return 0.0
            
        except Exception as e:
            logger.error(f"Error getting TRC20 balance: {e}")
            return 0.0
    
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
            # 使用 TronGrid API 查询合约交易
            url = f"/v1/contracts/{self.usdt_contract}/transactions"
            params = {
                "limit": limit * 3,  # 多查询一些以过滤
                "event_name": "Transfer",
            }
            
            response = await self.client.get(url, params=params)
            response.raise_for_status()
            
            data = response.json()
            
            if not data.get("success"):
                logger.warning(f"Failed to get transactions: {data}")
                return []
            
            transfers = []
            current_time = datetime.utcnow()
            
            for tx in data.get("data", []):
                # 解析交易信息
                tx_id = tx.get("txID", "")
                raw_data = tx.get("raw_data", {})
                contract_data = raw_data.get("contract", [{}])[0].get("parameter", {}).get("value", {})
                
                # 获取转账详情
                from_addr = contract_data.get("from_address", "")
                to_addr = contract_data.get("to_address", "")
                
                # 只返回指定地址的转入记录
                if to_addr.lower() != to_address.lower():
                    continue
                
                # 解析金额
                raw_amount = int(contract_data.get("amount", 0))
                amount = self._from_raw_amount(raw_amount)
                
                # 获取确认数
                ret = tx.get("ret", [{}])[0]
                confirmations = 1 if ret.get("contractRet") == "SUCCESS" else 0
                
                # 获取时间戳（毫秒转秒）
                timestamp_ms = raw_data.get("timestamp", 0)
                timestamp = datetime.fromtimestamp(timestamp_ms / 1000) if timestamp_ms else current_time
                
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
                
                if len(transfers) >= limit:
                    break
            
            return transfers
            
        except Exception as e:
            logger.error(f"Error getting TRC20 transfers: {e}")
            return []
    
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
