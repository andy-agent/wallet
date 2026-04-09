# CryptoVPN UI Prototype

这个目录先承载 **PNG 还原版原型页面**，目标不是直接上线，而是给后续 Android / Compose 落地提供稳定的视觉事实层。

## 目录约定

- `index.html`: P0 聚合总览页，汇总全部原型页面
- `pages/`: 单页原型，每个页面对应一块手机画板
- `styles.css`: 共享视觉令牌与区块样式
- `compose-map.md`: 页面区块到 Compose Composable 的映射
- `compose-integration-notes.md`: Android / Compose 接入建议

## 页面范围

1. `01-splash.html`
2. `02-login.html`
3. `03-plan-guide.html`
4. `04-control-plane.html`
5. `05-market-monitor.html`
6. `06-purchase-confirm.html`
7. `07-wallet-home.html`
8. `08-asset-detail.html`
9. `09-send-asset.html`
10. `10-receive-asset.html`
11. `11-wallet-payment-confirm.html`

## 使用方式

直接用静态服务器打开 `UI/index.html` 即可，例如：

```bash
python3 -m http.server 4173 --directory UI
```

然后访问 `http://127.0.0.1:4173/`。

## Compose 落地方向

- 所有页面都按“可拆成 Composable”的区块来组织，而不是随意拼网页。
- 圆角、层级、按钮、tab、列表项都尽量保持 Compose 易映射。
- 当前 `styles.css` 的颜色和背景会偏向最新 PNG 的浅色视觉，后续进入 `vpnui` 时应作为新版 light token 的候选来源，而不是强行套现有 dark theme。
