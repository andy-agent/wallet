# RCB26 Bitget White Route Capture

Date: 2026-04-08
Device: `ba2b016`
Target: current Bitget white-theme `Market / 行情` family on the connected real device

## Route Inventory

1. Contract market shell home
Path: `/tmp/liaojiang-screens/20260408-214141-bitget-current.png`
- White background, not dark mode.
- Top chrome is `全局搜索` plus utility icons.
- First-level market family tabs are `代币 / 合约 / 股票`.
- Secondary board row is real market taxonomy, not local mock chips.
- Quote list is dense and list-first. The page is not built around hero cards.

2. Contract in-page search/filter sheet
Path: `/tmp/liaojiang-screens/20260408-214706-bitget-market-token-shell-home.png`
- Search/filter is a sheet layered over the contract market shell.
- The overlay still keeps the quote list visible and searchable.
- This confirms the current Bitget route is multi-state within the market family, not a single static list.

3. Stock shell home
Path: `/tmp/liaojiang-screens/20260408-214205-bitget-03-token-tab.png`
- `股票` is a first-level tab inside the same market family.
- The page is white and card-light, with sector cards at the top and a stock list below.
- Stock boards include `收藏 / 涨幅榜 / 交易最多 / 中概股 / ETFs ...`.
- This is materially different from the current Android Market dashboard.

4. Stock hot list sub-route
Path: `/tmp/liaojiang-screens/20260408-214520-bitget-market-stock-home-2.png`
- Returning from stock detail lands on a narrower stock list route.
- The stock family has its own internal route hierarchy, not just one stock tab inside a single page.

5. Stock quote detail, `行情` tab
Path: `/tmp/liaojiang-screens/20260408-214205-bitget-04-token-page.png`
- Detail page uses top `行情 / 详情` tabs.
- Main body is price summary, right-side KPI table, time granularity row, K-line chart, and bottom `交易` CTA.
- The chart area is the center of the page. There is no explanatory hero copy.

6. Stock detail, `详情` tab
Path: `/tmp/liaojiang-screens/20260408-214453-bitget-stock-detail-wdcon.png`
- `详情` is a separate top tab, not a lower card section under the chart page.
- The page becomes a clean information sheet with key data and one bottom CTA.

7. Contract quote detail, `NVDAUSDT`
Path: `/tmp/liaojiang-screens/20260408-214205-bitget-09-nvda-detail.png`
- Contract detail remains under the market family and keeps `行情 / 详情`.
- It has real-time quote summary, K-line, indicator strip, and dual trading CTAs.
- The page chrome, spacing, and KPI layout are white-theme and highly structured.

8. Contract trade sub-route
Path: `/tmp/liaojiang-screens/20260408-214205-bitget-10-nvda-detail-tab.png`
- From contract detail, Bitget can continue into a trade-oriented route with order book and order-entry controls.
- This confirms that market detail is not a dead-end info page; it is a real functional route family.

## Structural Findings

- Current Bitget market baseline is white-theme on this device.
- `代币 / 合约 / 股票` are real first-level sections, not decorative local chips.
- The market family is route-rich:
  - top-level shell
  - search/filter state
  - section-specific list states
  - quote detail with `行情 / 详情`
  - contract trade continuation
- Quote detail is chart-first and KPI-dense.
- `详情` is a separate top-level detail tab, not extra commentary cards under the quote chart.

## Functional Findings

- Search is real and route-bearing.
- Section tabs change the content family, not just local sort labels.
- Stock and contract routes diverge after selection.
- Quote rows enter real details with stable market identity.
- Contract details expose real trading affordances, not a placeholder CTA.

## Baseline Implications For RCB26

- Android `Market` cannot stay a custom hero-first dashboard.
- Android `Market` must be re-based around:
  - white theme
  - first-level `代币 / 合约 / 股票`
  - real list-first shell
  - route-specific list states
  - `行情 / 详情` dual-tab quote detail
- Any implementation that keeps the current dark dashboard and synthetic boards will remain visibly non-1:1.
