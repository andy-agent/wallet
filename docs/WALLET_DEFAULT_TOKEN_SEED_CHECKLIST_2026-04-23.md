# Wallet Default Token Seed Checklist

更新时间：2026-04-23

目的：先形成一份“按主流链初始化默认显示代币”的人工核验清单。此清单用于产品/运营人工确认后，再固化为 App 内置默认代币，不直接作为最终合约地址真相。

说明：

- 当前优先级是“减少用户首次进入钱包后手动添加主流币的次数”。
- 默认显示建议尽量保守，优先选择原生币、头部稳定币、链上公认主流资产。
- 所有合约地址在接入前必须再做一轮人工核验。
- `官方支持` 表示有较明确的官方发行或链侧支持信息；`候选` 表示业务上常见，但仍建议二次确认。

| 链 | 建议默认显示 | 备注 |
| --- | --- | --- |
| Ethereum | ETH, USDT, USDC | 直接按用户示例作为首选默认三枚 |
| BSC | BNB, USDT, FDUSD | `USDC` 在 BSC 上不建议直接默认成官方版，若要放入需单独核验资产来源 |
| Polygon | POL, USDC, WETH | 若业务更偏稳定币，也可把第三枚改成 `USDT`，但需二次核验 |
| Arbitrum | ETH, USDC, ARB | 若后续确认 USDT 业务使用频率更高，可替换 `ARB` |
| Base | ETH, USDC, cbBTC | `AERO` 也是常见候选，需按业务目标二选一 |
| Optimism | ETH, USDC, OP | 同类规则下可备选 `USDT`，但建议先确认链上真实使用频率 |
| Avalanche C | AVAX, USDC, USDT | 这一组相对稳定，适合直接进入人工核验名单 |
| Solana | SOL, USDC, USDT | Solana 钱包首次使用最常见三枚 |
| TRON | TRX, USDT, USDD | `USDC` 不建议默认放入当前版本首批名单，除非业务确认要兼容历史存量 |

## 建议接入顺序

第一批建议先做：

1. Ethereum: `ETH / USDT / USDC`
2. Solana: `SOL / USDC / USDT`
3. TRON: `TRX / USDT`
4. Avalanche C: `AVAX / USDC / USDT`

第二批建议在人工确认后做：

1. BSC: `BNB / USDT / FDUSD`
2. Arbitrum: `ETH / USDC / ARB`
3. Base: `ETH / USDC / cbBTC`
4. Optimism: `ETH / USDC / OP`
5. Polygon: `POL / USDC / WETH`

## 人工核验项

在写入 App 初始化前，逐条确认以下内容：

1. 合约地址是否为官方发行或业务认可版本。
2. 代币精度是否正确。
3. 代币图标源是否稳定。
4. 是否需要默认展示价格，还是只展示资产占位。
5. 是否允许在对应链的 `token_manager` 中默认可见但余额为 0。

## 参考来源

- Circle USDC 主网合约与支持链：<https://developers.circle.com/stablecoins/usdc-on-main-networks>
- Circle CCTP 支持主网列表：<https://developers.circle.com/stablecoins/supported-domains>
- Circle Multi-Chain USDC 主页：<https://www.circle.com/multi-chain-usdc>
- Tether 官方支持协议页：<https://tether.to/en/supported-protocols/>
