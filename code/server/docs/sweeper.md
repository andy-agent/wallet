# 资金归集功能 (Sweeper)

## 概述

资金归集功能自动将已 fulfilled 订单的收款地址余额转移到主钱包，确保资金安全。

## 工作流程

```
┌─────────────┐     ┌─────────────┐     ┌─────────────┐
│   Order     │────▶│  Fulfilled  │────▶│  Sweeper    │
│   Paid      │     │   Status    │     │   Scans     │
└─────────────┘     └─────────────┘     └──────┬──────┘
                                                │
                                                ▼
┌─────────────┐     ┌─────────────┐     ┌─────────────┐
│   Address   │◀────│  Transfer   │◀────│  Check      │
│   Swept     │     │   Funds     │     │  Balance    │
└─────────────┘     └─────────────┘     └─────────────┘
```

## 配置说明

在 `.env` 文件中添加以下配置：

```bash
# 启用资金归集
SWEEPER_ENABLED=true

# 执行间隔（分钟）
SWEEPER_INTERVAL_MINUTES=5

# 触发阈值：低于此金额（USD）不触发归集
SWEEP_THRESHOLD_USD=1.0

# 保留金额（用于支付 gas 费）
SWEEP_RESERVE_AMOUNT_SOL=0.005    # Solana
SWEEP_RESERVE_AMOUNT_TRX=1.0      # Tron

# 重试配置
SWEEP_MAX_RETRY_COUNT=3
SWEEP_RETRY_DELAY_MINUTES=10

# 主钱包地址（必须配置）
SOLANA_MASTER_WALLET=your_solana_wallet_address
TRON_MASTER_WALLET=your_tron_wallet_address
```

## 归集记录模型

### SweepRecord

| 字段 | 类型 | 说明 |
|------|------|------|
| id | Integer | 主键 |
| address_id | Integer | 关联的 PaymentAddress ID |
| order_id | String | 关联的 Order ID |
| chain | String | 链类型 (solana/tron) |
| asset_code | String | 资产代码 (SOL/USDT_TRC20) |
| from_address | String | 来源地址 |
| to_address | String | 目标主钱包地址 |
| amount | Numeric | 归集金额 |
| amount_usd | Numeric | 对应的美元价值 |
| fee_amount | Numeric | 实际支付的手续费 |
| fee_asset | String | 手续费代币 |
| tx_hash | String | 交易哈希 |
| status | String | 状态 (pending/processing/completed/failed/retrying) |
| retry_count | Integer | 重试次数 |
| error_message | String | 失败原因 |
| created_at | DateTime | 创建时间 |
| started_at | DateTime | 开始处理时间 |
| completed_at | DateTime | 完成时间 |

### 状态流转

```
┌─────────┐    ┌───────────┐    ┌───────────┐    ┌───────────┐
│ PENDING │───▶│PROCESSING │───▶│ COMPLETED │    │           │
└─────────┘    └───────────┘    └───────────┘    │           │
      │                                          │           │
      │    ┌───────────┐    ┌───────────┐        │  ┌─────┐  │
      └───▶│   FAILED  │◀───│ RETRYING  │◀───────┘  │     │  │
           └───────────┘    └───────────┘           └─────┘  │
                  │                      ▲                   │
                  └──────────────────────┘                   │
                         达到最大重试次数                    │
                                                            │
                         所有状态的终点                      │
```

## 手续费计算

### Solana

| 资产类型 | 预估手续费 | 备注 |
|----------|-----------|------|
| SOL | ~0.00001 SOL | 基础转账费 |
| SPL Token | ~0.0005 SOL | 包含优先费 |

### Tron

| 资产类型 | 预估手续费 | 备注 |
|----------|-----------|------|
| USDT_TRC20 | ~20 TRX | 无能量时燃烧 TRX |
| TRX | ~1 TRX | 基础转账费 |

## API 查询

### 查询归集记录

```python
from app.models.sweep_record import SweepRecord
from sqlalchemy import select

# 查询所有归集记录
result = await session.execute(select(SweepRecord))
sweeps = result.scalars().all()

# 查询特定地址的归集记录
result = await session.execute(
    select(SweepRecord).where(SweepRecord.from_address == address)
)
sweeps = result.scalars().all()

# 查询待处理的归集
result = await session.execute(
    select(SweepRecord).where(
        SweepRecord.status.in_(['pending', 'retrying'])
    )
)
pending = result.scalars().all()
```

## 监控与告警

### 日志关键字

- `Starting sweep cycle` - 开始新的归集周期
- `Found X addresses to check` - 发现待检查地址
- `Created sweep record` - 创建新的归集记录
- `Sweep #N completed` - 归集完成
- `Sweep #N retry failed` - 归集重试失败

### 需要关注的告警

1. 大量归集失败 (`status='failed'`)
2. 归集任务长时间未执行
3. 主钱包地址未配置
4. 手续费估算与实际差距过大

## 手动触发归集

```python
from app.workers.sweeper import run_sweeper_cycle

# 手动执行完整的归集周期
await run_sweeper_cycle()
```

## 注意事项

1. **主钱包地址必须配置**：否则无法进行归集
2. **保留金额设置**：确保地址有足够余额支付 gas 费
3. **阈值设置**：过低的阈值会导致频繁小额转账，增加成本
4. **私钥安全**：私钥使用 ENCRYPTION_MASTER_KEY 加密存储
5. **重试机制**：失败任务会自动重试，达到最大次数后标记为失败

## 待实现功能

- [ ] Solana 真实转账签名逻辑
- [ ] Tron 真实转账签名逻辑
- [ ] 手续费精确估算（根据网络状况）
- [ ] 归集任务手动触发 API
- [ ] 归集统计报表
