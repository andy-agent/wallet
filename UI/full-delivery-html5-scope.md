# Full Delivery HTML5 Rebuild Scope

## Goal

以最新 UI 视觉稿为唯一事实源，重建一套独立的高保真 HTML5 页面集。该页面集用于人类先验收视觉与结构，再作为后续 Android / Compose 全量重做的实现蓝本。

## Source of Truth

### Visual truth source

1. `/Users/cnyirui/Downloads/cryptovpn_p0_ui_pack.zip`
   - 含 11 张 P0 单页 PNG
   - 含 11 个对应动态 HTML 页面
   - 含 `P0_overview.png`
2. `/Users/cnyirui/Downloads/cryptovpn_ui_master_pack_p0_p1_p2.zip`
   - 含 P1 的 16 张单页 PNG
   - 含 P2 的 12 张单页 PNG
   - 含 `P1_overview.png`、`P2_overview.png`
3. `/Users/cnyirui/Downloads/cryptovpn_compose_full_delivery`
   - 不是视觉稿包
   - 用作路由、页面命名、阶段分组、参数定义的结构真源

### Important interpretation

- `cryptovpn_compose_full_delivery` 决定页面和路由结构。
- PNG 资源包决定视觉表现。
- 两者冲突时：
  - 路由命名、页面范围、参数，以 `full delivery` 为准
  - 视觉层级、间距、底栏、背景、卡片、图表、模块样式，以 PNG 为准

## Route and Visual Coverage

- `full delivery` routed pages: `51`
- 全局弹层: `1` (`SessionEvictedDialog`)
- 当前已有最新 PNG 对应的 routed pages: `38`
- 当前仅有代码模板、没有最新 PNG 视觉稿的 routed pages: `13`
- 非 routed 视觉参考页: `1`
  - `04_unified_home.png`

## Structural Cues From Full Delivery

- 分阶段导航骨架固定为：
  - `P0`
  - `P1`
  - `P2 Core`
  - `P2 Extended`
- 主导航是固定的 5-tab：
  - `vpn_home`
  - `plans`
  - `wallet_home`
  - `invite_center`
  - `profile`
- 全局弹层固定为：
  - `SessionEvictedDialog`
- 页面实现模式不是全部独立页面：
  - 5 个 P0 页面是手写重场景页
  - 其余大部分页面属于共享模板页体系
- HTML5 重建不能只还原截图，还要保留这一层信息架构：
  - 主底栏切换
  - 页面分阶段关系
  - 全局状态弹层入口
  - 共享卡片/表单/列表/hero 模块的可复用性

## Routed Pages With Confirmed Latest PNG

### P0 routes

| Route | Visual asset |
|---|---|
| `splash` | `cryptovpn_p0_ui/01_splash.png` |
| `email_login` | `cryptovpn_p0_ui/02_login.png` |
| `wallet_onboarding` | `cryptovpn_p0_ui/03_wallet_onboarding.png` |
| `vpn_home` | `cryptovpn_p1_ui/05_vpn_home.png` |
| `wallet_home` | `cryptovpn_p0_ui/07_wallet_home.png` |
| `force_update` | `cryptovpn_p1_ui/01_force_update.png` |
| `optional_update` | `cryptovpn_p1_ui/02_optional_update.png` |
| `email_register` | `cryptovpn_p1_ui/03_register.png` |
| `reset_password` | `cryptovpn_p1_ui/04_reset_password.png` |

### P1 routes

| Route | Visual asset |
|---|---|
| `plans` | `cryptovpn_p0_ui/06_plans.png` |
| `region_selection` | `cryptovpn_p0_ui/05_region_selection.png` |
| `order_checkout` | `cryptovpn_p1_ui/06_order_checkout.png` |
| `wallet_payment_confirm` | `cryptovpn_p0_ui/11_wallet_payment_confirm.png` |
| `order_result` | `cryptovpn_p1_ui/07_order_result.png` |
| `order_list` | `cryptovpn_p1_ui/08_order_list.png` |
| `order_detail` | `cryptovpn_p1_ui/09_order_detail.png` |

### P2 core routes

