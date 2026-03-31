"""
SPL Token integration tests
"""
import pytest
from decimal import Decimal

from app.integrations.solana import SolanaClient


@pytest.fixture
def solana_client():
    """Create a mock Solana client for testing"""
    return SolanaClient(
        rpc_url="https://api.devnet.solana.com",
        mock_mode=True,
        spl_token_mint="8zFP8GeszFz7FvuHesguekTxDjm4KLsJEYBZTKyMLEoE",
        spl_token_decimals=6
    )


@pytest.fixture
def sample_wallet():
    """Sample wallet address"""
    return "8ZUcz6GmBjP73eFHN5LwT4HCDrnrVUHbZXSaJtWsoSk7"


@pytest.fixture
def sample_sender():
    """Sample sender address"""
    return "SenderAddress1111111111111111111111111111111"


class TestSPLTokenATA:
    """Test Associated Token Account calculation"""
    
    def test_get_associated_token_address(self, solana_client, sample_wallet):
        """Test ATA address calculation"""
        ata = solana_client.get_associated_token_address(
            sample_wallet,
            solana_client.spl_token_mint
        )
        
        # Verify ATA is a valid base58 string
        assert len(ata) >= 43
        assert len(ata) <= 44
        
        # Verify ATA is deterministic
        ata2 = solana_client.get_associated_token_address(
            sample_wallet,
            solana_client.spl_token_mint
        )
        assert ata == ata2
    
    def test_different_wallets_different_atas(self, solana_client):
        """Test different wallets produce different ATAs"""
        wallet1 = "8ZUcz6GmBjP73eFHN5LwT4HCDrnrVUHbZXSaJtWsoSk7"
        wallet2 = "GsbwXfJraMomNxB8j6XPrBhrJLjpzU6qJDx6pPxFn8xK"
        
        ata1 = solana_client.get_associated_token_address(
            wallet1, solana_client.spl_token_mint
        )
        ata2 = solana_client.get_associated_token_address(
            wallet2, solana_client.spl_token_mint
        )
        
        assert ata1 != ata2


class TestSPLTokenMockPayments:
    """Test SPL token mock payment functionality"""
    
    @pytest.mark.asyncio
    async def test_mock_add_spl_token_payment(self, solana_client, sample_wallet, sample_sender):
        """Test adding mock SPL token payment"""
        amount = 10.5
        
        tx = solana_client.mock_add_spl_token_payment(
            wallet_address=sample_wallet,
            mint=solana_client.spl_token_mint,
            from_addr=sample_sender,
            amount=amount,
            confirmations=32
        )
        
        assert tx is not None
        assert tx.amount == amount
        assert tx.from_address == sample_sender
        assert tx.to_address == sample_wallet
        assert tx.mint == solana_client.spl_token_mint
        assert tx.confirmations == 32
    
    @pytest.mark.asyncio
    async def test_detect_spl_token_payment_found(self, solana_client, sample_wallet, sample_sender):
        """Test detecting existing SPL token payment"""
        amount = 25.0
        
        # Add mock payment
        solana_client.mock_add_spl_token_payment(
            wallet_address=sample_wallet,
            mint=solana_client.spl_token_mint,
            from_addr=sample_sender,
            amount=amount,
            confirmations=32
        )
        
        # Detect payment
        result = await solana_client.detect_spl_token_payment(
            wallet_address=sample_wallet,
            mint=solana_client.spl_token_mint,
            expected_amount=amount,
            min_confirmations=1
        )
        
        assert result.found is True
        assert result.amount == amount
        assert result.from_address == sample_sender
        assert result.confirmations == 32
        assert result.status == "confirmed"
    
    @pytest.mark.asyncio
    async def test_detect_spl_token_payment_not_found(self, solana_client, sample_wallet):
        """Test detecting non-existent SPL token payment"""
        result = await solana_client.detect_spl_token_payment(
            wallet_address=sample_wallet,
            mint=solana_client.spl_token_mint,
            expected_amount=100.0,
            min_confirmations=1
        )
        
        assert result.found is False
        assert result.status == "pending"
    
    @pytest.mark.asyncio
    async def test_detect_spl_token_payment_with_tolerance(self, solana_client, sample_wallet, sample_sender):
        """Test detecting payment with amount tolerance"""
        actual_amount = 10.0
        expected_amount = 10.05  # Within 1% tolerance
        
        solana_client.mock_add_spl_token_payment(
            wallet_address=sample_wallet,
            mint=solana_client.spl_token_mint,
            from_addr=sample_sender,
            amount=actual_amount,
            confirmations=12
        )
        
        result = await solana_client.detect_spl_token_payment(
            wallet_address=sample_wallet,
            mint=solana_client.spl_token_mint,
            expected_amount=expected_amount,
            amount_tolerance=0.01,  # 1% tolerance
            min_confirmations=1
        )
        
        assert result.found is True
    
    @pytest.mark.asyncio
    async def test_detect_spl_token_payment_confirmations_filter(self, solana_client, sample_wallet, sample_sender):
        """Test detecting payment with minimum confirmations filter"""
        solana_client.mock_add_spl_token_payment(
            wallet_address=sample_wallet,
            mint=solana_client.spl_token_mint,
            from_addr=sample_sender,
            amount=5.0,
            confirmations=5  # Less than required
        )
        
        result = await solana_client.detect_spl_token_payment(
            wallet_address=sample_wallet,
            mint=solana_client.spl_token_mint,
            expected_amount=5.0,
            min_confirmations=10  # Require 10 confirmations
        )
        
        assert result.found is False


