# Custom Token Search Provider Setup

## Purpose
`custom token search` 后端已拆成三套 provider：

- `Solana -> Jupiter`
- `EVM -> CoinGecko`
- `TRON -> TRONSCAN`

Android 继续只调用：

- `GET /api/client/v1/wallet/custom-tokens/search?chainId=...&query=...`

## Required Environment Variables

在运行 `cryptovpn-backend` 的环境中，需要通过安全渠道注入以下变量：

- `JUPITER_API_KEY`
- `COINGECKO_API_KEY`
- `TRONSCAN_API_KEY`

可选运行参数：

- `TOKEN_SEARCH_TIMEOUT_MS`
- `TOKEN_SEARCH_CACHE_TTL_MS`
- `JUPITER_BASE_URL`
- `JUPITER_ORGANIZATION_ID`
- `COINGECKO_BASE_URL`
- `TRONSCAN_BASE_URL`
- `TRONSCAN_APPLICATION_NAME`
- `DEXSCREENER_BASE_URL`

## Secure Handling

- 实际 Key 不得提交到 git。
- 实际 Key 只允许放在：
  - 服务器环境变量
  - 本地受控 `.env.local`
  - 团队安全凭据管理系统
- 仓库文档只记录变量名、用途和官方文档入口。

2026-04-20 收到的 provider 凭据已由人类通过安全渠道提供，后续如需上线启用，直接注入上述环境变量即可，不要把明文再写回仓库。

## Provider Notes

### Jupiter
- 用途：Solana token keyword search / mint resolve
- 账户侧还需要保留：
  - `JUPITER_ORGANIZATION_ID`
- 参考文档：
  - [Jupiter Tokens API](https://dev.jup.ag/docs/tokens/token-information)
- 当前实现：
  - 关键词搜索走 Jupiter
  - 精确 mint 地址走 Jupiter address resolve

### CoinGecko
- 用途：EVM token keyword search / contract resolve
- 当前提供的是 Demo Key 时：
  - root URL 使用 `https://api.coingecko.com/api/v3`
  - header 使用 `x-cg-demo-api-key`
- 若后续升级为 Pro Key：
  - 再切回 `https://pro-api.coingecko.com/api/v3`
  - header 使用 `x-cg-pro-api-key`
- 参考文档：
  - [CoinGecko Token Data by Address](https://docs.coingecko.com/reference/token-data-contract-address)
  - [CoinGecko AI Agent Hub](https://docs.coingecko.com/docs/ai-agent-hub)
- 当前实现：
  - 精确合约地址只走 CoinGecko
  - 关键词先走 CoinGecko
  - CoinGecko 若未返回可持久化精确地址，再退回 DexScreener

### TRONSCAN
- 用途：TRON token keyword search / contract resolve
- 账户侧还需要保留：
  - `TRONSCAN_APPLICATION_NAME`
- 参考文档：
  - [TRONSCAN Search API](https://docs.tronscan.org/api-endpoints/homepage-and-search)
- 当前实现：
  - 关键词搜索走 TRONSCAN
  - 精确合约地址走 TRONSCAN contract/token metadata

## Runtime Behavior

- 任一 provider 单独失败时，只记 warning，不抛 500。
- 搜索接口失败时返回空数组。
- 缓存键格式：
  - `provider + chainId + mode(address|keyword) + normalizedQuery`
- 默认 TTL：5 分钟