| Route | Visual asset |
|---|---|
| `asset_detail` | `cryptovpn_p0_ui/08_asset_detail.png` |
| `receive` | `cryptovpn_p0_ui/10_receive.png` |
| `send` | `cryptovpn_p0_ui/09_send.png` |
| `send_result` | `cryptovpn_p1_ui/10_send_result.png` |
| `invite_center` | `cryptovpn_p1_ui/11_invite_center.png` |
| `commission_ledger` | `cryptovpn_p1_ui/12_commission_ledger.png` |
| `withdraw` | `cryptovpn_p1_ui/13_withdraw.png` |
| `profile` | `cryptovpn_p1_ui/14_profile.png` |
| `legal_documents` | `cryptovpn_p1_ui/15_legal_documents.png` |
| `legal_document_detail` | `cryptovpn_p1_ui/16_legal_detail.png` |

### P2 extended routes with confirmed latest PNG

| Route | Visual asset |
|---|---|
| `import_wallet_method` | `cryptovpn_p2_ui/01_import_wallet.png` |
| `import_mnemonic` | `cryptovpn_p2_ui/02_import_mnemonic.png` |
| `backup_mnemonic` | `cryptovpn_p2_ui/03_backup_phrase.png` |
| `confirm_mnemonic` | `cryptovpn_p2_ui/04_confirm_phrase.png` |
| `security_center` | `cryptovpn_p2_ui/05_security_center.png` |
| `chain_manager` | `cryptovpn_p2_ui/06_chain_manager.png` |
| `add_custom_token` | `cryptovpn_p2_ui/07_add_token.png` |
| `swap` | `cryptovpn_p2_ui/08_swap.png` |
| `bridge` | `cryptovpn_p2_ui/09_bridge.png` |
| `dapp_browser` | `cryptovpn_p2_ui/10_dapp_browser.png` |
| `wallet_connect_session` | `cryptovpn_p2_ui/11_walletconnect.png` |
| `sign_message_confirm` | `cryptovpn_p2_ui/12_sign_request.png` |

## Routed Pages Missing Confirmed Latest PNG

这些页面存在于 `full delivery` 路由矩阵中，但当前没有可确认的最新 PNG 视觉稿。HTML5 首轮不会拍脑袋补视觉，只能基于后续设计确认再做。

- `wallet_payment`
- `subscription_detail`
- `expiry_reminder`
- `node_speed_test`
- `auto_connect_rules`
- `create_wallet`
- `import_private_key`
- `wallet_manager`
- `address_book`
- `gas_settings`
- `risk_authorizations`
- `nft_gallery`
- `staking_earn`

## Non-Routed Visual Reference

- `cryptovpn_p0_ui/04_unified_home.png`
  - 它是强视觉参考页
  - 但不直接对应 `full delivery` 的独立 route
  - 可用于吸收背景、概览卡片、底栏、快捷动作、提醒列表等视觉语言
  - 不作为 HTML5 首轮 routed page 直接交付

## Rebuild Rules

1. 不复用旧 `/UI/pages` 的布局或组件结构。
2. 可参考旧 `p0-pack` 中的动效表达方式，但必须以最新 PNG 结果重新组织页面。
3. HTML5 目录使用全新结构，便于后续整页映射到 Android / Compose。
4. 首轮优先交付：
   - gallery/index
   - design tokens
   - shared shell
   - 38 个有最新 PNG 的 routed pages
5. 缺少 PNG 的 13 个 routed pages 标记为 blocked，不做猜测式视觉实现。

## Shared Visual Modules To Rebuild

- 粒子背景层
- 右上角网格/节点连线装饰
- 玻璃态卡片系统
- 渐变按钮系统
- 页面顶部标题组
- 搜索框 / chip / tab 组合
- 数据概览卡
- 列表项 / 榜单项 / 订单项
- 图表区与 mini chart 组件
- 风险 / AI / 异动模块
- 底部导航栏
- 全局状态角标 / badge / status pill
- 全局 Session 失效弹层

## Next Execution Split

1. 新建干净的 HTML5 目录骨架与共享 token
2. 先完成 gallery / shared shell / base styles
3. 并行实现：
   - P0 + P1 核心购买链路
   - P2 core 资产链路
   - P2 extended 有 PNG 的钱包能力页
4. 静态验收与逐页对照
