# Compose UI 真实化返工执行记录

更新时间：2026-04-11 22:20 +0800

## 本轮执行目标
- 停止用“支付主链路真实”替代“Compose 页面真实落地”。
- 对当前 Android Compose UI 做逐页真实化审计。
- 建立严格的 A/B/C/D 页面分级与后续返工清单。

## 本轮已完成动作
- 将主任务 [liaojiang-0jp](/Users/cnyirui/git/projects/liaojiang/.codex/recovery-context.md) 重新解释为“Compose UI 去 mock/真实化”主线。
- 新建并启动子任务：
  - `liaojiang-0jp.1`：P0 Compose UI 审计
  - `liaojiang-0jp.2`：P1 Compose UI 审计
  - `liaojiang-0jp.3`：P2/P2Extended Compose UI 审计
- 主控完成的实证审计：
  - 核对生产入口 [ComposeContainerActivity.kt](/Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/ui/compose/ComposeContainerActivity.kt)
  - 核对全部 NavGraph 路由到页面/ViewModel/Repository 的连接关系
  - 核对 [RealCryptoVpnRepository.kt](/Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/common/repository/RealCryptoVpnRepository.kt) 与 [RealP0Repository.kt](/Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/p0/repository/RealP0Repository.kt)
  - 核对真实 API 能力边界 [PaymentApi.kt](/Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/payment/data/api/PaymentApi.kt)
  - 产出首版逐页真实化清单 [COMPOSE_UI_REALIFICATION_AUDIT_2026-04-11.md](/Users/cnyirui/git/projects/liaojiang/docs/COMPOSE_UI_REALIFICATION_AUDIT_2026-04-11.md)

## 本轮审计结果
- 审计覆盖：46 个 Compose 页面/路由
- 分类统计：
  - A：0
  - B：11
  - C：17
  - D：18
- 分组结果：
  - P0：A0 / B4 / C3 / D2
  - P1：A0 / B1 / C7 / D0
  - P2 + P2Extended：A0 / B6 / C7 / D16

## 本轮发现的关键伪真实模式
- `Real*Repository` 名称真实，但返回的是本地聚合视图、本地节点快照或本地流程说明。
- P1/P2 NavGraph 仍用硬编码参数推进页面：
  - `annual_pro`
  - `ORD-2025-0001`
  - `TX-9F32`
  - `primary_wallet`
  - `session_default`
  - `request_default`
- 至少一个生产仓库路径仍引用 preview contract：
  - `AssetDetail` 在空列表时回退 `assetDetailPreviewState()`
- 多个页面动作只是导航，不触发真实业务动作：
  - `EmailRegister`
  - `ResetPassword`
  - `Plans`
  - `WalletPaymentConfirm`
  - `Send`
  - `Withdraw`
  - 大部分钱包扩展页

## 本轮没有完成的事
- 还没有开始逐页代码改造。
- 还没有把任何页面从 B/C/D 直接改造成 A。
- 这是因为当前工作树存在你在其他线程进行中的动画/结构改动，和去 mock 主线所需的页面文件高度重叠。

## 当前真实阻塞
- 并非后端支付阻塞。支付主线已经在真实环境闭环。
- 当前阻塞是 **Android 页面文件重叠修改**：
  - 动画线程正在改同一批 Compose 页面/scaffold
  - 去 mock 真实化也要修改同一批页面、状态和交互
  - 在未合流前继续改这些页，会让验收证据失真，且高概率造成覆盖或冲突

## 下一步执行顺序
1. 锁定一批“数据层可先改、视觉层暂不碰”的页面，从 Repository / ViewModel 开始去 mock。
2. 优先返工 P0/P1 中直接影响用户感知的假页面：
   - `EmailRegister`
   - `ResetPassword`
   - `WalletOnboarding`
   - `VpnHome`
   - `WalletHome`
   - `Plans`
   - `OrderCheckout`
   - `WalletPaymentConfirm`
   - `RegionSelection`
3. 第二批返工资产/钱包核心页：
   - `AssetDetail`
   - `Receive`
   - `Send`
   - `SendResult`
   - `Withdraw`
4. 第三批返工法务/关于/扩展钱包页：
   - `LegalDocuments`
   - `LegalDocumentDetail`
   - `AboutApp`
   - `GasSettings`
   - `Swap`
   - `Bridge`
   - `WalletConnectSession`
   - `SignMessageConfirm`

## 当前阶段结论
- 现在只能说：
  - 支付、订阅、VPN 配置签发等若干业务链路是真实的
  - 当前 Compose UI **大部分页面仍未真实化**
- 尤其需要停止误判的页面类型：
  - `RealRepository` 名称真实，但内部返回本地拼装视图
  - 真实数据只占一小部分，其余页面主体仍是模板/合同默认值
  - 导航动作仍使用硬编码 `planId/orderId/txId/walletId`
  - 页面只读 Room/MMKV 缓存，不读真实业务对象
  - preview fallback 仍进入生产仓库逻辑
- 在逐页清掉模板态、本地拼装态、preview/mock contract 态之前，不应再声称“当前新 UI 已完成真实落地”。

## 2026-04-12 运行级返工与回归

