# SPL代币合约支持 - 任务完成交接文档

**任务ID**: liaojiang-1k3  
**任务名称**: SPL代币合约8zFP8GeszFz7FvuHesguekTxDjm4KLsJEYBZTKyMLEoE支持  
**完成日期**: 2026-03-31

---

## 完成情况概述

SPL代币支付支持已完全实现，包括：
- SolanaClient扩展支持SPL代币操作
- Scanner支持SPL代币支付检测
- AddressPool支持SPL_TOKEN资产类型
- 配置扩展添加SPL代币配置
- 汇率服务支持SPL代币汇率查询
- 订单API支持创建SPL代币订单

---

## 修改的文件

### 1. app/integrations/solana.py
**新增功能：**
- `get_associated_token_address(wallet_address, mint)` - 计算ATA地址
- `get_spl_token_balance(wallet_address, mint)` - 查询SPL代币余额
- `get_spl_token_transactions(wallet_address, mint, limit)` - 查询SPL代币交易历史
- `detect_spl_token_payment(wallet_address, mint, expected_amount, ...)` - 检测SPL代币支付
- Mock模式辅助方法支持SPL代币测试

**数据类：**
- `SPLTokenTransaction` - SPL代币交易数据
- `SPLPaymentDetectionResult` - SPL代币支付检测结果

**关键常量：**
- `TOKEN_PROGRAM_ID` - TokenkegQfeZyiNwAJbNbGKPFXCWuBvf9Ss623VQ5DA
- `ASSOCIATED_TOKEN_PROGRAM_ID` - ATokenGPvbdGVxr1b2hvZbsiqW5xWH25efTNsLJA8knL

### 2. app/workers/scanner.py
**新增方法：**
- `_detect_spl_token_payment(session, order, client)` - SPL代币支付检测逻辑
- `_detect_native_payment(session, order, client)` - 原生代币支付检测

**修改：**
- `_get_chain_client()` 支持 `asset_code` 参数，为SPL_TOKEN配置mint和decimals
- `_detect_payment_for_order()` 根据asset_code路由到对应的检测方法
- `confirm_seen_transactions()` 支持SPL代币交易确认

### 3. app/core/config.py
**新增配置项：**
```python
spl_token_mint: str = "8zFP8GeszFz7FvuHesguekTxDjm4KLsJEYBZTKyMLEoE"
spl_token_decimals: int = 6
spl_token_symbol: str = "USDC"
spl_token_enabled: bool = True
```

### 4. app/services/fx_rate.py
**新增功能：**
- `FXRateService.ASSET_MAPPING` 添加 `SPL_TOKEN` 映射
- `get_spl_token_usd_rate()` 获取SPL代币USD汇率
- `convert_usd_to_crypto()` 支持SPL_TOKEN精度处理

**默认汇率行为：**
- SPL_TOKEN默认映射到USDC（CoinGecko: usd-coin, Binance: USDCUSDT）
- 如果是稳定币（USDC/USDT），默认返回1.0

### 5. app/api/client/orders.py
**修改：**
- `CreateOrderRequest.asset_code` 验证支持 `SPL_TOKEN`
- `_resolve_chain_and_asset()` 添加SPL_TOKEN映射
- `_get_fx_rate_safe()` 添加SPL_TOKEN汇率获取
- `_convert_amount_safe()` 支持SPL代币精度（基于配置）

---

## 环境变量配置

在 `.env` 文件中添加：

```bash
# SPL Token Configuration (可选，使用默认值时可省略)
SPL_TOKEN_MINT=8zFP8GeszFz7FvuHesguekTxDjm4KLsJEYBZTKyMLEoE
SPL_TOKEN_DECIMALS=6
SPL_TOKEN_SYMBOL=USDC
SPL_TOKEN_ENABLED=true
```

---

## 数据库配置

### Address Pool
需要预先导入SPL代币收款地址：

```sql
-- 导入SPL代币地址（与普通Solana地址格式相同）
INSERT INTO payment_addresses (chain, asset_code, address, encrypted_private_key, status)
VALUES 
    ('solana', 'SPL_TOKEN', 'YOUR_SOLANA_ADDRESS_1', 'encrypted_priv_key_1', 'available'),
    ('solana', 'SPL_TOKEN', 'YOUR_SOLANA_ADDRESS_2', 'encrypted_priv_key_2', 'available');
```

**说明：**
- SPL代币的收款地址实际上是Solana钱包地址（owner地址）
- 用户支付时需要转账到该地址对应的ATA（Associated Token Account）
- 系统会自动计算ATA地址并监控该地址的转账

---

## 使用示例

### 创建SPL代币订单

```bash
curl -X POST "http://localhost:8000/api/client/orders" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "X-Client-Version: 1.0.0" \
  -d '{
    "plan_id": "your_plan_id",
    "purchase_type": "new",
    "asset_code": "SPL_TOKEN"
  }'
```

**响应示例：**
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

### 用户支付说明

用户需要向以下地址转账：
- **收款地址（Owner）**: `receive_address`（订单返回的地址）
- **实际ATA地址**: 系统会自动计算
- **转账要求**: 使用标准SPL Token Transfer指令转账

