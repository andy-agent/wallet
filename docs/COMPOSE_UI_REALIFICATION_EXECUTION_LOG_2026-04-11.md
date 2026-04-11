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
