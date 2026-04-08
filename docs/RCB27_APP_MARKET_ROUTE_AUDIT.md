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
