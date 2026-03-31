# SPL Token Integration Guide

This document describes the SPL Token payment integration for the v2rayng-payment-bridge system.

## Overview

The system now supports SPL Token payments on Solana, in addition to native SOL and TRC20 USDT. This allows users to pay using SPL Tokens like USDC.

## Supported Assets

| Asset Code | Chain | Type | Notes |
|------------|-------|------|-------|
| `SOL` | Solana | Native | Native SOL transfers |
| `SPL_TOKEN` | Solana | SPL Token | Configurable SPL Token (default: USDC) |
| `USDT_TRC20` | Tron | TRC20 | USDT on Tron network |

## Configuration

### Environment Variables

```bash
# SPL Token Contract (Mint) Address
SPL_TOKEN_MINT=8zFP8GeszFz7FvuHesguekTxDjm4KLsJEYBZTKyMLEoE

# Token Decimals (6 for USDC/USDT, 9 for some tokens)
SPL_TOKEN_DECIMALS=6

# Token Symbol for display
SPL_TOKEN_SYMBOL=USDC

# Enable/Disable SPL Token payments
SPL_TOKEN_ENABLED=true
```

### Database Setup

Import SPL Token receiving addresses into the address pool:

```sql
-- Import SPL Token addresses
INSERT INTO payment_addresses (chain, asset_code, address, encrypted_private_key, status)
VALUES 
    ('solana', 'SPL_TOKEN', 'YOUR_SOLANA_WALLET_ADDRESS_1', 'encrypted_private_key_1', 'available'),
    ('solana', 'SPL_TOKEN', 'YOUR_SOLANA_WALLET_ADDRESS_2', 'encrypted_private_key_2', 'available');
```

**Important Notes:**
- SPL Token addresses use the same format as Solana addresses
- The `asset_code` must be exactly `SPL_TOKEN`
- The addresses are "owner" addresses, not the ATA addresses
- System automatically calculates the corresponding ATA for monitoring

## How It Works

### 1. Order Creation

When a user creates an order with `asset_code: "SPL_TOKEN"`:

1. System allocates a Solana address from the address pool
2. Calculates the required token amount based on current USD rate
3. Returns the order with the receiving address

### 2. Payment Detection

The scanner (`app/workers/scanner.py`) monitors for payments:

1. Calculates the Associated Token Account (ATA) address from the owner address
2. Queries the Solana RPC for token transfers to that ATA
3. Parses transaction data to extract transfer amounts
4. Matches payments against expected amounts (with tolerance)

### 3. Payment Flow

```
User Wallet -> SPL Token Transfer -> Receiver ATA
                                      ↓
                                   Scanner detects
                                      ↓
                                 Order confirmed
```

## API Usage

### Create SPL Token Order

```bash
POST /api/client/orders
Content-Type: application/json
Authorization: Bearer <jwt_token>
X-Client-Version: 1.0.0

{
    "plan_id": "your_plan_id",
    "purchase_type": "new",
    "asset_code": "SPL_TOKEN"
}
```

### Response

```json
{
    "code": "SUCCESS",
    "message": "订单创建成功",
    "data": {
        "order_id": "01HQ...",
        "order_no": "ORD240331...",
        "chain": "solana",
        "asset_code": "SPL_TOKEN",
        "receive_address": "8ZUcz6GmBjP73eFHN5LwT4HCDrnrVUHbZXSaJtWsoSk7",
        "amount_crypto": "10.000000",
        "amount_usd": "10.00",
        "fx_rate": "1.0",
        "status": "pending_payment",
        "expires_at": "2026-03-31T21:29:00Z"
    }
}
```

### User Payment Instructions

Users should send SPL Tokens to the `receive_address` shown in the order. Most wallets (Phantom, Solflare, etc.) will:

1. Automatically calculate the correct ATA address
2. Create the ATA if it doesn't exist
3. Execute the token transfer

The system monitors the ATA for incoming transfers and confirms payments automatically.

## Associated Token Account (ATA)

### What is ATA?

An Associated Token Account is a deterministic address derived from:
- Wallet owner address
- Token mint address
- Token Program ID

### How It's Calculated

