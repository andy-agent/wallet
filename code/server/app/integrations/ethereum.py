"""
Ethereum blockchain integration module for ERC20 USDT monitoring
支持 Alchemy/Infura 等 RPC 节点
支持 Mock 模式用于测试
"""
from dataclasses import dataclass
from datetime import datetime, timezone
from typing import List, Optional, Dict, Any
import asyncio
import logging

import httpx

logger = logging.getLogger(__name__)

# USDT ERC20 合约地址
USDT_CONTRACT_MAINNET = "0xdAC17F958D2ee523a2206206994597C13D831ec7"
USDT_CONTRACT_SEPOLIA = "0xaA8E23Fb1079EA71e0a56F48a2aA51851D8433D0"  # Sepolia testnet USDT

# ERC20 Transfer 事件签名哈希
TRANSFER_EVENT_SIGNATURE = "0xddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef"

# ERC20 标准 ABI (仅包含需要的部分)
ERC20_ABI = [
    {
        "constant": True,
        "inputs": [],
        "name": "decimals",
        "outputs": [{"name": "", "type": "uint8"}],
        "payable": False,
        "stateMutability": "view",
        "type": "function"
    },
    {
        "constant": True,
        "inputs": [{"name": "_owner", "type": "address"}],
        "name": "balanceOf",
        "outputs": [{"name": "balance", "type": "uint256"}],
        "payable": False,
        "stateMutability": "view",
        "type": "function"
    }
]


@dataclass
class ERC20Transfer:
    """ERC20 转账记录"""
    transaction_hash: str
    from_address: str
    to_address: str
    amount: float  # 已转换精度（6位小数）
    token_contract: str
    confirmations: int
    timestamp: datetime
    block_number: int


@dataclass
class PaymentDetectionResult:
    """支付检测结果"""
    found: bool
    tx_hash: Optional[str] = None
    from_address: Optional[str] = None
    amount: Optional[float] = None
    confirmations: int = 0
    status: str = "pending"  # pending, confirmed, failed


