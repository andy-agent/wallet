"""
Solana blockchain integration module
Supports both real RPC calls and mock mode for testing
"""
from dataclasses import dataclass
from datetime import datetime, timezone
from typing import List, Optional, Dict, Any
import random
import string

import httpx


@dataclass
class Transaction:
    """Solana transaction data"""
    signature: str
    from_address: str
    to_address: str
    amount: float
    confirmations: int
    timestamp: datetime
    memo: Optional[str]


@dataclass
class PaymentDetectionResult:
    """Payment detection result"""
    found: bool
    tx_hash: str
    from_address: str
    amount: float
    confirmations: int
    status: str  # pending, confirmed


class SolanaClient:
    """
    Solana blockchain client
    
    Supports:
    - Real RPC calls to Solana network
    - Mock mode for development and testing
    """
    
    def __init__(self, rpc_url: str, mock_mode: bool = False):
        """
        Initialize Solana client
        
        Args:
            rpc_url: Solana RPC endpoint URL
            mock_mode: If True, return mock data instead of real RPC calls
        """
        self.rpc_url = rpc_url
        self.mock_mode = mock_mode
        self._mock_transactions: Dict[str, List[Transaction]] = {}
        self._mock_balances: Dict[str, float] = {}
        self._client: Optional[httpx.AsyncClient] = None
    
    async def _get_client(self) -> httpx.AsyncClient:
        """Get or create HTTP client"""
        if self._client is None or self._client.is_closed:
            self._client = httpx.AsyncClient(timeout=30.0)
        return self._client
    
    async def _rpc_call(self, method: str, params: List[Any]) -> Dict[str, Any]:
        """
        Make JSON-RPC call to Solana node
        
        Args:
            method: RPC method name
            params: Method parameters
            
        Returns:
            RPC response data
        """
        if self.mock_mode:
            raise RuntimeError("Should not call _rpc_call in mock mode")
        
        client = await self._get_client()
        payload = {
            "jsonrpc": "2.0",
            "id": 1,
            "method": method,
            "params": params
        }
        
        response = await client.post(self.rpc_url, json=payload)
        response.raise_for_status()
        data = response.json()
        
        if "error" in data:
            raise RuntimeError(f"RPC error: {data['error']}")
        
        return data.get("result", {})
    
    def _generate_signature(self) -> str:
        """Generate a mock transaction signature"""
        chars = string.ascii_letters + string.digits
        return ''.join(random.choices(chars, k=87))
    
    def _generate_address(self) -> str:
        """Generate a mock Solana address"""
        chars = string.ascii_letters + string.digits
        return ''.join(random.choices(chars, k=43))
    
    def _add_mock_transaction(
        self,
        address: str,
        from_addr: str,
        amount: float,
        confirmations: int = 32,
        memo: Optional[str] = None
    ) -> Transaction:
        """Add a mock transaction for testing"""
        tx = Transaction(
            signature=self._generate_signature(),
            from_address=from_addr,
            to_address=address,
            amount=amount,
            confirmations=confirmations,
            timestamp=datetime.now(timezone.utc),
            memo=memo
        )
        
        if address not in self._mock_transactions:
            self._mock_transactions[address] = []
        self._mock_transactions[address].insert(0, tx)
        
        # Update mock balance
        self._mock_balances[address] = self._mock_balances.get(address, 0) + amount
        
        return tx
    
    async def get_balance(self, address: str) -> float:
        """
        Get SOL balance for an address
        
        Args:
            address: Solana wallet address
            
        Returns:
            Balance in SOL
        """
        if self.mock_mode:
            # Return mock balance or default value
            return self._mock_balances.get(address, random.uniform(0.1, 100.0))
        
        result = await self._rpc_call("getBalance", [address])
        # Convert lamports to SOL (1 SOL = 10^9 lamports)
        lamports = result.get("value", 0)
        return lamports / 1_000_000_000.0
    
    async def get_transactions(
        self,
        address: str,
        limit: int = 10
    ) -> List[Transaction]:
        """
        Get transaction history for an address
        
        Args:
            address: Solana wallet address
            limit: Maximum number of transactions to return
            
        Returns:
            List of transactions
        """
        if self.mock_mode:
            return self._mock_transactions.get(address, [])[:limit]
        
        # Get signatures for address
        signatures_result = await self._rpc_call(
            "getSignaturesForAddress",
            [address, {"limit": limit}]
        )
        
        transactions = []
        for sig_info in signatures_result:
            signature = sig_info.get("signature")
            if not signature:
                continue
            
            # Get transaction details
            tx = await self.get_transaction(signature)
            if tx:
                transactions.append(tx)
        
        return transactions
    
    async def get_transaction(self, signature: str) -> Optional[Transaction]:
        """
        Get transaction details by signature
        
        Args:
            signature: Transaction signature
            
        Returns:
            Transaction details or None if not found
        """
        if self.mock_mode:
            # Search in mock transactions
            for addr_txs in self._mock_transactions.values():
                for tx in addr_txs:
                    if tx.signature == signature:
                        return tx
            return None
        
        try:
            result = await self._rpc_call(
                "getTransaction",
                [signature, {"encoding": "jsonParsed", "maxSupportedTransactionVersion": 0}]
            )
            
            if not result:
                return None
            
            return self._parse_transaction(result, signature)
        except Exception:
            return None
    
    def _parse_transaction(
        self,
        result: Dict[str, Any],
        signature: str
    ) -> Optional[Transaction]:
        """
        Parse RPC transaction result into Transaction object
        
        Args:
            result: RPC response result
            signature: Transaction signature
            
        Returns:
            Parsed Transaction or None
        """
        meta = result.get("meta", {})
        transaction = result.get("transaction", {})
        message = transaction.get("message", {})
        
        # Get account keys
        account_keys = message.get("accountKeys", [])
        if not account_keys:
            return None
        
        # Parse timestamp
        block_time = result.get("blockTime")
        timestamp = datetime.fromtimestamp(block_time, tz=timezone.utc) if block_time else datetime.now(timezone.utc)
        
        # Get confirmations
        confirmations = result.get("confirmations", 0)
        
        # Try to extract transfer info from parsed instructions
        instructions = message.get("instructions", [])
        memo = None
        
        for instr in instructions:
            parsed = instr.get("parsed", {})
            instr_type = parsed.get("type", "")
            
            # Look for memo instruction
            if instr_type == "memo":
                memo = parsed.get("info", "")
            
            # Look for transfer instruction
            if instr_type == "transfer":
                info = parsed.get("info", {})
                from_addr = info.get("source", account_keys[0] if account_keys else "")
                to_addr = info.get("destination", "")
                amount_lamports = int(info.get("lamports", 0))
                amount = amount_lamports / 1_000_000_000.0
                
                return Transaction(
                    signature=signature,
                    from_address=from_addr,
                    to_address=to_addr,
                    amount=amount,
                    confirmations=confirmations,
                    timestamp=timestamp,
                    memo=memo
                )
        
        # If no parsed transfer found, try to extract from pre/post balances
        pre_balances = meta.get("preBalances", [])
        post_balances = meta.get("postBalances", [])
        
        if len(pre_balances) >= 2 and len(post_balances) >= 2:
            # Simple heuristic: find account with decreased balance
            for i, (pre, post) in enumerate(zip(pre_balances, post_balances)):
                if pre > post and i < len(account_keys):
                    from_addr = account_keys[i]
                    amount_lamports = pre - post
                    # Find destination (account with increased balance)
                    for j, (pre_j, post_j) in enumerate(zip(pre_balances, post_balances)):
                        if post_j > pre_j and j < len(account_keys):
                            return Transaction(
                                signature=signature,
                                from_address=from_addr,
                                to_address=account_keys[j],
                                amount=amount_lamports / 1_000_000_000.0,
                                confirmations=confirmations,
                                timestamp=timestamp,
                                memo=memo
                            )
        
        # Fallback: return basic transaction info
        if account_keys:
            return Transaction(
                signature=signature,
                from_address=account_keys[0],
                to_address=account_keys[1] if len(account_keys) > 1 else account_keys[0],
                amount=0.0,
                confirmations=confirmations,
                timestamp=timestamp,
                memo=memo
            )
        
        return None
    
    async def detect_payment(
        self,
        address: str,
        expected_amount: float,
        memo: Optional[str] = None
    ) -> Optional[PaymentDetectionResult]:
        """
        Detect payment to an address
        
        Args:
            address: Recipient wallet address
            expected_amount: Expected payment amount in SOL
            memo: Optional memo to match
            
        Returns:
            Payment detection result or None if no matching payment found
        """
        # Get recent transactions
        transactions = await self.get_transactions(address, limit=20)
        
        for tx in transactions:
            # Check if transaction is incoming to the target address
            if tx.to_address != address:
                continue
            
            # Check amount (with small tolerance for floating point)
            amount_diff = abs(tx.amount - expected_amount)
            tolerance = max(expected_amount * 0.001, 0.000001)  # 0.1% or 0.000001 SOL
            
            if amount_diff > tolerance:
                continue
            
            # Check memo if specified
            if memo is not None and tx.memo != memo:
                continue
            
            # Payment found
            status = "confirmed" if tx.confirmations >= 12 else "pending"
            
            return PaymentDetectionResult(
                found=True,
                tx_hash=tx.signature,
                from_address=tx.from_address,
                amount=tx.amount,
                confirmations=tx.confirmations,
                status=status
            )
        
        return None
    
    async def close(self):
        """Close HTTP client connection"""
        if self._client and not self._client.is_closed:
            await self._client.aclose()
    
    async def __aenter__(self):
        """Async context manager entry"""
        return self
    
    async def __aexit__(self, exc_type, exc_val, exc_tb):
        """Async context manager exit"""
        await self.close()
    
    # Mock mode helper methods for testing
    
    def mock_add_incoming_payment(
        self,
        address: str,
        from_addr: str,
        amount: float,
        confirmations: int = 32,
        memo: Optional[str] = None
    ) -> Transaction:
        """
        Add a mock incoming payment (for testing)
        
        Args:
            address: Recipient address
            from_addr: Sender address
            amount: Amount in SOL
            confirmations: Number of confirmations
            memo: Optional memo
            
        Returns:
            Created mock transaction
        """
        if not self.mock_mode:
            raise RuntimeError("Can only add mock payments in mock mode")
        
        return self._add_mock_transaction(address, from_addr, amount, confirmations, memo)
    
    def mock_set_balance(self, address: str, balance: float):
        """
        Set mock balance for an address (for testing)
        
        Args:
            address: Wallet address
            balance: Balance in SOL
        """
        if not self.mock_mode:
            raise RuntimeError("Can only set mock balance in mock mode")
        
        self._mock_balances[address] = balance
    
    def mock_clear_data(self):
        """Clear all mock data (for testing)"""
        if not self.mock_mode:
            raise RuntimeError("Can only clear mock data in mock mode")
        
        self._mock_transactions.clear()
        self._mock_balances.clear()
