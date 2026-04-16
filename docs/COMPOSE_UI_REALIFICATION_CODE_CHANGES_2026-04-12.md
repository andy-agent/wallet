# Compose UI 代码改造清单

更新时间：2026-04-12

数据来源：
- integration 分支 `codex/liaojiang-0jp-integrate`
- [COMPOSE_UI_REALIFICATION_EXECUTION_LOG_2026-04-11.md](/Users/cnyirui/git/projects/liaojiang/docs/COMPOSE_UI_REALIFICATION_EXECUTION_LOG_2026-04-11.md)
- launcher-driven 真机截图与动作级证据

说明：
- 本清单记录的是已经接受进 integration 主线的真实化改造，不包含当前主仓未验收且编译失败的坏补丁。
- “已接入真实源/动作”并不等于页面已达 A，只表示对应页面已从纯模板/样本态向真实对象或真实阻塞态推进。

## P0

| 页面 | 代码改造 | 删掉的 mock / preview / local sample | 已接入真实源 / 动作 | 剩余阻塞 |
|---|---|---|---|---|
| Splash | 调整 `SplashViewModel` 与 `RealP0Repository`，把启动页渲染改为真实缓存/会话摘要驱动。 | 去掉固定示例启动说明和误导性进度文案。 | 读取本地登录态、订单缓存、token 有效性。 | 仍缺真实 bootstrap 编排与分流。 |
| EmailLogin | `LoginViewModel` 与登录页改为 Compose 内调用真实认证仓储。 | 去掉只展示模板种子的假登录路径。 | 真实登录 API、真实错误消息回填。 | 缺完整 loading/retry/unavailable 状态。 |
| EmailRegister | 收紧合同与页面语义，避免页面继续伪装“注册已完整接通”。 | 去掉生产路径里的纯样本 copy。 | 已开始从真实仓储提供字段和状态说明。 | 真实注册提交仍未完全迁入当前页。 |
| ResetPassword | 页面和合同从“假成功/假跳转”改为明确当前能力缺口。 | 去掉误导性 reset 完成语义。 | 已接入真实状态说明。 | Android 侧真实 reset API 仍缺。 |
| WalletOnboarding | 页面文案改为真实阻塞态，不再暗示已可创建/导入。 | 去掉“生成助记词并云备份”等伪完成文案。 | 真实空态/阻塞说明。 | 钱包 onboarding 真实对象仍缺。 |
| VpnHome | `RealP0Repository`、`VpnHomePage` 去掉 `nullms` 和过度承诺文案。 | 去掉误导性测速与“已就绪”表达。 | 订阅、订单与连接状态摘要来自真实缓存/真实业务上下文。 | 节点列表与首页总览仍混本地聚合。 |
| WalletHome | `WalletHomePage` 调整为真实缓存/支付网络摘要，不再冒充链资产系统。 | 去掉“已激活链/自动补齐”等伪能力文案。 | 真实订单映射资产摘要、真实缓存状态说明。 | 仍未接真实钱包资产域。 |
| ForceUpdate | 更新页改为信息态/阻塞态，不再伪装更新动作已接通。 | 去掉假更新按钮语义。 | 真实版本说明态。 | 真实版本策略与分发动作仍缺。 |
| OptionalUpdate | 更新页改为信息态/阻塞态，不再伪装更新动作已接通。 | 去掉假更新按钮语义。 | 真实版本说明态。 | 真实版本策略与分发动作仍缺。 |

## P1

