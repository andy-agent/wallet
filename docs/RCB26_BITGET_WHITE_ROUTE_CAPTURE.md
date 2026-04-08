# RCB26 Bitget White Route Capture

Date: 2026-04-08
Device: `ba2b016`
Target: current Bitget white-theme `Market / 行情` family on the connected real device

## Baseline Sources

### Authoritative Human Screenshots

Pulled from the device gallery at `/sdcard/DCIM/Screenshots` into `/tmp/phone-screens`:

- `/tmp/phone-screens/Screenshot_2026-04-08-22-04-23-54.jpg`
- `/tmp/phone-screens/Screenshot_2026-04-08-22-05-00-66_d45d6ff0b77b937b71a8cf17b79cd5c0.jpg`
- `/tmp/phone-screens/Screenshot_2026-04-08-22-05-07-56_d45d6ff0b77b937b71a8cf17b79cd5c0.jpg`
- `/tmp/phone-screens/Screenshot_2026-04-08-22-05-13-19_d45d6ff0b77b937b71a8cf17b79cd5c0.jpg`
- `/tmp/phone-screens/Screenshot_2026-04-08-22-05-21-49_d45d6ff0b77b937b71a8cf17b79cd5c0.jpg`
- `/tmp/phone-screens/Screenshot_2026-04-08-22-05-30-85_d45d6ff0b77b937b71a8cf17b79cd5c0.jpg`

These screenshots are the authoritative route anchors for `rcb.26.2`.

### Supporting Valid ADB Captures

The following ADB captures were verified before route drift and still match Bitget:

- `/tmp/liaojiang-screens/20260408-bitget-market-home-reset-1.png`
- `/tmp/liaojiang-screens/20260408-bitget-market-contract.png`
- `/tmp/liaojiang-screens/20260408-bitget-market-stock.png`
- `/tmp/liaojiang-screens/20260408-bitget-market-search-open.png`
- `/tmp/liaojiang-screens/20260408-bitget-market-search-nvda.png`
- `/tmp/liaojiang-screens/20260408-bitget-market-stock-favorites.png`

### Invalidated Automatic Captures

The following later files are not valid Bitget baseline evidence because automation drifted into the local test app `com.v2ray.ang.fdroid`:

- `/tmp/liaojiang-screens/20260408-bitget-market-root-token.png`
- `/tmp/liaojiang-screens/20260408-bitget-market-root-contract.png`
- `/tmp/liaojiang-screens/20260408-bitget-market-root-stock.png`
- `/tmp/bitget-uix/20260408-bitget-market-root-token.xml`
- `/tmp/bitget-uix/20260408-bitget-market-root-contract.xml`
- `/tmp/bitget-uix/20260408-bitget-market-root-stock.xml`

Do not use those files for parity decisions.

## Confirmed Route Tree

### Lane A: `行情 -> 代币`

1. `行情 -> 代币` root
Source: `/tmp/liaojiang-screens/20260408-bitget-market-home-reset-1.png`

- White market shell with top `全局搜索`.
- First-level tabs are `代币 / 合约 / 股票`.
- `代币` root contains first-screen callable entries `金狗雷达 / 扫链 / 监控`.
- `市场热度` is a separate clickable section with cards.
- Board row is real: `收藏 / Hot Picks / 全链 / 新币榜 / 涨幅榜 ...`.

2. `行情 -> 代币 -> 监控`
Source: `/tmp/phone-screens/Screenshot_2026-04-08-22-04-23-54.jpg`

- This is a dedicated route, not just a token list filter.
- Top tabs are `推荐牛人 / 交易动态 / 关注的人`.
- Page structure is list-first and signal-heavy.
- This route was missing from the earlier automatic capture set.

3. `行情 -> 代币 -> Hot Picks -> 某币种(BURNIE) -> 交易` family
Sources:

- `/tmp/phone-screens/Screenshot_2026-04-08-22-05-21-49_d45d6ff0b77b937b71a8cf17b79cd5c0.jpg`
- `/tmp/phone-screens/Screenshot_2026-04-08-22-05-30-85_d45d6ff0b77b937b71a8cf17b79cd5c0.jpg`

- Human screenshots show that after selecting a token such as `BURNIE`, Bitget continues into a trade-family page, not just a quote detail dead-end.
- The page has token header, chart, buy/sell segmented control, amount chips, fee section, and a bottom CTA.
- This route proves the required parity chain extends beyond `行情详情`.

3a. `行情 -> 代币 -> Hot Picks`
Source: `/tmp/phone-screens/Screenshot_2026-04-08-21-54-47-03.jpg`

