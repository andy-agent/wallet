# RCB27 App Market Route Audit

Date: 2026-04-08
Device: `ba2b016`
Compared Against: `docs/RCB26_BITGET_WHITE_ROUTE_CAPTURE.md`

## Current App Route Evidence

1. Current Market home after app launch
Path: `/tmp/liaojiang-screens/20260408-214755-liaojiang-launch.png`
- The page is still dark and gradient-heavy.
- It opens as a custom dashboard with explanatory copy, spotlight cards, and local chips.
- The top-level visible categories are `自选 / 热门 / AI / 美股 / 夜盘`, not Bitget's `代币 / 合约 / 股票`.
- The lower board row is local `热门 / 涨幅榜 / 成交额 / 新上线`, also not Bitget's current white market route grammar.

2. Current XML evidence on the same screen
Path: `/tmp/liaojiang-screens/liaojiang-current.xml`
- UI dump confirms the same custom route model:
  - `Market`
  - `CRWVon 拉升到榜首`
  - `自选 / 热门 / AI / 美股 / 夜盘`
  - `成交额 / 新上线`
- This is not a list-first market shell and not a white-theme Bitget market baseline.

3. Current detail-route attempt from Market
Path: `/tmp/liaojiang-screens/20260408-214846-liaojiang-market-detail.png`
- Tapping into the current market surface led to a blank white screen.
- This is not a functioning Bitget-style quote detail.

4. Stable follow-up after the blank route
Path: `/tmp/liaojiang-screens/20260408-214917-liaojiang-market-detail-blank-stable.png`
- After waiting, the device ended up showing the app preview / recents state instead of a usable quote-detail page.
- Current Market-to-detail routing is therefore unstable in real-device use.

## Direct Differences vs Bitget

### 1. Theme

- Bitget current baseline is white.
- Our current Market is still dark with a custom gradient shell.
- This is an implementation error on our side, not a requirement misunderstanding.

### 2. Top-Level Market Semantics

- Bitget: `代币 / 合约 / 股票` as first-level market sections.
- App: `自选 / 热门 / AI / 美股 / 夜盘` as local chips over a custom dashboard.
- The current app does not preserve Bitget's market information architecture.

### 3. Page Structure

- Bitget starts from a dense market list shell with real boards and live rows.
- App starts from a hero-first, custom-card dashboard.
- Bitget detail uses `行情 / 详情` as top tabs.
- App does not reliably reach a real quote-detail page on device.

### 4. Route Depth

- Bitget market family contains:
  - shell home
  - search/filter state
  - section-specific stock/contract list states
  - quote detail
  - contract trade continuation
- App currently exposes:
  - one custom overview dashboard
  - unstable detail navigation

### 5. Real Functionality

- Bitget search, list entry, detail tabs, chart, indicators, and trade continuation are real.
- App still mixes custom local chips and dashboard semantics with an unstable detail route.
- On-device behavior currently fails the user's "not just pixel copy, functions must be real" requirement.

## Token Lane Gap Matrix

This section follows the route order explicitly required by the user: finish the `行情 -> 代币` lane first, then move to the next lane.

1. `行情 -> 代币` root
Bitget baseline:
- White shell.
- First-level tab is the real `代币` lane.
- Root contains `金狗雷达 / 扫链 / 监控 / 市场热度` plus board row `Hot Picks / 全链 / 新币榜 ...`.

Current app status: `wrong page`
- App opens a dark custom dashboard.
- Visible taxonomy is `自选 / 热门 / AI / 美股 / 夜盘`, not a `代币` lane root.
- There is no route-faithful token root matching the Bitget entry page.

2. `代币 -> 金狗雷达`
Bitget baseline:
- Independent clickable entry from the token root.

Current app status: `no route`
- No corresponding entry exists on Market home.
- No sign of a token-radar route in the current Android Market code or device behavior.

3. `代币 -> 扫链`
Bitget baseline:
- Independent clickable entry from the token root.

Current app status: `no route`
- No equivalent entry or downstream screen is exposed by the app Market lane.

4. `代币 -> 监控`
Bitget baseline:
- Dedicated route with its own top tabs: `推荐牛人 / 交易动态 / 关注的人`.

Current app status: `no route`
- Current app Market does not expose a monitoring/watchlist people-signal route.
- Nothing on the current Market page maps to that page structure or function.