```python
ATA = find_program_address([
    wallet_address_bytes,
    token_program_id_bytes,
    mint_address_bytes
], associated_token_program_id)
```

### Implementation

The system provides `get_associated_token_address()` method:

```python
from app.integrations.solana import SolanaClient

client = SolanaClient(rpc_url, mock_mode=True)
ata = client.get_associated_token_address(
    wallet_address="8ZUcz6GmBjP73eFHN5LwT4HCDrnrVUHbZXSaJtWsoSk7",
    mint="8zFP8GeszFz7FvuHesguekTxDjm4KLsJEYBZTKyMLEoE"
)
```

**Note:** Current implementation uses a simplified PDA calculation. For production use with high security requirements, consider integrating the `solders` library for accurate PDA derivation.

## Testing

### Run SPL Token Tests

```bash
cd /Users/cnyirui/git/projects/liaojiang/code/server
python3 -m pytest tests/test_spl_token.py -v
```

### Mock Mode Testing

```python
import asyncio
from app.integrations.solana import SolanaClient

async def test():
    client = SolanaClient(
        rpc_url="https://api.devnet.solana.com",
        mock_mode=True,
        spl_token_mint="8zFP8GeszFz7FvuHesguekTxDjm4KLsJEYBZTKyMLEoE",
        spl_token_decimals=6
    )
    
    # Add mock payment
    client.mock_add_spl_token_payment(
        wallet_address="8ZUcz6GmBjP73eFHN5LwT4HCDrnrVUHbZXSaJtWsoSk7",
        mint=client.spl_token_mint,
        from_addr="SenderAddress1111111111111111111111111111111",
        amount=10.5,
        confirmations=32
    )
    
    # Detect payment
    result = await client.detect_spl_token_payment(
        wallet_address="8ZUcz6GmBjP73eFHN5LwT4HCDrnrVUHbZXSaJtWsoSk7",
        mint=client.spl_token_mint,
        expected_amount=10.5
    )
    
    print(f"Found: {result.found}, Amount: {result.amount}")

asyncio.run(test())
```

## Devnet Testing

### 1. Get Devnet SOL

Use the [Solana Faucet](https://faucet.solana.com/) to get devnet SOL for testing.

### 2. Create SPL Token (Optional)

If you want to test with a custom SPL token:

```bash
# Install SPL Token CLI
npm install -g @solana/spl-token

# Create a new token
spl-token create-token

# Create token account
spl-token create-account <TOKEN_MINT>

# Mint some tokens for testing
spl-token mint <TOKEN_MINT> 1000
```

### 3. Test Payment Flow

1. Create an order via API
2. Send SPL tokens to the receiving address
3. Wait for scanner to detect the payment
4. Verify order status changes to `paid_success`

## Troubleshooting

### Issue: Payment not detected

**Check:**
1. Is the ATA correctly calculated?
2. Is the transaction confirmed on-chain?
3. Are the token mint addresses matching?
4. Check scanner logs for errors

### Issue: Wrong amount detected

**Check:**
1. Token decimals configuration matches the actual token
2. Amount precision in calculations
3. Tolerance settings in `detect_spl_token_payment()`

### Issue: ATA not found

**Check:**
1. The wallet address format is valid base58
2. The mint address is correct
3. RPC endpoint is accessible

## Security Considerations

1. **Private Key Storage**: Payment address private keys must be securely encrypted
2. **RPC Endpoint**: Use reliable RPC providers (QuickNode, Alchemy, etc.) for production
3. **Confirmation Count**: Ensure sufficient confirmations before marking as paid
4. **Amount Tolerance**: Configure appropriate tolerance for stablecoin depegs

## Migration from SOL-only

If upgrading from SOL-only system:

1. Import SPL Token addresses to address pool
2. Update environment variables
3. Restart workers
4. Test payment flow on devnet
5. Deploy to production

No database migration required - existing SOL payments continue to work.

## Further Reading

- [SPL Token Program](https://spl.solana.com/token)
- [Associated Token Account](https://spl.solana.com/associated-token-account)
- [Solana RPC API](https://docs.solana.com/api/http)
- [Helius API](https://docs.helius.dev/) (alternative RPC provider with enhanced APIs)
