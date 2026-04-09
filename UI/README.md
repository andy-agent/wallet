# CryptoVPN UI Prototype

这个目录现在承载两套原型，但**已采纳 ZIP 导入的 `p0-pack/` 作为主事实源**。目标不是直接上线，而是给后续 Android / Compose 落地提供稳定的视觉事实层。

## 目录约定

- `index.html`: `/UI` 总入口，默认指向已采纳的 P0 包
- `p0-pack/`: 已采纳的 ZIP 版 P0 UI，当前主事实源
- `legacy-index.html`: 旧版仓库看板入口
- `pages/`: 单页原型，每个页面对应一块手机画板
- `styles.css`: 共享视觉令牌与区块样式
- `compose-map.md`: 页面区块到 Compose Composable 的映射
- `compose-integration-notes.md`: Android / Compose 接入建议
- `app-ui-gap-audit.md`: Android APP 功能盘点与 UI 缺口审计
- `p0-adoption-decision.md`: ZIP 与旧版 `/UI` 的采用结论

## 页面范围

当前主页面集见 `p0-pack/`：

1. `01_splash.html`
2. `02_login.html`
3. `03_wallet_onboarding.html`
4. `04_unified_home.html`
5. `05_region_selection.html`
6. `06_plans.html`
7. `07_wallet_home.html`
8. `08_asset_detail.html`
9. `09_send.html`
10. `10_receive.html`
11. `11_wallet_payment_confirm.html`

旧版 `pages/` 保留为 legacy 参考。

## 使用方式

直接用静态服务器打开 `UI/index.html` 即可，例如：

```bash
python3 -m http.server 4173 --directory UI
```

然后访问 `http://127.0.0.1:4173/`。

## Compose 落地方向

- 当前应优先以 `p0-pack/` 的视觉和页面范围做 Compose 嵌套。
- `pages/` 与 `styles.css` 对应的是旧版仓库内探索稿，保留参考价值，但不再是首选来源。
- 后续进入 `vpnui` 时，应把浅色视觉体系作为新版 light token 的候选来源，而不是反向迁就旧 dark theme。