| 页面 | 代码改造 | 删掉的 mock / preview / local sample | 已接入真实源 / 动作 | 剩余阻塞 |
|---|---|---|---|---|
| Plans | `PlansPage` 修掉空列表崩溃，页面开始消费真实套餐对象。 | 去掉示例套餐卡主路径。 | 真实 `plans` 列表、真实下单入口。 | 状态机仍不完整，成功/失败反馈偏弱。 |
| RegionSelection | 页面层去掉本地 fallback 节点和伪 `89 Mbps` 速度。 | 去掉硬编码节点与测速样本。 | 真实 `uiState.regions` 和 `stateInfo`。 | 区域/节点仍未绑定完整真实区域业务对象。 |
| OrderCheckout | 页面开始围绕真实 `plan`/`order`/`paymentTarget` 组织结算信息。 | 去掉默认 `annual_pro` 主路径。 | 真实套餐与订单上下文。 | 支付块和动作仍偏模板。 |
| WalletPaymentConfirm | 页面改为显示真实订单号、金额、风险说明。 | 去掉 `CVP-2409` 等固定样本值。 | 真实 `order` / `metrics` / `riskLines`。 | 真实支付确认动作仍不足。 |
| OrderResult | 页面不再一律显示“订单已生效”，改为按真实状态渲染。 | 去掉固定成功页模板。 | 真实 `order.status`、真实详情行。 | 仍缺更完整的状态机与动作反馈。 |
| OrderList | 改为读取真实订单列表对象和真实跳转目标。 | 去掉 `ORD-2025-0001` 样本列表。 | 真实订单集合、真实点击跳详情。 | 仍缺空态/错态/重试。 |
| OrderDetail | 页面改为真实订单字段和真实详情块驱动。 | 去掉样本订单号、样本交易摘要。 | 真实 `orderNo`、真实金额、真实状态。 | 仍缺更多订单动作闭环。 |
| WalletPayment | 补齐 `wallet_payment` 路由；页面透出 `stateInfo` 并在无真实订单时隐藏 CTA。 | 去掉在空会话下仍能继续的假按钮。 | 真实支付会话摘要。 | 页面是否保留以及如何与真实钱包对象衔接仍待定。 |

## P2 Core

| 页面 | 代码改造 | 删掉的 mock / preview / local sample | 已接入真实源 / 动作 | 剩余阻塞 |
|---|---|---|---|---|
| AssetDetail | 引入 truthful blocker/只读信息结构，避免把假资产页当真钱包详情。 | 去掉预览式余额/明细包装。 | 真实阻塞说明。 | 真实钱包资产与明细 API 仍缺。 |
| Receive | 改为真实阻塞语义，不再假装有可收款地址。 | 去掉假地址/假二维码主语义。 | 真实阻塞说明。 | 真实钱包地址源、copy/share 能力仍缺。 |
| Send | 改为真实阻塞语义，不再假装可直接发链上交易。 | 去掉模板发送成功语义。 | 真实阻塞说明。 | 真实转账广播链路仍缺。 |
| SendResult | 改为真实阻塞/结果说明态，不再用固定交易号冒充真实结果。 | 去掉 `TX-9F32` 成功模板主语义。 | 真实阻塞说明。 | 真实交易结果对象仍缺。 |
| InviteCenter | 页面开始消费真实邀请概览对象。 | 去掉纯本地奖励统计拼装。 | 真实 `referral/overview`。 | 真实绑定/分享动作和状态机仍弱。 |
| InviteShare | 收紧页面语义，不再把少量真实邀请码包装成完整分享系统。 | 去掉固定邀请码/固定分享稿样本主语义。 | 真实邀请码上下文。 | 真实 share link / share action 仍缺。 |
| CommissionLedger | 页面绑定真实 summary + ledger 数据。 | 去掉本地佣金账本样本。 | 真实佣金汇总与账本。 | 分页、筛选、重试仍需补。 |
| Withdraw | 页面绑定真实 summary + withdrawal 历史。 | 去掉假提现记录。 | 真实提现汇总和历史。 | 真实提交提现动作仍需补齐。 |
| Profile | `ProfilePage` 由真实 badge/导航语义驱动，不再靠纯模板菜单。 | 去掉部分假菜单语义。 | 真实 `me` 上下文、真实导航跳转。 | 仍混本地缓存视图，缺退出/刷新等动作。 |
| LegalDocuments | 文档列表改为按真实文档 `id` 导航，而不是按样本索引。 | 去掉索引式详情跳转。 | 真实文档列表语义与详情跳转。 | 文档内容源仍是本地资源/配置。 |
| LegalDocumentDetail | 修正文档标识/时间等字段语义，并验证外链动作。 | 去掉把文档 id 当日期的错语义。 | 真实详情页外链动作。 | 文档正文仍不是后端真实对象。 |
| AboutApp | 页面按真实 `badge` 执行外链/法务跳转，并已验证浏览器动作。 | 去掉纯静态说明页语义。 | 真实外链动作。 | 动态版本/发布对象仍缺。 |

## P2 Extended