class EthereumClient:
    """
    Ethereum 链上客户端
    
    支持功能：
    - 查询 ERC20 余额
    - 查询 ERC20 转账记录（通过事件日志）
    - 检测 USDT 到账
    - Mock 模式用于测试
    """
    
    def __init__(
        self,
        rpc_url: str,
        usdt_contract: str,
        mock_mode: bool = False,
        decimals: int = 6
    ):
        self.rpc_url = rpc_url.rstrip('/')
        self.usdt_contract = usdt_contract.lower()
        self.mock_mode = mock_mode
        self.decimals = decimals  # USDT 是 6 位小数
        
        if not mock_mode:
            self.client = httpx.AsyncClient(
                timeout=30.0,
                headers={
                    "Accept": "application/json",
                    "Content-Type": "application/json"
                }
            )
        else:
            self.client = None
            self._mock_transfers: List[ERC20Transfer] = []
            self._mock_balances: Dict[str, float] = {}
            self._mock_block_number = 1000000
        
        logger.info(f"EthereumClient initialized (mock={mock_mode}, contract={usdt_contract})")
    
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
    
    async def _rpc_call(self, method: str, params: List[Any]) -> Dict[str, Any]:
        """
        发送 JSON-RPC 请求
        
        Args:
            method: RPC 方法名
            params: 方法参数
            
        Returns:
            RPC 响应结果
        """
        if self.mock_mode:
            raise RuntimeError("Should not call _rpc_call in mock mode")
        
        payload = {
            "jsonrpc": "2.0",
            "method": method,
            "params": params,
            "id": 1
        }
        
        response = await self.client.post(self.rpc_url, json=payload)
        response.raise_for_status()
        data = response.json()
        
        if "error" in data:
            raise RuntimeError(f"RPC error: {data['error']}")
        
        return data.get("result", {})
    
    async def get_block_number(self) -> int:
        """
        获取当前最新区块高度
        
        Returns:
            int: 最新区块高度
        """
        if self.mock_mode:
            return self._mock_block_number
        
        result = await self._rpc_call("eth_blockNumber", [])
        return int(result, 16) if result else 0
    
    async def get_erc20_balance(
        self,
        address: str,
        contract: str = None
    ) -> float:
        """
        查询 ERC20 代币余额
        
        Args:
            address: 钱包地址
            contract: 合约地址，默认使用 USDT 合约
            
        Returns:
            float: 代币余额（已转换精度）
        """
        if self.mock_mode:
            return self._mock_balances.get(address.lower(), 0.0)
        
        contract = contract or self.usdt_contract
        
        # 构造 balanceOf 调用数据
        # 方法签名: balanceOf(address) = 0x70a08231
        address_padded = address.lower().replace('0x', '').zfill(64)
        data = f"0x70a08231{address_padded}"
        
        params = {
            "to": contract,
            "data": data
        }
        
        result = await self._rpc_call("eth_call", [params, "latest"])
        
        if result and result != "0x":
            raw_balance = int(result, 16)
            return self._from_raw_amount(raw_balance)
        
        return 0.0
    
    async def get_transfer_events(
        self,
        to_address: str,
        from_block: Optional[int] = None,
        to_block: Optional[int] = None,
        limit: int = 10
    ) -> List[ERC20Transfer]:
        """
        查询指定地址的 ERC20 Transfer 事件
        
        使用 eth_getLogs 查询 Transfer 事件日志
        
        Args:
            to_address: 接收地址
            from_block: 起始区块（可选，默认最近1000个区块）
            to_block: 结束区块（可选，默认最新）
            limit: 返回记录数量限制
            
        Returns:
            List[ERC20Transfer]: 转账记录列表
        """
        if self.mock_mode:
            return [
                t for t in self._mock_transfers
                if t.to_address.lower() == to_address.lower()
            ][:limit]
        
        try:
            # 获取当前区块高度
            latest_block = await self.get_block_number()
            
            # 计算区块范围
            if to_block is None:
                to_block = latest_block
            if from_block is None:
                from_block = max(0, to_block - 1000)  # 默认查询最近1000个区块
            
            # 构造事件过滤器
            # Transfer 事件的 indexed topic[0] 是事件签名
            # topic[1] 是 from 地址，topic[2] 是 to 地址
            to_address_padded = to_address.lower().replace('0x', '').zfill(64)
            
            params = {
                "fromBlock": hex(from_block),
                "toBlock": hex(to_block),
                "address": self.usdt_contract,
                "topics": [
                    TRANSFER_EVENT_SIGNATURE,
                    None,  # from (any)
                    f"0x{to_address_padded}"  # to (specific address)
                ]
            }
            
            logs = await self._rpc_call("eth_getLogs", [params])
            
            if not logs:
                return []
            
            transfers = []
            for log in logs[:limit]:
                transfer = await self._parse_transfer_log(log, latest_block)
                if transfer:
                    transfers.append(transfer)
            
            return transfers
            
        except Exception as e:
            logger.error(f"Error getting transfer events: {e}")
            return []
    
    async def _parse_transfer_log(
        self,
        log: Dict[str, Any],
        latest_block: int
    ) -> Optional[ERC20Transfer]:
        """
        解析 Transfer 事件日志
        
        Args:
            log: 事件日志
            latest_block: 最新区块高度（用于计算确认数）
            
        Returns:
            ERC20Transfer 或 None
        """
        try:
            topics = log.get("topics", [])
            if len(topics) < 3:
                return None
            
            # topics[1] 是 from 地址（去掉前导0）
            from_addr = "0x" + topics[1][-40:]
            # topics[2] 是 to 地址
            to_addr = "0x" + topics[2][-40:]
            
            # data 是金额（32字节）
            data = log.get("data", "0x")
            if data == "0x":
                return None
            
            raw_amount = int(data, 16)
            amount = self._from_raw_amount(raw_amount)
            
            # 获取区块信息以提取时间戳
            block_number = int(log.get("blockNumber", "0x0"), 16)
            tx_hash = log.get("transactionHash", "")
            
            # 计算确认数
            confirmations = max(0, latest_block - block_number + 1) if latest_block else 0
            
            # 获取区块时间戳
            timestamp = await self._get_block_timestamp(block_number)
            
            return ERC20Transfer(
                transaction_hash=tx_hash,
                from_address=from_addr.lower(),
                to_address=to_addr.lower(),
                amount=amount,
                token_contract=self.usdt_contract,
                confirmations=confirmations,
                timestamp=timestamp,
                block_number=block_number
            )
            
        except Exception as e:
            logger.error(f"Error parsing transfer log: {e}")
            return None
    
    async def _get_block_timestamp(self, block_number: int) -> datetime:
        """
        获取区块时间戳
        
        Args:
            block_number: 区块高度
            
        Returns:
            datetime: 区块时间
        """
        if self.mock_mode:
            return datetime.now(timezone.utc)
        
        try:
            block = await self._rpc_call("eth_getBlockByNumber", [hex(block_number), False])
            if block and "timestamp" in block:
                timestamp = int(block["timestamp"], 16)
                return datetime.fromtimestamp(timestamp, tz=timezone.utc)
        except Exception as e:
            logger.warning(f"Error getting block timestamp: {e}")
        
        return datetime.now(timezone.utc)
    
    async def detect_payment(
        self,
        address: str,
        expected_amount: float,
        contract: str = None,
        min_confirmations: int = 1,
        amount_tolerance: float = 0.01  # 1% 容差
    ) -> PaymentDetectionResult:
        """
        检测指定地址是否收到预期的 USDT 转账
        
        Args:
            address: 接收地址
            expected_amount: 预期金额
            contract: 合约地址，默认使用 USDT 合约
            min_confirmations: 最小确认数
            amount_tolerance: 金额容差比例
            
        Returns:
            PaymentDetectionResult: 检测结果
        """
        contract = contract or self.usdt_contract
        
        # 计算容差范围
        min_amount = expected_amount * (1 - amount_tolerance)
        max_amount = expected_amount * (1 + amount_tolerance)
        
        # 获取转账记录（查询更广泛的区块范围）
        try:
            latest_block = await self.get_block_number()
            from_block = max(0, latest_block - 5000)  # 查询最近5000个区块
            
            transfers = await self.get_transfer_events(
                to_address=address,
                from_block=from_block,
                limit=50
            )
        except Exception as e:
            logger.error(f"Error fetching transfers for payment detection: {e}")
            transfers = []
        
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
                tx_hash=transfer.transaction_hash,
                from_address=transfer.from_address,
                amount=transfer.amount,
                confirmations=transfer.confirmations,
                status=status
            )
        
        return PaymentDetectionResult(
            found=False,
            status="pending"
        )
    
    async def get_transaction_confirmations(self, tx_hash: str) -> int:
        """
        获取交易确认数
        
        Args:
            tx_hash: 交易哈希
            
        Returns:
            int: 确认数，如果交易不存在返回 0
        """
        if self.mock_mode:
            for transfer in self._mock_transfers:
                if transfer.transaction_hash.lower() == tx_hash.lower():
                    return transfer.confirmations
            return 0
        
        try:
            # 获取交易收据
            receipt = await self._rpc_call("eth_getTransactionReceipt", [tx_hash])
            if not receipt:
                return 0
            
            # 获取当前区块高度
            latest_block = await self.get_block_number()
            tx_block = int(receipt.get("blockNumber", "0x0"), 16)
            
            if tx_block == 0:
                return 0
            
            # 计算确认数
            confirmations = latest_block - tx_block + 1
            return max(0, confirmations)
            
        except Exception as e:
            logger.warning(f"Error getting transaction confirmations: {e}")
            return 0
    
    # ============== Mock 模式辅助方法 ==============
    
    def mock_add_transfer(self, transfer: ERC20Transfer):
        """Mock 模式：添加模拟转账记录"""
        if not self.mock_mode:
            raise RuntimeError("Only available in mock mode")
        self._mock_transfers.append(transfer)
        self._mock_transfers.sort(key=lambda x: x.timestamp, reverse=True)
    
    def mock_set_balance(self, address: str, balance: float):
        """Mock 模式：设置模拟余额"""
        if not self.mock_mode:
            raise RuntimeError("Only available in mock mode")
        self._mock_balances[address.lower()] = balance
    
    def mock_set_block_number(self, block_number: int):
        """Mock 模式：设置当前区块高度"""
        if not self.mock_mode:
            raise RuntimeError("Only available in mock mode")
        self._mock_block_number = block_number
    
    def mock_advance_blocks(self, count: int = 1):
        """Mock 模式：推进区块高度"""
        if not self.mock_mode:
            raise RuntimeError("Only available in mock mode")
        self._mock_block_number += count
        
        # 更新所有转账的确认数
        for transfer in self._mock_transfers:
            transfer.confirmations = self._mock_block_number - transfer.block_number + 1
    
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
    
    def mock_create_incoming_transfer(
        self,
        to_address: str,
        from_address: str,
        amount: float,
        confirmations: int = 12
    ) -> ERC20Transfer:
        """
        Mock 模式：创建一笔模拟的转入转账
        
        Args:
            to_address: 接收地址
            from_address: 发送地址
            amount: 金额
            confirmations: 确认数
            
        Returns:
            ERC20Transfer: 创建的转账记录
        """
        if not self.mock_mode:
            raise RuntimeError("Only available in mock mode")
        
        tx_hash = f"0x{''.join(['0123456789abcdef'[i % 16] for i in range(64)])}"
        block_number = self._mock_block_number - confirmations + 1
        
        transfer = ERC20Transfer(
            transaction_hash=tx_hash,
            from_address=from_address.lower(),
            to_address=to_address.lower(),
            amount=amount,
            token_contract=self.usdt_contract,
            confirmations=confirmations,
            timestamp=datetime.now(timezone.utc),
            block_number=block_number
        )
        
        self.mock_add_transfer(transfer)
        
        # 更新余额
        addr_lower = to_address.lower()
        self._mock_balances[addr_lower] = self._mock_balances.get(addr_lower, 0.0) + amount
        
        return transfer