class TestSPLTokenBalance:
    """Test SPL token balance operations"""
    
    @pytest.mark.asyncio
    async def test_mock_set_and_get_balance(self, solana_client, sample_wallet):
        """Test setting and getting SPL token balance"""
        balance = 1000.5
        
        solana_client.mock_set_spl_token_balance(
            wallet_address=sample_wallet,
            mint=solana_client.spl_token_mint,
            balance=balance
        )
        
        result = await solana_client.get_spl_token_balance(
            sample_wallet,
            solana_client.spl_token_mint
        )
        
        assert result == balance
    
    @pytest.mark.asyncio
    async def test_get_balance_non_existent_account(self, solana_client):
        """Test getting balance for non-existent account"""
        unknown_wallet = "GsbwXfJraMomNxB8j6XPrBhrJLjpzU6qJDx6pPxFn8xK"
        
        result = await solana_client.get_spl_token_balance(
            unknown_wallet,
            solana_client.spl_token_mint
        )
        
        assert result == 0.0


class TestSPLTokenAmountConversion:
    """Test amount conversion with decimals"""
    
    def test_from_token_amount(self, solana_client):
        """Test converting raw amount to human readable"""
        raw = 1000000  # 1 token with 6 decimals
        result = solana_client._from_token_amount(raw)
        assert result == 1.0
    
    def test_to_token_amount(self, solana_client):
        """Test converting human readable to raw amount"""
        amount = 1.5
        result = solana_client._to_token_amount(amount)
        assert result == 1500000
    
    def test_amount_conversion_round_trip(self, solana_client):
        """Test amount conversion round trip"""
        original = 123.456789
        raw = solana_client._to_token_amount(original)
        result = solana_client._from_token_amount(raw)
        # Should match within precision (6 decimals)
        assert abs(result - original) < 0.000001


class TestSPLTokenTransactions:
    """Test SPL token transaction queries"""
    
    @pytest.mark.asyncio
    async def test_get_spl_token_transactions(self, solana_client, sample_wallet, sample_sender):
        """Test getting SPL token transactions"""
        # Add multiple payments
        for i in range(3):
            solana_client.mock_add_spl_token_payment(
                wallet_address=sample_wallet,
                mint=solana_client.spl_token_mint,
                from_addr=sample_sender,
                amount=float(10 + i),
                confirmations=32
            )
        
        transactions = await solana_client.get_spl_token_transactions(
            sample_wallet,
            solana_client.spl_token_mint,
            limit=10
        )
        
        assert len(transactions) == 3
        # Transactions should be in reverse chronological order
        assert transactions[0].amount == 12.0
        assert transactions[1].amount == 11.0
        assert transactions[2].amount == 10.0


class TestSPLTokenClientConfiguration:
    """Test client configuration"""
    
    def test_spl_token_configuration(self):
        """Test client initialization with SPL token config"""
        mint = "CustomMintAddress111111111111111111111111111"
        decimals = 9
        
        client = SolanaClient(
            rpc_url="https://api.mainnet-beta.solana.com",
            mock_mode=True,
            spl_token_mint=mint,
            spl_token_decimals=decimals
        )
        
        assert client.spl_token_mint == mint
        assert client.spl_token_decimals == decimals
    
    def test_default_spl_token_decimals(self):
        """Test default SPL token decimals"""
        client = SolanaClient(
            rpc_url="https://api.devnet.solana.com",
            mock_mode=True
        )
        
        assert client.spl_token_decimals == 6  # Default


class TestSPLTokenMockModeRestrictions:
    """Test mock mode restrictions"""
    
    def test_mock_methods_require_mock_mode(self):
        """Test that mock methods require mock mode"""
        client = SolanaClient(
            rpc_url="https://api.devnet.solana.com",
            mock_mode=False
        )
        
        with pytest.raises(RuntimeError, match="mock mode"):
            client.mock_add_spl_token_payment(
                wallet_address="wallet",
                mint="mint",
                from_addr="from",
                amount=1.0
            )
        
        with pytest.raises(RuntimeError, match="mock mode"):
            client.mock_set_spl_token_balance("wallet", "mint", 1.0)
        
        with pytest.raises(RuntimeError, match="mock mode"):
            client.mock_clear_spl_token_data()


# Integration with Scanner tests would go here
# These would require database setup and full application context
