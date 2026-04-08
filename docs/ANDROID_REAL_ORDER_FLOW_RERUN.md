# Android 真机真实下单流程复跑记录（Plans CTA 修复后）

- 任务：`liaojiang-4j0.22.6.2.3`
- 设备：`ba2b016`（真机）
- 包名：`com.v2ray.ang.fdroid`
- 复跑时间：2026-04-08 13:50-13:57（Asia/Shanghai）
- 约束：仅使用当前已安装 App 的现有状态；未清理用户数据；未打印或持久化任何账号凭证。

## 1) 复跑目标

从 Home / Market / Wallet 相关入口推进到 Plans，尝试创建订单并进入 `OrderCheckout`（支付页）或支付结果页；若失败，记录最远可达点与精确阻塞点。

## 2) 实际执行路径（真机）

1. 冷启动 App，等待启动页结束，进入 Home（现有本地会话可直接使用）。
2. 在 Home 点击“去订阅”，进入“套餐中心（Plans）”。
3. 在 Plans 选择“基础版-1个月”，点击底部 CTA 区域（预期“继续下单”）。
4. 未跳转后，再走备用路径：返回 Home，点击“套餐”入口再次进入 Plans，并再次点击底部 CTA 区域。
5. 仍未跳转，返回 Home，点击“订单”验证是否有新建订单。
6. 额外检查 Market：可正常进入 Market 页面，但无直接推进到下单收银台的有效入口。

## 3) 结果判定

- 是否创建真实订单：**否**
- 是否进入 OrderCheckout / 收银台（Payment）：**否**
- 是否到达支付结果页：**否**

## 4) 最远到达点与阻塞点

- 最远到达点：**Plans（套餐中心）**
- 精确阻塞点：Plans 页底部 CTA（“继续下单”）在 `uiautomator dump` 中仍表现为 `bounds=[0,0][0,0]`，点击底部条带区域后路由无推进，页面持续停留在“套餐中心”。
- 旁证：订单列表仍为 `待支付 0 / 已完成 0 / 总记录 0`，且显示“暂无 VPN 订单”。

## 5) 关键截图（/tmp/liaojiang-screens）

- 启动后首屏：`/tmp/liaojiang-screens/20260408-135029-01-launch-postwait.png`
- Home（含“去订阅”入口）：`/tmp/liaojiang-screens/20260408-135029-02-home.png`
- 进入 Plans：`/tmp/liaojiang-screens/20260408-135029-03-plans-entry.png`
- 选中套餐：`/tmp/liaojiang-screens/20260408-135029-04-plan-selected.png`
- 首次点击 CTA 后：`/tmp/liaojiang-screens/20260408-135029-05-cta-tap-1.png`
- Market 页面检查：`/tmp/liaojiang-screens/20260408-135029-06-market-tab.png`
- Home“套餐”入口二次进入 Plans：`/tmp/liaojiang-screens/20260408-135029-07-home-plan-icon.png`
- 二次点击 CTA 后：`/tmp/liaojiang-screens/20260408-135029-08-cta-tap-2.png`
- 订单列表核验（空）：`/tmp/liaojiang-screens/20260408-135029-09-order-list.png`

## 6) 结论

本次在真机 `ba2b016` 使用当前安装态复跑后，链路依旧卡在 Plans CTA，未能推进到订单收银台或支付结果页。当前可复现阻塞与上次一致，farthest point 为 Plans。
