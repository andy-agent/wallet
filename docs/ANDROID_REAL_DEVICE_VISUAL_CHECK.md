# Android 真机视觉/路由回归（ba2b016）

- 检查日期：2026-04-08
- 设备：`ba2b016`（OPPO PDVM00）
- 构建上下文：已安装最新 master 重建包；冷启动入口应为 `ComposeLauncherAlias`
- 证据目录：`/tmp/liaojiang-screens`
- 约束：本次未执行登录流、未执行购买流

## 冷启动与壳层检查

- 通过：
  - `liaojiang-cold-launch-post-fix.png`：冷启动已进入新壳，不再落到旧“配置项”页。
  - `liaojiang-after-splash.png`：Splash 后进入应用主内容。
  - `liaojiang-post-fix-current.png`：主界面可见底部导航与业务内容。

## 一级 Tab 可达性（Home / Wallet / VPN / Discover / Profile）

- Home：通过  
  - 证据：`20260408-ba2b016-tab-home.png`
- Wallet：通过  
  - 证据：`20260408-ba2b016-tab-wallet.png`
- VPN：通过  
  - 证据：`20260408-ba2b016-tab-vpn.png`、`20260408-ba2b016-tab-vpn-recheck.png`
- Discover：可达但判定失败（内容异常）  
  - 证据：`20260408-ba2b016-tab-discover.png`、`20260408-ba2b016-tab-discover-recheck.png`
  - 现象：落点呈现“带返回箭头的奖励/邀请上下文”，不是稳定的一层 Discover 壳；疑似路由绑定到二级页。
- Profile：可达但判定失败（内容异常）  
  - 证据：`20260408-ba2b016-tab-profile.png`、`20260408-ba2b016-profile-secondary-settings.png`
  - 现象：Tab 落点直接是“设置”页（含“钱包安全/偏好设置”），未见独立 Profile 首页壳。

## 二级路由抽样（每个家族至少 1 个）

- Home 家族：通过  
  - 路由：Home -> 套餐中心  
  - 证据：`20260408-ba2b016-home-secondary-subscribe.png`
- Wallet 家族：通过  
  - 路由：Wallet -> 收款（USDT 收款）  
  - 证据：`20260408-ba2b016-wallet-secondary-receive.png`
- VPN 家族：通过  
  - 路由：VPN -> 详情子页签（概览/详情切换）  
  - 证据：`20260408-ba2b016-vpn-secondary-detail-tab.png`
- Discover 家族：通过（但一级壳异常）  
  - 路由：Discover -> 奖励记录  
  - 证据：`20260408-ba2b016-discover-secondary-reward-records.png`
- Profile 家族：通过（但一级壳异常）  
  - 路由：Profile -> 设置页  
  - 证据：`20260408-ba2b016-profile-secondary-settings.png`

## 已知失败项（明确记录）

1. Discover Tab 内容异常（失败）  
   - 证据：`20260408-ba2b016-tab-discover.png`、`liaojiang-wallet-shell-regression-20260408.png`  
   - 描述：Tab 可点通，但呈现为奖励/邀请语义上下文，不像稳定 Discover 一级壳。
2. Profile Tab 内容异常（失败）  
   - 证据：`20260408-ba2b016-tab-profile.png`  
   - 描述：Tab 落点直接进入设置语义页面，疑似错误内容或壳层映射不符。

## 未覆盖/未验证

- 登录后态下的 Tab 与二级路由一致性：未验证（本任务不做登录）。
- 购买链路（下单、支付、结果页）视觉与路由：未验证（本任务不做购买）。
- Discover/Profile 在产品预期定义下的“正确一层壳”对照基准：仅凭当前截图无法完全判定产品期望，已按“壳层异常”先记失败。