- Human screenshot shows the token root with `Hot Picks` selected and a live list that includes `BURNIE`.
- This confirms the exact representative-coin path used by the user: `行情 -> 代币 -> Hot Picks -> BURNIE`.

3b. `BURNIE -> 概览`
Source: `/tmp/phone-screens/Screenshot_2026-04-08-21-55-52-18.jpg`

- `BURNIE` has its own multi-tab quote/detail family before entering the lower trade dock.
- Visible top tabs include `概览 / 交易历史 / 持币地址 / 盈利地址 / 资金池 ...`.
- The `概览` tab contains AI-style commentary and transaction summary modules.

3c. `BURNIE -> 交易历史`
Source: `/tmp/phone-screens/Screenshot_2026-04-08-21-56-16-22_d45d6ff0b77b937b71a8cf17b79cd5c0.jpg`

- `交易历史` is a distinct tab under the token route.
- The page combines chart, trade history filters, and lower `交易 / 极速交易` actions.

3d. `BURNIE -> 持币地址`
Source: `/tmp/phone-screens/Screenshot_2026-04-08-21-56-53-45_d45d6ff0b77b937b71a8cf17b79cd5c0.jpg`

- `持币地址` is another distinct tab under the same token route.
- This shows the token lane is not just a single chart page followed by trade; it has deeper information tabs and holder analytics.

3e. `BURNIE -> 分享预览`
Source: `/tmp/phone-screens/Screenshot_2026-04-08-21-59-41-97_d45d6ff0b77b937b71a8cf17b79cd5c0.jpg`

- Bitget exposes a share-preview state from the token route.
- This is another real substate missing from the earlier automatic capture set.

4. `BURNIE -> 交易 -> 跨链闪兑`
Source: `/tmp/phone-screens/Screenshot_2026-04-08-22-05-00-66_d45d6ff0b77b937b71a8cf17b79cd5c0.jpg`

- Title is explicitly `跨链闪兑`.
- Bottom dock exposes `闪兑 / 限价 / 合约 / 股票`.
- This is a concrete downstream route of the token trade family.

5. `BURNIE -> 交易 -> 限价`
Source: `/tmp/phone-screens/Screenshot_2026-04-08-22-05-07-56_d45d6ff0b77b937b71a8cf17b79cd5c0.jpg`

- Title is `限价`.
- Form is input-first, not chart-first.
- Same bottom trade dock remains visible.

6. `BURNIE -> 交易 -> 合约`
Source: `/tmp/phone-screens/Screenshot_2026-04-08-22-05-13-19_d45d6ff0b77b937b71a8cf17b79cd5c0.jpg`

- Title is `合约`.
- Layout changes again into a contract-trading surface with chart, order book, and position controls.
- This confirms the trade family has multiple sibling routes under the same token context.

7. Additional token-lane discovery subroute with network overlay
Sources:

- `/tmp/phone-screens/Screenshot_2026-04-08-22-02-42-14.jpg`
- `/tmp/phone-screens/Screenshot_2026-04-08-22-02-53-16_d45d6ff0b77b937b71a8cf17b79cd5c0.jpg`

- These screenshots show an additional token-lane list/discovery route with alert-style rows and a `切换网络` overlay.
- The route is still within the token market family because the overlay operates on token-network scope (`全部网络 / Solana / BNB chain / Ethereum / BASE`).
- This confirms the token lane is richer than just `监控 + Hot Picks + BURNIE trade family`; it also contains a discovery/feed state with network filtering.
- Exact entry label still needs one more controlled recapture before final naming, so this evidence is recorded as a confirmed token-lane subroute without over-claiming which root chip launched it.

### Lane B: `行情 -> 合约`

1. `行情 -> 合约` root
Source: `/tmp/liaojiang-screens/20260408-bitget-market-contract.png`

- White root page with first-level `代币 / 合约 / 股票`.
- Contract board row includes `热门 / 新上线 / 全部 / 股票 ...`.
- Representative pairs like `CLUSDT` are visible in the list.

Supplementary human screenshot:

- `/tmp/phone-screens/Screenshot_2026-04-08-22-01-06-16_d45d6ff0b77b937b71a8cf17b79cd5c0.jpg`

- This confirms the same contract root under the user's manual route sequence and shows a `热门`-style board state with pairs such as `SMSNUSDC`, `PUMPUSDC`, `JPYUSDC`, and `SOLUSDC`.

2. `行情 -> 合约 -> 某合约交易对 -> 交易 continuation`
Source: `/tmp/phone-screens/Screenshot_2026-04-08-22-05-13-19_d45d6ff0b77b937b71a8cf17b79cd5c0.jpg`