### 本轮代码改造
- P2 Core clean worktree `codex/liaojiang-0jp-p2core` 已形成可编译提交：
  - `d9ce058a` `feat(android): realify p2 core compose states and actions`
- integration worktree `codex/liaojiang-0jp-integrate` 已并入：
  - `acfdeeda` `feat(android): realify p2 core compose states and actions`
  - `506ec757` `feat(android): realify p2 extended screens from ui state`
- 本轮又追加了真实回归入口和页面级 crash 修复：
  - debug-only `ComposeRouteOverrideReceiver`，通过广播写入一次性目标路由
  - `LaunchSplashActivity` 改为读取 route override 并透传
  - `ComposeContainerActivity` 改为最终消费 route override，避免 OEM 环境下 activity extra 丢失导致的默认落回登录页
  - `PlansPage` 修掉真实空数据下 `cards.first()` 导致的 `NoSuchElementException`

### 删除/收口的伪真实路径
- P2 Core：
  - `Receive` 不再继续显示派生占位收款地址
  - `Send` / `SendResult` 不再伪装成真实可广播/可成功结果页
  - `InviteShare` 改为真实邀请码 + 真实推广链接语义
  - `Withdraw` 开始绑定真实提交动作，而不是只展示假表单
- P2 Extended：
  - `SecurityCenter`
  - `Swap`
  - `Bridge`
  - `DappBrowser`
  - `WalletConnectSession`
  - `SignMessageConfirm`
  - `ImportMnemonic`
  - `BackupMnemonic`
  - `ImportWalletMethod`
  都已从页面内硬编码“完整功能故事”切回 `uiState` 驱动渲染，不再自己拼一套假业务内容。

### 真机回归入口修复
- 原始 blocker：
  - Oppo 测试机上，`adb shell am start ... ComposeRouteHarnessActivity` 会把 fdroidDebug 包顶回 launcher，导致逐页截图证据不可信。
- 当前修复：
  - 不再依赖 shell 直起目标页面
  - 改为 `broadcast route override -> launcher 启动 fdroidDebug -> ComposeContainerActivity 消费 route` 的链路
  - 实测 `monkey -p com.v2ray.ang.fdroid -c android.intent.category.LAUNCHER 1` 需要最多 1~2 次尝试才能把 app 拉到前台，因此当前回归脚本使用循环重试而不是单次启动

### 真实运行结果
- `plans`
  - 初次真实回归暴露了真实 crash：
    - `java.util.NoSuchElementException: List is empty.`
    - 位置：`PlansPage.kt:68`
    - 原因：真实套餐列表为空时仍直接 `cards.first()`
  - 修复后重新安装并回归，页面可稳定打开
  - 当前证据：
    - 截图：`/tmp/compose-realify-20260412-route/plans.retry.png`
    - 前台 activity：`ComposeContainerActivity`
- `email_register`
  - 通过 route override + launcher loop 打开
  - 当前证据：
    - 截图：`/tmp/compose-realify-20260412-route2/email_register.png`
    - 前台 activity：`ComposeContainerActivity`
- `subscription_detail/current_subscription`
  - 通过 route override + launcher loop 打开
  - 当前证据：
    - 截图：`/tmp/compose-realify-20260412-route2/subscription_detail_current_subscription.png`
    - 前台 activity：`ComposeContainerActivity`
- `chain_manager/primary_wallet`
  - 已通过 launcher-driven route override 打开
  - 当前证据：
    - 截图：`/tmp/compose-realify-20260412-route3/chain_manager_primary_wallet.png`
    - 前台 activity：`ComposeContainerActivity`
- `add_custom_token/base`
  - 已通过 launcher-driven route override 打开
  - 当前证据：
    - 截图：`/tmp/compose-realify-20260412-route3/add_custom_token_base.png`
    - 前台 activity：`ComposeContainerActivity`
- `confirm_mnemonic/primary_wallet`
  - 已通过 launcher-driven route override 打开
  - 当前证据：
    - 截图：`/tmp/compose-realify-20260412-route3/confirm_mnemonic_primary_wallet.png`
    - 前台 activity：`ComposeContainerActivity`

### P2Extended 第二批去硬编码
- 下列页面已从页面文件内写死的“完整业务故事”切回 `uiState` 驱动：
  - `ChainManager`
  - `ConfirmMnemonic`
  - `AddCustomToken`
- 这意味着：
  - 页面标题、摘要、CTA 不再由页面硬编码
  - 页面展示对象改为跟随 repository/viewmodel 状态
  - 即使当前仍是 D 类，也会真实暴露“本地状态 / 未接能力 / 阻塞说明”，而不是继续伪装成功能完整

### 当前仍未完成的事
- 这轮只拿到了“页面能打开”的真实证据，还没有把所有页面的主动作、loading、error、retry 全量跑完。
- P2 Extended 仍有一批自定义页没切完 `uiState` 驱动。
- 运行级回归虽然已建立可用入口，但仍受 Oppo 设备 launcher 行为影响，当前脚本必须重试启动。

### 当前新增 BD 任务
- `liaojiang-0jp.8`
  - `Compose UI：建立稳定真机路由回归入口并沉淀逐页证据`
  - 该任务专门承接 Phase 3 的真实运行证据，不再把回归混在实现批里。
