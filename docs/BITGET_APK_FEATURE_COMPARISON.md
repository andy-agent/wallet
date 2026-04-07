# Bitget APK Feature Comparison

## Source

- Reference APK: `/Users/cnyirui/git/projects/liaojiang/BitgetWallet9400_bgwapp.apk`
- Package: `com.bgwapp.official`
- Version: `9.40.0`
- Entry activity: `com.bgwapp.official.MainActivity`
- Observed stack: Flutter-based app with large `flutter_assets` payload

## High-Level IA Inference

The APK and extracted assets indicate a wallet-first product shell with a bottom-tab-driven structure. The most visible primary areas are:

- `Home`
- `Wallet`
- `Swap`
- `Discover / DApp / Browser`
- `Quote / Market`
- `Profile / Me`

The asset set strongly suggests a home dashboard plus wallet/assets center with secondary modules surfaced as cards and shortcuts rather than as isolated deep menus.

## Observable Bitget Feature Groups

### 1. Wallet / Asset Center

Evidence from assets:

- `all_token`
- `asset_information`
- `address_detail_page_bg`
- `wallet_menu_receive`
- `wallet_menu_send`
- `wallet_menu_swap`
- `wallet_add_asset`
- `wallet_change_chains`
- `token_risk_*`

Inferred capability:

- asset list
- token detail
- receive / send
- multi-chain switch
- add asset
- risk labeling

### 2. Wallet Security / Backup / Cloud Wallet

Evidence:

- `backup_keyword`
- `backup_safe`
- `backup_write`
- `wallet_safe_guide_*`
- `wallet_backup_tip`
- `cloudwallet`
- `cloud_wallet_*`
- `wallet_setting_question`

Inferred capability:

- mnemonic backup guidance
- secure setup / recovery education
- cloud wallet onboarding
- wallet security settings and warnings

### 3. Swap / Trade / Quote

Evidence:

- `BTN_exchange`
- `icon_bitswap`
- `swap_*`
- `trade_filter_icon`
- `quote_hot_picks_*`
- `tradingview_advanced/assets/chart.html`

Inferred capability:

- token swap
- quote / market style pages
- charting / price views
- limit/contract/rwa swap tabs

### 4. DApp / Browser / Web3 Connection

Evidence:

- `dapp_browser_tab_empty`
- `dapp_favorite`
- `dapp_no_wallet`
- `dapp_prevention`
- `top_dapp_list_icon`
- `walletconnect`
- manifest deep links including `dapp` scheme
- injected chain JS for many ecosystems in `packages/core_js/assets/chain/*`

Inferred capability:

- dapp browser
- wallet connect / chain injection
- favorite dapps
- web3 session handling

### 5. NFT / Collections

Evidence:

- `default_placeholder_nft_banner`
- `home_nft_item_avatar_bg`
- `icon_nft_have_nft`
- `icon_nft_have_no_nft`
- `receive_nft`
- `pro_wallet_other_assets_nft_*`

Inferred capability:

- NFT tab or NFT sections inside wallet/home
- NFT receive / display

### 6. Invite / Reward / Growth

Evidence:

- `card_invite`
- `card_reward`
- `icon_invite`
- `invite_card_bg`
- `ic_home_invitation`

Inferred capability:

- invitation / referral
- rewards / task-like growth surfaces

### 7. Card / Fiat / Payment

Evidence:

- extensive `card_*` asset family
- `fiat24`
- `visa`
- recharge / spending / transfer / withdraw icons
- Google Wallet integration metadata

Inferred capability:

- card management
- fiat-linked recharge/spending flows
- card detail / limits / fees

### 8. Earn / Yield

Evidence:

- `earn_*`
- `index_earn_icon`
- `earn_withdraw_arrow`

Inferred capability:

- earn / yield entry and product detail pages

### 9. Prediction / Market Specials

Evidence:

- `bgw_home_prediction_market_*`
- `pro_wallet_other_assets_prediction_market_icon`

Inferred capability:

- featured market/prediction surfaces promoted from home/wallet

## Comparison With Our Project

### Already Matching or Directly Mappable

- `Wallet / Assets`
  - We already have wallet-facing compose pages and payment/account pages.
- `Receive / Send / Payment confirmation`
  - We already have order/payment/wallet confirmation flows.
- `Growth / Invite / Reward-like surfaces`
  - We already have invite, commission, withdrawal.
- `Profile / Legal / Settings`
  - We already have profile/legal/settings equivalents.
- `Order / Payment state flows`
  - We already have plan/order/payment/subscription flows.

### Needs IA Remapping, Not Brand-Copying

- `Quote / Market`
  - In our product, this slot should be replaced by `VPN`.
- `Discover / DApp / Browser`
  - We can map this visual shell to discovery/growth/legal/support style content.
- `Earn`
  - We do not need to reproduce yield products literally; we should reuse the visual/card patterns for growth or benefits content where appropriate.
- `Card / Fiat`
  - We should borrow visual structure, not directly clone card business scope unless required later.
- `NFT`
  - We can reuse layout language, but NFT as a first-class business module is not currently core to our product.

### Not Suitable for Direct Product-Scope Copy

- third-party card programs
- full exchange/market feature set
- DApp browser chain injection breadth
- NFT marketplace depth
- fiat integrations tied to Bitget ecosystem

These are visual/IA references, not immediate implementation commitments.

## IA Decision For Our Product

Bitget is wallet-first. Our product is VPN-first with wallet/payment support.

Therefore the correct adaptation is:

- `Home`
  - dashboard / highlights / status / promotions
- `Wallet`
  - assets / receive / send / order payment entry
- `VPN`
  - first-class bottom-tab module replacing Bitget's market/quote position
- `Discover`
  - growth / invite / legal / support / feature cards
- `Profile`
  - account / settings / session / app info

This keeps Bitget's shell grammar while preserving our actual business core.

## Implementation Consequence

For the ongoing Android UI refactor:

- `4j0.15`
  - should own the Bitget-like app shell and bottom nav
- `4j0.17`
  - should own wallet/home visual reconstruction
- `4j0.18`
  - should own VPN as the center business tab and purchase/order flow
- `4j0.19`
  - should map discover/growth/profile/legal into the Bitget-like secondary surfaces
- `4j0.16`
  - should verify the whole shell on-device after the above land

## Final Conclusion

The user's observation is directionally correct: most of Bitget's visible product grammar can be mapped to our needs.

But the product-core substitution is mandatory:

- Bitget's core = wallet / market / web3
- Our core = VPN / purchase / subscription / wallet-assisted payment

So the correct 1:1 imitation is:

- copy the shell, pacing, hierarchy, card language, and tab grammar
- replace the market core slot with VPN
- map wallet/growth/profile/legal onto our existing business modules