# ============== 便捷工厂函数 ==============

def create_ethereum_client(
    rpc_url: str = None,
    usdt_contract: str = None,
    mock_mode: bool = False,
    use_mainnet: bool = False
) -> EthereumClient:
    """
    创建 EthereumClient 实例
    
    Args:
        rpc_url: RPC 节点 URL，默认使用 Alchemy/Infura
        usdt_contract: USDT 合约地址
        mock_mode: 是否使用 Mock 模式
        use_mainnet: 是否使用主网（影响默认合约地址）
        
    Returns:
        EthereumClient: 客户端实例
    """
    if usdt_contract is None:
        usdt_contract = USDT_CONTRACT_MAINNET if use_mainnet else USDT_CONTRACT_SEPOLIA
    
    return EthereumClient(
        rpc_url=rpc_url or "https://eth-sepolia.g.alchemy.com/v2/demo",
        usdt_contract=usdt_contract,
        mock_mode=mock_mode
    )


# ============== 配置集成 ==============

async def get_ethereum_client_from_config() -> EthereumClient:
    """
    从应用配置创建 EthereumClient
    
    需要确保配置已加载
    """
    try:
        from app.core.config import get_settings
        settings = get_settings()
        
        return EthereumClient(
            rpc_url=settings.eth_rpc_url,
            usdt_contract=settings.usdt_contract_address,
            mock_mode=settings.eth_mock_mode
        )
    except ImportError:
        # 配置未加载时使用默认值
        return create_ethereum_client()