| 页面 | 代码改造 | 删掉的 mock / preview / local sample | 已接入真实源 / 动作 | 剩余阻塞 |
|---|---|---|---|---|
| SubscriptionDetail | 页面改成真实订阅上下文驱动的只读态/阻塞态。 | 去掉纯模板订阅权益页主语义。 | 真实订阅上下文。 | 生产路由挂载和续费动作仍不足。 |
| ExpiryReminder | 页面改成真实订阅到期上下文驱动。 | 去掉假倒计时主语义。 | 真实订阅到期上下文。 | 续费动作与完整状态机仍不足。 |
| NodeSpeedTest | 页面改成真实阻塞/说明态，不再伪装测速已实现。 | 去掉固定测速结果说明模板。 | 真实阻塞说明。 | 真实测速服务仍缺。 |
| AutoConnectRules | 页面改成 `uiState` 驱动和真实阻塞说明。 | 去掉假规则列表主语义。 | 真实状态说明。 | 规则持久化/同步仍缺。 |
| CreateWallet | 页面改成真实阻塞态，不再误导“立即创建成功”。 | 去掉伪创建完成语义。 | 真实阻塞说明。 | 真实钱包创建引擎仍缺。 |
| ImportWalletMethod | 页面改成 `uiState` 驱动，不再用页面内写死方法卡。 | 去掉写死导入方法样本。 | 真实状态说明。 | 真实导入流仍缺。 |
| ImportMnemonic | 页面改成真实阻塞态和真实字段说明。 | 去掉假导入成功主语义。 | 真实阻塞说明。 | 助记词解析/派生/导入仍缺。 |
| ImportPrivateKey | 页面改成真实阻塞态。 | 去掉假导入完成语义。 | 真实阻塞说明。 | 私钥验证/保存仍缺。 |
| BackupMnemonic | 页面从写死文案改为 `uiState` 驱动。 | 去掉固定 12 词和伪完成语义。 | 真实阻塞说明。 | 真实待备份助记词仍缺。 |
| ConfirmMnemonic | 页面从写死挑战改为 `uiState` 驱动。 | 去掉固定顺序题样本。 | 真实阻塞说明。 | 真实 challenge/verify 仍缺。 |
| SecurityCenter | 页面改成真实 `uiState` 驱动，不再完全靠硬编码卡片。 | 去掉一批模板安全卡片。 | 真实会话/账户上下文说明。 | 真实设备/授权/风险对象仍缺。 |
| ChainManager | 页面改成 `uiState` 驱动。 | 去掉页面内写死链管理卡片。 | 真实阻塞说明。 | 真实多链配置读写仍缺。 |
| AddCustomToken | 页面改成 `uiState` 驱动。 | 去掉页面内写死自定义代币表单文案。 | 真实阻塞说明。 | 真实 metadata 查询/持久化仍缺。 |
| WalletManager | 页面改成 `uiState` 驱动，并补齐生产路由。 | 去掉假单钱包管理主语义。 | 真实阻塞说明。 | 多钱包模型仍缺。 |
| AddressBook | 页面改成 `uiState` 驱动，并补齐生产路由。 | 去掉假地址簿样本。 | 真实阻塞说明。 | 地址簿持久化仍缺。 |
| GasSettings | 页面改成 `uiState` 驱动。 | 去掉页面内写死 gas 档位主语义。 | 真实阻塞说明。 | 真实 gas estimator 仍缺。 |
| Swap | 页面改成 `uiState` 驱动和真实阻塞说明。 | 去掉假 quote/假兑换完成语义。 | 真实阻塞说明。 | 真实 swap quote/submit 仍缺。 |
| Bridge | 页面改成 `uiState` 驱动和真实阻塞说明。 | 去掉假跨链完成语义。 | 真实阻塞说明。 | 真实 bridge quote/submit 仍缺。 |
| DappBrowser | 页面改成 `uiState` 驱动和真实阻塞说明。 | 去掉假浏览器容器主语义。 | 真实阻塞说明。 | 真实浏览器容器/历史/收藏仍缺。 |
| WalletConnectSession | 页面改成 `uiState` 驱动和真实阻塞说明。 | 去掉假 session 操作语义。 | 真实阻塞说明。 | 真实 WalletConnect 会话仓储仍缺。 |
| SignMessageConfirm | 页面改成 `uiState` 驱动和真实阻塞说明。 | 去掉假签名确认完成语义。 | 真实阻塞说明。 | 真实签名请求源仍缺。 |
| RiskAuthorizations | 页面改成 `uiState` 驱动和真实阻塞说明。 | 去掉假授权清单样本。 | 真实阻塞说明。 | 真实授权记录与撤销动作仍缺。 |
| NftGallery | 页面改成 `uiState` 驱动和真实阻塞说明。 | 去掉假 NFT 画廊样本。 | 真实阻塞说明。 | 真实 NFT 索引仍缺。 |
| StakingEarn | 页面改成 `uiState` 驱动和真实阻塞说明。 | 去掉假质押收益样本。 | 真实阻塞说明。 | 真实 staking 仓位和收益仍缺。 |