5. `代币 -> 市场热度`
Bitget baseline:
- Separate clickable section with heat cards and downstream ranking navigation.

Current app status: `visual placeholder only`
- Current app has spotlight/hero cards, but they are not the same route and not the same market-heat semantics.
- This is the closest visual echo, but it is not a route-equivalent implementation.

6. `代币 -> Hot Picks / 全链 / 新币榜`
Bitget baseline:
- Real token board row under the token root.
- Each board changes the token list family and continues into real token entries.

Current app status: `wrong page`
- App has local boards such as `热门 / 涨幅榜 / 成交额 / 新上线`.
- Those boards are not the same taxonomy and do not correspond 1:1 to `Hot Picks / 全链 / 新币榜`.
- The page is therefore not just missing labels; the board model itself is wrong.

7. `代币 -> 某币种(BURNIE-like) -> 交易`
Bitget baseline:
- Selecting a representative token continues into a real trade-family page with chart, buy/sell state, amount chips, fee block, and bottom CTA.

Current app status: `wrong page`
- Current Market-to-detail navigation on device falls into a blank white screen or unstable route.
- The app therefore does not currently provide a usable token-detail or token-trade continuation from Market.

7a. `BURNIE-like -> 概览 / 交易历史 / 持币地址 / 分享预览`
Bitget baseline:
- The representative token route contains multiple upper information tabs before and alongside the lower trade-family routes.
- Human screenshots already show `概览`, `交易历史`, `持币地址`, and a share-preview state.

Current app status: `no route`
- The app does not expose a token-detail route deep enough to support these subpages.
- Because the current route breaks before a stable token detail exists, all of these substates are currently absent.

8. `某币种 -> 交易 -> 跨链闪兑`
Bitget baseline:
- Explicit titled route `跨链闪兑`.

Current app status: `no route`
- No equivalent downstream route exists from the current app Market token lane.

9. `某币种 -> 交易 -> 限价`
Bitget baseline:
- Explicit titled route `限价`.

Current app status: `no route`
- No equivalent downstream route exists from the current app Market token lane.

10. `某币种 -> 交易 -> 合约`
Bitget baseline:
- Explicit titled route `合约`.

Current app status: `no route`
- No equivalent downstream route exists from the current app Market token lane.

11. `某币种 -> 交易 -> 股票`
Bitget baseline:
- Trade dock exposes `股票` as a sibling route under the same token trade family.

Current app status: `no route`
- No equivalent sibling trade route exists under the current app Market token flow.

## What This Changes

- The gap is no longer only “theme + overview layout”.
- The app is missing most of the token lane route tree itself.
- Implementation must therefore start by creating a route-correct token lane skeleton before trying to polish detail visuals.

## Contract Lane Early Gap Notes

1. `行情 -> 合约` root
Bitget baseline:
- White contract root with real board stack `热门 / 新上线 / 全部 / 股票 ...`.

Current app status: `wrong page`
- App still exposes the same dark custom Market dashboard instead of a distinct contract root lane.
- There is no route-faithful contract root with Bitget board taxonomy.

2. `合约 -> 代表性交易对 -> 交易 continuation`
Bitget baseline:
- Human screenshot already shows a real `SOLUSDT` contract trading continuation with chart and order-entry controls.

Current app status: `no route`
- Current app Market lane has no stable contract-trade continuation from a market symbol.
- Existing real-device detail transition is unstable and can fall into a blank white screen instead of a usable continuation route.

3. Outstanding contract-lane audit work

- `热门 / 新上线 / 全部 / 股票` board-by-board comparison is still pending.
- Contract lane should remain active in `bd` until those boards and downstream routes are audited route-by-route.

## Immediate Implementation Consequences

1. `rcb.25` must remove the forced dark baseline before Market parity can hold.
2. `rcb.26` must rebuild Market around the Bitget white list-first route family, not the current custom dashboard.
3. `rcb.27` must fix the Market-to-detail route first:
   - no blank white screen
   - stable instrument identity
   - real `行情 / 详情` structure
   - real route transitions instead of UI-only sections

## Conclusion

The current mismatch is caused by our implementation, not by user wording. The app is not close to the current Bitget white-theme market family either visually or functionally:

- wrong theme baseline
- wrong section semantics
- wrong shell structure
- wrong route depth
- broken detail navigation on real device