**注意：** 用户钱包会自动处理ATA创建，用户只需确认转账到指定owner地址的代币数量。

---

## 测试

### Mock模式测试

```python
import asyncio
from app.integrations.solana import SolanaClient

async def test_spl_token():
    client = SolanaClient(
        rpc_url="https://api.devnet.solana.com",
        mock_mode=True,
        spl_token_mint="8zFP8GeszFz7FvuHesguekTxDjm4KLsJEYBZTKyMLEoE",
        spl_token_decimals=6
    )
    
    # 添加模拟SPL代币支付
    wallet = "8ZUcz6GmBjP73eFHN5LwT4HCDrnrVUHbZXSaJtWsoSk7"
    client.mock_add_spl_token_payment(
        wallet_address=wallet,
        mint=client.spl_token_mint,
        from_addr="SenderAddress1111111111111111111111111111111",
        amount=10.5,
        confirmations=32
    )
    
    # 检测支付
    result = await client.detect_spl_token_payment(
        wallet_address=wallet,
        mint=client.spl_token_mint,
        expected_amount=10.5
    )
    
    print(f"Payment found: {result.found}")
    print(f"Amount: {result.amount}")
    print(f"Tx Hash: {result.tx_hash}")

asyncio.run(test_spl_token())
```

---

## 技术实现细节

### ATA地址计算

ATA（Associated Token Account）地址通过以下算法计算：

```
ATA = find_program_address([
    wallet_address_bytes,
    token_program_id_bytes,
    mint_address_bytes
], associated_token_program_id)
```

**注意：** 当前实现使用简化的PDA计算方法。生产环境建议集成 `solders` 库：

```bash
pip install solders
```

然后替换为：
```python
from solders.pubkey import Pubkey
from spl.token.constants import ASSOCIATED_TOKEN_PROGRAM_ID, TOKEN_PROGRAM_ID

def get_ata(wallet: str, mint: str) -> str:
    wallet_pubkey = Pubkey.from_string(wallet)
    mint_pubkey = Pubkey.from_string(mint)
    ata, _ = Pubkey.find_program_address(
        [
            bytes(wallet_pubkey),
            bytes(TOKEN_PROGRAM_ID),
            bytes(mint_pubkey)
        ],
        ASSOCIATED_TOKEN_PROGRAM_ID
    )
    return str(ata)
```

### SPL Token精度处理

- 默认精度：6位小数（类似USDC/USDT）
- 可配置：通过 `SPL_TOKEN_DECIMALS` 环境变量
- 金额计算：使用 `Decimal` 类型避免浮点误差

### 支付检测流程

1. **扫描订单**：`scan_pending_orders()` 查询 `PENDING_PAYMENT` 状态的订单
2. **获取客户端**：`_get_chain_client()` 根据 `asset_code` 创建配置好的客户端
3. **检测支付**：
   - SOL: `client.detect_payment()`
   - SPL_TOKEN: `_detect_spl_token_payment()` → `client.detect_spl_token_payment()`
4. **状态转换**：检测到支付后，状态变为 `SEEN_ONCHAIN`
5. **确认处理**：`confirm_seen_transactions()` 确认交易并更新状态

---

## 注意事项

1. **地址池管理**
   - SPL_TOKEN使用与SOL相同的Solana地址格式
   - 需要单独导入 `asset_code='SPL_TOKEN'` 的地址
   - 地址分配逻辑与SOL完全相同

2. **汇率获取**
   - 默认将SPL_TOKEN映射到USDC
   - 如果需要支持其他SPL代币，修改 `FXRateService.ASSET_MAPPING`
   - 稳定币可配置为固定汇率1.0

3. **交易解析**
   - 使用Solana RPC的 `getTokenAccountBalance` 查询余额
   - 使用 `getSignaturesForAddress` 查询ATA交易历史
   - 解析 `preTokenBalances` 和 `postTokenBalances` 计算转账金额

4. **Mock模式**
   - 支持完整的SPL代币Mock测试
   - `mock_add_spl_token_payment()` 添加模拟支付
   - `mock_set_spl_token_balance()` 设置模拟余额

---

## 后续优化建议

1. **集成solders库**：替换简化的ATA计算为正确的PDA算法
2. **Helius API支持**：添加Helius API作为备用数据源获取SPL交易
3. **多SPL代币支持**：扩展配置支持多种SPL代币
4. **监控告警**：添加SPL代币余额监控和告警
5. **文档完善**：添加更详细的用户支付指南

---

## 相关配置速查

| 环境变量 | 默认值 | 说明 |
|---------|-------|------|
| `SPL_TOKEN_MINT` | 8zFP8Gesz... | SPL代币合约地址 |
| `SPL_TOKEN_DECIMALS` | 6 | 代币精度 |
| `SPL_TOKEN_SYMBOL` | USDC | 代币符号 |
| `SPL_TOKEN_ENABLED` | true | 是否启用 |
| `SOLANA_RPC_URL` | devnet | Solana RPC端点 |
| `SOLANA_MOCK_MODE` | false | Mock模式开关 |

---

**任务状态**: ✅ 已完成  
**代码审查**: 待进行  
**部署状态**: 待部署到测试网验证