- Human screenshot confirms that the contract lane continues into a full trade surface, not just a read-only quote page.
- Sample visible pair is `SOLUSDT`.
- The route already exposes chart, order-entry widgets, and lower trade dock.

2a. `行情 -> 合约 -> 某合约交易对 -> 行情`
Sources:

- `/tmp/phone-screens/Screenshot_2026-04-08-22-00-39-98_d45d6ff0b77b937b71a8cf17b79cd5c0.jpg`
- `/tmp/phone-screens/Screenshot_2026-04-08-22-01-49-38.jpg`

- Human screenshots show contract quote-detail states for `SOL` and `SMSNUSDC`.
- These pages use the expected `行情 / 详情` structure with chart-first layout and bottom long/short trade actions.
- This confirms the contract lane includes a stable quote-detail layer before the deeper trading continuation.

3. `行情 -> 合约 -> 新上线`
Source: `/tmp/liaojiang-screens/20260408-bitget-contract-board-new.png`

- `新上线` is now captured as a distinct contract-lane board state.
- Visible rows include `SP500USDC`, `CLUSDT`, `BZUSDT`, and `EDGEUSDT`.
- This confirms the board materially changes the contract list payload.

4. `行情 -> 合约 -> 全部`
Source: `/tmp/liaojiang-screens/20260408-bitget-contract-board-all.png`

- `全部` is a separate board state under the contract lane.
- Visible rows include `BTCUSDT`, `ETHUSDT`, `SOLUSDT`, `XAUUSDT`, and `XAGUSDT`.
- This broadens beyond the narrower `新上线` list and confirms a distinct route/list state.

5. `行情 -> 合约 -> 股票`
Source: `/tmp/liaojiang-screens/20260408-bitget-contract-board-stock.png`

- `股票` here is a board inside the contract lane, not the top-level stock lane.
- Visible rows include `TSLAUSDC`, `SNDKUSDC`, `NVDAUSDC`, `HOODUSDC`, and `INTCUSDC`.
- This confirms the contract lane carries its own stock-derived subroute semantics.

6. Remaining captures still required under this lane

- representative-row jump from contract board list into downstream quote detail under controlled recapture

The contract lane is now mostly mapped; remaining work is narrow and focused on one clean board-list-to-detail transition.

### Lane C: `行情 -> 股票`

Temporary product note:

- On 2026-04-08, the user explicitly decided that the stock lane may remain blank for now.
- The evidence below is retained as future restoration input and is not part of the current must-deliver parity scope.

1. `行情 -> 股票` root
Source: `/tmp/liaojiang-screens/20260408-bitget-market-stock.png`

- White root page with sector cards and a stock-specific board row.
- Stock boards include `收藏 / 涨幅榜 / 交易最多 / 中概股 / ETFs ...`.

Supplementary live probe:

- `/tmp/liaojiang-screens/current_probe.png`

- This later live probe confirms the stock lane can sit on a `涨幅榜` board state while keeping the same white shell and board taxonomy.

2. `行情 -> 股票 -> 搜索 -> NVDAon`
Sources:

- `/tmp/liaojiang-screens/20260408-bitget-market-search-open.png`
- `/tmp/liaojiang-screens/20260408-bitget-market-search-nvda.png`

- Search is route-bearing and returns stock entries such as `NVDAon`.
- This is not a local-only filter over an already loaded list.

3. `行情 -> 股票 -> 某标的详情`
Source: `/tmp/liaojiang-screens/20260408-bitget-market-stock-favorites.png`

- Automatic capture reached a real stock-detail page (`CRWVon / CoreWeave`) with top `行情 / 详情`.
- The route confirms the stock lane also continues into deeper detail and trade states.

## Comparison Against The Earlier Automatic Capture Set

- Earlier automatic work correctly identified the white theme and the three first-level sections.
- Earlier automatic work missed user-provided high-value routes:
  - `代币 -> 监控`
  - `Hot Picks -> BURNIE -> 交易`
  - `交易 -> 跨链闪兑 / 限价 / 合约`
  - the need to finish one lane completely before switching to another lane
- Earlier automatic work also became polluted by app drift into `com.v2ray.ang.fdroid`; those files are now explicitly invalidated above.

## Baseline Implications For RCB26 / RCB27

- Parity work must follow route lanes, not just isolated screens.
- The required order is:
  1. finish `行情 -> 代币` lane from root through user-confirmed downstream routes
  2. then finish `行情 -> 合约` lane
  3. then finish `行情 -> 股票` lane
- Android `Market` is not allowed to stop at a mock list + mock detail.
- Functional parity must include the route continuations after token or quote selection, not only top-level market browsing.
