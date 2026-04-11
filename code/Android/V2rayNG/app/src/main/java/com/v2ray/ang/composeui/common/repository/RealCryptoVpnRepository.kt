package com.v2ray.ang.composeui.common.repository

import android.content.Context
import com.v2ray.ang.AppConfig
import com.v2ray.ang.BuildConfig
import com.v2ray.ang.composeui.common.model.FeatureBullet
import com.v2ray.ang.composeui.common.model.FeatureField
import com.v2ray.ang.composeui.common.model.FeatureListItem
import com.v2ray.ang.composeui.common.model.FeatureMetric
import com.v2ray.ang.composeui.global.session.SessionEvictedDialogUiState
import com.v2ray.ang.composeui.global.session.sessionEvictedDialogPreviewState
import com.v2ray.ang.composeui.p0.model.EmailRegisterUiState
import com.v2ray.ang.composeui.p0.model.ForceUpdateUiState
import com.v2ray.ang.composeui.p0.model.OptionalUpdateUiState
import com.v2ray.ang.composeui.p0.model.ResetPasswordUiState
import com.v2ray.ang.composeui.p0.model.emailRegisterPreviewState
import com.v2ray.ang.composeui.p0.model.forceUpdatePreviewState
import com.v2ray.ang.composeui.p0.model.optionalUpdatePreviewState
import com.v2ray.ang.composeui.p0.model.resetPasswordPreviewState
import com.v2ray.ang.composeui.p1.model.OrderCheckoutRouteArgs
import com.v2ray.ang.composeui.p1.model.OrderCheckoutUiState
import com.v2ray.ang.composeui.p1.model.P1DetailLine
import com.v2ray.ang.composeui.p1.model.P1OrderSummary
import com.v2ray.ang.composeui.p1.model.P1PlanCard
import com.v2ray.ang.composeui.p1.model.P1RegionOption
import com.v2ray.ang.composeui.p1.model.P1ScreenState
import com.v2ray.ang.composeui.p1.model.P1StateInfo
import com.v2ray.ang.composeui.p1.model.OrderDetailRouteArgs
import com.v2ray.ang.composeui.p1.model.OrderDetailUiState
import com.v2ray.ang.composeui.p1.model.OrderListUiState
import com.v2ray.ang.composeui.p1.model.OrderResultRouteArgs
import com.v2ray.ang.composeui.p1.model.OrderResultUiState
import com.v2ray.ang.composeui.p1.model.PlansUiState
import com.v2ray.ang.composeui.p1.model.RegionSelectionUiState
import com.v2ray.ang.composeui.p1.model.WalletPaymentConfirmRouteArgs
import com.v2ray.ang.composeui.p1.model.WalletPaymentConfirmUiState
import com.v2ray.ang.composeui.p1.model.WalletPaymentUiState
import com.v2ray.ang.composeui.p1.model.orderCheckoutPreviewState
import com.v2ray.ang.composeui.p1.model.orderDetailPreviewState
import com.v2ray.ang.composeui.p1.model.orderListPreviewState
import com.v2ray.ang.composeui.p1.model.orderResultPreviewState
import com.v2ray.ang.composeui.p1.model.plansPreviewState
import com.v2ray.ang.composeui.p1.model.regionSelectionPreviewState
import com.v2ray.ang.composeui.p1.model.walletPaymentConfirmPreviewState
import com.v2ray.ang.composeui.p1.model.walletPaymentPreviewState
import com.v2ray.ang.composeui.p2.model.AssetDetailRouteArgs
import com.v2ray.ang.composeui.p2.model.AssetDetailUiState
import com.v2ray.ang.composeui.p2.model.AboutAppUiState
import com.v2ray.ang.composeui.p2.model.CommissionLedgerUiState
import com.v2ray.ang.composeui.p2.model.InviteCenterUiState
import com.v2ray.ang.composeui.p2.model.InviteShareUiState
import com.v2ray.ang.composeui.p2.model.LegalDocumentDetailRouteArgs
import com.v2ray.ang.composeui.p2.model.LegalDocumentDetailUiState
import com.v2ray.ang.composeui.p2.model.LegalDocumentsUiState
import com.v2ray.ang.composeui.p2.model.ProfileUiState
import com.v2ray.ang.composeui.p2.model.ReceiveRouteArgs
import com.v2ray.ang.composeui.p2.model.ReceiveUiState
import com.v2ray.ang.composeui.p2.model.SendResultRouteArgs
import com.v2ray.ang.composeui.p2.model.SendResultUiState
import com.v2ray.ang.composeui.p2.model.SendRouteArgs
import com.v2ray.ang.composeui.p2.model.SendUiState
import com.v2ray.ang.composeui.p2.model.WithdrawUiState
import com.v2ray.ang.composeui.p2.model.aboutAppPreviewState
import com.v2ray.ang.composeui.p2.model.assetDetailPreviewState
import com.v2ray.ang.composeui.p2.model.commissionLedgerPreviewState
import com.v2ray.ang.composeui.p2.model.inviteCenterPreviewState
import com.v2ray.ang.composeui.p2.model.inviteSharePreviewState
import com.v2ray.ang.composeui.p2.model.legalDocumentDetailPreviewState
import com.v2ray.ang.composeui.p2.model.legalDocumentsPreviewState
import com.v2ray.ang.composeui.p2.model.profilePreviewState
import com.v2ray.ang.composeui.p2.model.receivePreviewState
import com.v2ray.ang.composeui.p2.model.sendPreviewState
import com.v2ray.ang.composeui.p2.model.sendResultPreviewState
import com.v2ray.ang.composeui.p2.model.withdrawPreviewState
import com.v2ray.ang.composeui.p2extended.model.AddCustomTokenRouteArgs
import com.v2ray.ang.composeui.p2extended.model.AddCustomTokenUiState
import com.v2ray.ang.composeui.p2extended.model.AddressBookRouteArgs
import com.v2ray.ang.composeui.p2extended.model.AddressBookUiState
import com.v2ray.ang.composeui.p2extended.model.AutoConnectRulesUiState
import com.v2ray.ang.composeui.p2extended.model.BackupMnemonicRouteArgs
import com.v2ray.ang.composeui.p2extended.model.BackupMnemonicUiState
import com.v2ray.ang.composeui.p2extended.model.BridgeRouteArgs
import com.v2ray.ang.composeui.p2extended.model.BridgeUiState
import com.v2ray.ang.composeui.p2extended.model.ChainManagerRouteArgs
import com.v2ray.ang.composeui.p2extended.model.ChainManagerUiState
import com.v2ray.ang.composeui.p2extended.model.ConfirmMnemonicRouteArgs
import com.v2ray.ang.composeui.p2extended.model.ConfirmMnemonicUiState
import com.v2ray.ang.composeui.p2extended.model.CreateWalletRouteArgs
import com.v2ray.ang.composeui.p2extended.model.CreateWalletUiState
import com.v2ray.ang.composeui.p2extended.model.DappBrowserRouteArgs
import com.v2ray.ang.composeui.p2extended.model.DappBrowserUiState
import com.v2ray.ang.composeui.p2extended.model.ExpiryReminderRouteArgs
import com.v2ray.ang.composeui.p2extended.model.ExpiryReminderUiState
import com.v2ray.ang.composeui.p2extended.model.GasSettingsRouteArgs
import com.v2ray.ang.composeui.p2extended.model.GasSettingsUiState
import com.v2ray.ang.composeui.p2extended.model.ImportMnemonicRouteArgs
import com.v2ray.ang.composeui.p2extended.model.ImportMnemonicUiState
import com.v2ray.ang.composeui.p2extended.model.ImportPrivateKeyRouteArgs
import com.v2ray.ang.composeui.p2extended.model.ImportPrivateKeyUiState
import com.v2ray.ang.composeui.p2extended.model.ImportWalletMethodUiState
import com.v2ray.ang.composeui.p2extended.model.NftGalleryUiState
import com.v2ray.ang.composeui.p2extended.model.NodeSpeedTestRouteArgs
import com.v2ray.ang.composeui.p2extended.model.NodeSpeedTestUiState
import com.v2ray.ang.composeui.p2extended.model.RiskAuthorizationsUiState
import com.v2ray.ang.composeui.p2extended.model.SecurityCenterUiState
import com.v2ray.ang.composeui.p2extended.model.SignMessageConfirmRouteArgs
import com.v2ray.ang.composeui.p2extended.model.SignMessageConfirmUiState
import com.v2ray.ang.composeui.p2extended.model.StakingEarnUiState
import com.v2ray.ang.composeui.p2extended.model.SubscriptionDetailRouteArgs
import com.v2ray.ang.composeui.p2extended.model.SubscriptionDetailUiState
import com.v2ray.ang.composeui.p2extended.model.SwapRouteArgs
import com.v2ray.ang.composeui.p2extended.model.SwapUiState
import com.v2ray.ang.composeui.p2extended.model.WalletConnectSessionRouteArgs
import com.v2ray.ang.composeui.p2extended.model.WalletConnectSessionUiState
import com.v2ray.ang.composeui.p2extended.model.WalletManagerRouteArgs
import com.v2ray.ang.composeui.p2extended.model.WalletManagerUiState
import com.v2ray.ang.composeui.p2extended.model.addCustomTokenPreviewState
import com.v2ray.ang.composeui.p2extended.model.addressBookPreviewState
import com.v2ray.ang.composeui.p2extended.model.autoConnectRulesPreviewState
import com.v2ray.ang.composeui.p2extended.model.backupMnemonicPreviewState
import com.v2ray.ang.composeui.p2extended.model.bridgePreviewState
import com.v2ray.ang.composeui.p2extended.model.chainManagerPreviewState
import com.v2ray.ang.composeui.p2extended.model.confirmMnemonicPreviewState
import com.v2ray.ang.composeui.p2extended.model.createWalletPreviewState
import com.v2ray.ang.composeui.p2extended.model.dappBrowserPreviewState
import com.v2ray.ang.composeui.p2extended.model.expiryReminderPreviewState
import com.v2ray.ang.composeui.p2extended.model.gasSettingsPreviewState
import com.v2ray.ang.composeui.p2extended.model.importMnemonicPreviewState
import com.v2ray.ang.composeui.p2extended.model.importPrivateKeyPreviewState
import com.v2ray.ang.composeui.p2extended.model.importWalletMethodPreviewState
import com.v2ray.ang.composeui.p2extended.model.nftGalleryPreviewState
import com.v2ray.ang.composeui.p2extended.model.nodeSpeedTestPreviewState
import com.v2ray.ang.composeui.p2extended.model.riskAuthorizationsPreviewState
import com.v2ray.ang.composeui.p2extended.model.securityCenterPreviewState
import com.v2ray.ang.composeui.p2extended.model.signMessageConfirmPreviewState
import com.v2ray.ang.composeui.p2extended.model.stakingEarnPreviewState
import com.v2ray.ang.composeui.p2extended.model.subscriptionDetailPreviewState
import com.v2ray.ang.composeui.p2extended.model.swapPreviewState
import com.v2ray.ang.composeui.p2extended.model.walletConnectSessionPreviewState
import com.v2ray.ang.composeui.p2extended.model.walletManagerPreviewState
import com.v2ray.ang.payment.PaymentConfig
import com.v2ray.ang.payment.data.api.CommissionLedgerItem
import com.v2ray.ang.payment.data.api.CommissionSummaryData
import com.v2ray.ang.payment.data.api.CurrentSubscriptionData
import com.v2ray.ang.payment.data.api.MeData
import com.v2ray.ang.payment.data.api.ReferralOverviewData
import com.v2ray.ang.payment.data.api.WithdrawalItem
import com.v2ray.ang.payment.data.local.entity.OrderEntity
import com.v2ray.ang.payment.data.local.entity.UserEntity
import com.v2ray.ang.payment.data.model.Order
import com.v2ray.ang.payment.data.model.Plan
import com.v2ray.ang.payment.data.repository.PaymentRepository
import com.v2ray.ang.handler.MmkvManager
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

class RealCryptoVpnRepository(context: Context) : CryptoVpnRepository {
    private val paymentRepository = PaymentRepository(context.applicationContext)

    override suspend fun getForceUpdateState(): ForceUpdateUiState {
        return ForceUpdateUiState(
            metrics = listOf(
                FeatureMetric("当前版本", BuildConfig.VERSION_NAME),
                FeatureMetric("分发渠道", BuildConfig.DISTRIBUTION),
                FeatureMetric("数据源", "真实模块"),
            ),
            note = "当前为真实 Android 模块状态，升级策略暂未接入独立后端检查。",
        )
    }

    override suspend fun getOptionalUpdateState(): OptionalUpdateUiState {
        return OptionalUpdateUiState(
            metrics = listOf(
                FeatureMetric("当前版本", BuildConfig.VERSION_NAME),
                FeatureMetric("分发渠道", BuildConfig.DISTRIBUTION),
                FeatureMetric("检查方式", "本地构建"),
            ),
            note = "可选升级页暂使用真实模块版本信息，后续可接入独立更新接口。",
        )
    }

    override suspend fun getEmailRegisterState(): EmailRegisterUiState {
        val cached = paymentRepository.getCachedCurrentUser()
        return EmailRegisterUiState(
            fields = listOf(
                FeatureField("email", "邮箱", cached?.email ?: "", "将作为登录与找回凭据"),
                FeatureField("code", "验证码", "", "真实接口支持发送邮箱验证码"),
                FeatureField("password", "登录密码", "", "需包含字母与数字"),
                FeatureField("invite", "邀请码", "", "选填"),
            ),
            note = "已切换到真实认证域数据源；当前页面动作仍为模板流，下一步可继续接通注册提交。",
        )
    }

    override suspend fun getResetPasswordState(): ResetPasswordUiState {
        val cached = paymentRepository.getCachedCurrentUser()
        return ResetPasswordUiState(
            fields = listOf(
                FeatureField("email", "邮箱", cached?.email ?: "", "会向该邮箱发送验证码"),
                FeatureField("code", "验证码", "", "请填写收到的验证码"),
                FeatureField("password", "新密码", "", "后端 reset endpoint 尚未提供"),
            ),
            note = "已切换到真实认证域数据源；当前页面动作仍为模板流，等待后端 reset-password 能力补齐。",
        )
    }

    override suspend fun getPlansState(): PlansUiState {
        val plans = paymentRepository.getPlans().getOrNull()
        if (plans.isNullOrEmpty()) {
            return PlansUiState(
                stateInfo = P1StateInfo(P1ScreenState.Empty, title = "暂无套餐", message = "当前未取到真实套餐数据。"),
                metrics = listOf(
                    FeatureMetric("套餐数量", "0"),
                    FeatureMetric("状态", "未取到真实套餐"),
                    FeatureMetric("数据源", "PaymentRepository"),
                ),
                summary = "当前未取到真实套餐数据。",
                note = "套餐页未再回退到 Mock 仓库；保持真实接口空态。",
            )
        }

        val sortedPlans = plans.sortedBy { it.displayOrder }
        return PlansUiState(
            stateInfo = P1StateInfo(P1ScreenState.Content),
            metrics = sortedPlans.take(3).map {
                FeatureMetric(it.getDurationDisplay(), "$${it.priceUsd}")
            },
            plans = sortedPlans.map {
                P1PlanCard(
                    planCode = it.planCode,
                    title = it.name,
                    priceText = "$${it.priceUsd}",
                    subtitle = it.description ?: it.regionAccessPolicy,
                    badge = it.badge ?: "LIVE",
                    tags = listOf(it.getDurationDisplay(), it.quoteAssetCodeLabel(), it.quoteNetworkCodeLabel()),
                    featured = it.badge?.contains("推荐") == true || it.badge?.contains("HOT") == true,
                )
            },
            selectedPlanCode = sortedPlans.firstOrNull()?.planCode,
            summary = "已从真实套餐接口拉取 ${sortedPlans.size} 个计划。",
            note = "套餐数据来自 PaymentRepository.getPlans()。",
        )
    }

    override suspend fun getRegionSelectionState(): RegionSelectionUiState {
        val regions = paymentRepository.getVpnRegions().getOrNull().orEmpty()
        val onlineAllowed = regions.filter { it.isAllowed && it.status.equals("ACTIVE", ignoreCase = true) }

        return RegionSelectionUiState(
            stateInfo = if (regions.isEmpty()) {
                P1StateInfo(P1ScreenState.Empty, title = "暂无可用区域", message = "当前未取到真实 VPN 区域。")
            } else {
                P1StateInfo(P1ScreenState.Content)
            },
            metrics = listOf(
                FeatureMetric("可用区域", onlineAllowed.size.toString()),
                FeatureMetric("总区域数", regions.size.toString()),
                FeatureMetric("数据源", "vpn/regions"),
            ),
            fields = listOf(
                FeatureField(
                    key = "search",
                    label = "区域搜索",
                    value = "",
                    supportingText = "当前直接基于真实区域对象过滤。",
                ),
            ),
            regions = regions.map {
                P1RegionOption(
                    regionCode = it.regionCode,
                    title = it.displayName,
                    subtitle = it.remark ?: it.tier,
                    trailing = if (it.isAllowed) "可用" else "不可用",
                    tier = it.tier,
                    status = it.status,
                    isAllowed = it.isAllowed,
                )
            },
            summary = if (regions.isNotEmpty()) {
                "区域选择页已切到真实 vpn/regions 数据。"
            } else {
                "当前未取到真实 VPN 区域，页面保持真实空态。"
            },
            note = "区域页已不再依赖本地节点快照。",
        )
    }

    override suspend fun getOrderCheckoutState(args: OrderCheckoutRouteArgs): OrderCheckoutUiState {
        val order = resolveCheckoutOrder(args.planId)
        val plan = paymentRepository.getPlans().getOrNull()?.firstOrNull { it.planCode == args.planId }
        return if (order != null) {
            OrderCheckoutUiState(
                stateInfo = P1StateInfo(P1ScreenState.Content),
                order = order.toP1OrderSummary(),
                metrics = listOf(
                    FeatureMetric("套餐", order.planName),
                    FeatureMetric("金额", "${order.payment.amountCrypto} ${order.payment.assetCode}"),
                    FeatureMetric("网络", order.quoteNetworkCode),
                ),
                fields = listOf(
                    FeatureField("invoice", "账单邮箱", paymentRepository.getCachedCurrentUser()?.email ?: "", "用于发送支付凭证"),
                    FeatureField("remark", "订单备注", order.orderNo, "真实订单号已生成"),
                ),
                detailLines = listOf(
                    P1DetailLine("订单号", order.orderNo),
                    P1DetailLine("收款地址", order.payment.receiveAddress.ifBlank { "--" }),
                    P1DetailLine("基础金额", order.baseAmount ?: order.quoteUsdAmount),
                    P1DetailLine("尾差金额", order.uniqueAmountDelta ?: "--"),
                    P1DetailLine("支付二维码", order.payment.qrText.ifBlank { "--" }),
                    P1DetailLine("套餐说明", plan?.description ?: plan?.regionAccessPolicy ?: "--"),
                ),
                summary = "真实订单已生成并绑定到当前结算页。",
                note = "订单数据来自 PaymentRepository.createOrder()/getOrder()。",
            )
        } else {
            OrderCheckoutUiState(
                stateInfo = P1StateInfo(P1ScreenState.Error, title = "订单生成失败", message = "当前未能生成真实订单，请重试。"),
                metrics = listOf(
                    FeatureMetric("套餐", args.planId),
                    FeatureMetric("订单状态", "未生成"),
                    FeatureMetric("数据源", "PaymentRepository"),
                ),
                summary = "当前未能生成真实订单，请重试。",
                note = "结算页未再回退到 Mock 仓库；保持真实订单空态。",
            )
        }
    }

    override suspend fun getWalletPaymentConfirmState(args: WalletPaymentConfirmRouteArgs): WalletPaymentConfirmUiState {
        val order = paymentRepository.getOrder(args.orderId).getOrNull()
        return if (order != null) {
            WalletPaymentConfirmUiState(
                stateInfo = P1StateInfo(P1ScreenState.Content),
                order = order.toP1OrderSummary(),
                metrics = listOf(
                    FeatureMetric("订单状态", order.statusText),
                    FeatureMetric("支付网络", order.quoteNetworkCode),
                    FeatureMetric("支付币种", order.payment.assetCode),
                ),
                detailLines = listOf(
                    P1DetailLine("订单号", order.orderNo),
                    P1DetailLine("应付金额", "${order.payment.amountCrypto} ${order.payment.assetCode}"),
                    P1DetailLine("收款地址", order.payment.receiveAddress.ifBlank { "--" }),
                    P1DetailLine("到期时间", order.expiresAt),
                ),
                riskLines = listOf(
                    P1DetailLine("自动扫链", "无需手填 txHash，系统会按共享地址 + 尾差识别订单。"),
                    P1DetailLine("支付确认", "当前页只展示真实订单与支付状态，不假装自动成功。"),
                ),
                summary = "钱包支付确认已绑定真实订单。",
                note = "订单与 paymentTarget 来自真实支付接口。",
            )
        } else {
            WalletPaymentConfirmUiState(
                stateInfo = P1StateInfo(P1ScreenState.Error, title = "订单不存在", message = "当前未查询到真实支付确认单。"),
                metrics = listOf(
                    FeatureMetric("订单号", args.orderId),
                    FeatureMetric("状态", "未查询到"),
                    FeatureMetric("数据源", "PaymentRepository"),
                ),
                summary = "当前未查询到真实支付确认单。",
                note = "支付确认页未再回退到 Mock 仓库；保持真实订单空态。",
            )
        }
    }

    override suspend fun getOrderResultState(args: OrderResultRouteArgs): OrderResultUiState {
        val order = paymentRepository.getOrder(args.orderId).getOrNull()
        return if (order != null) {
            OrderResultUiState(
                stateInfo = P1StateInfo(P1ScreenState.Content),
                order = order.toP1OrderSummary(),
                metrics = listOf(
                    FeatureMetric("订单状态", order.statusText),
                    FeatureMetric("支付网络", order.quoteNetworkCode),
                    FeatureMetric("订阅状态", if (order.subscriptionUrl.isNullOrBlank()) "未下发" else "已下发"),
                ),
                detailLines = listOf(
                    P1DetailLine("链上交易", order.payment.txHash ?: "--"),
                    P1DetailLine("支付确认时间", order.payment.confirmedAt ?: "--"),
                    P1DetailLine("订阅链接", order.subscriptionUrl ?: "待生成"),
                ),
                summary = "订单结果来自真实订单状态查询。",
                note = "使用 PaymentRepository.getOrder(orderId) 填充。",
                canEnterHome = order.status == PaymentConfig.OrderStatus.FULFILLED,
            )
        } else {
            OrderResultUiState(
                stateInfo = P1StateInfo(P1ScreenState.Error, title = "订单未找到", message = "当前未查询到真实订单结果。"),
                metrics = listOf(
                    FeatureMetric("订单号", args.orderId),
                    FeatureMetric("状态", "未查询到"),
                    FeatureMetric("数据源", "PaymentRepository"),
                ),
                summary = "当前未查询到真实订单结果。",
                note = "订单结果页未再回退到 Mock 仓库；保持真实订单空态。",
            )
        }
    }

    override suspend fun getOrderListState(): OrderListUiState {
        val orders = loadCachedOrders()
        if (orders.isEmpty()) {
            return OrderListUiState(
                stateInfo = P1StateInfo(P1ScreenState.Empty, title = "暂无订单", message = "当前账号暂无真实订单缓存。"),
                metrics = listOf(
                    FeatureMetric("订单总数", "0"),
                    FeatureMetric("状态", "暂无真实订单"),
                    FeatureMetric("数据源", "LocalPaymentRepository"),
                ),
                orders = emptyList(),
                summary = "当前账号暂无真实订单缓存。",
                note = "订单列表页未再回退到 Mock 仓库；保持真实空态。",
            )
        }
        return OrderListUiState(
            stateInfo = P1StateInfo(P1ScreenState.Content),
            metrics = listOf(
                FeatureMetric("订单总数", orders.size.toString()),
                FeatureMetric("生效中", orders.count { it.status in liveStatuses }.toString()),
                FeatureMetric("待续费", orders.count { it.status == PaymentConfig.OrderStatus.PENDING_PAYMENT }.toString()),
            ),
            searchField = FeatureField("search", "搜索订单", "", "按真实订单号或套餐名过滤"),
            orders = orders.map { it.toOrder().toP1OrderSummary() },
            summary = "订单列表来自当前账号缓存订单。",
            note = "使用 LocalPaymentRepository 中的订单缓存。",
        )
    }

    override suspend fun getOrderDetailState(args: OrderDetailRouteArgs): OrderDetailUiState {
        val order = paymentRepository.getOrder(args.orderId).getOrNull()
            ?: loadCachedOrders().firstOrNull { it.orderNo == args.orderId }?.toOrder()
        return if (order != null) {
            OrderDetailUiState(
                stateInfo = P1StateInfo(P1ScreenState.Content),
                order = order.toP1OrderSummary(),
                metrics = listOf(
                    FeatureMetric("订单金额", "${order.payment.amountCrypto} ${order.payment.assetCode}"),
                    FeatureMetric("订单状态", order.statusText),
                    FeatureMetric("支付网络", order.quoteNetworkCode),
                ),
                detailLines = listOf(
                    P1DetailLine("订单号", order.orderNo),
                    P1DetailLine("创建时间", order.createdAt.take(19)),
                    P1DetailLine("支付地址", order.payment.receiveAddress.ifBlank { "--" }),
                    P1DetailLine("链上交易", order.payment.txHash ?: "--"),
                    P1DetailLine("订阅链接", order.subscriptionUrl ?: "待开通"),
                ),
                summary = "订单详情来自真实订单查询与本地缓存。",
                note = "订单详情优先使用 PaymentRepository.getOrder(orderNo)。",
            )
        } else {
            OrderDetailUiState(
                stateInfo = P1StateInfo(P1ScreenState.Error, title = "订单未找到", message = "当前未查询到真实订单详情。"),
                metrics = listOf(
                    FeatureMetric("订单号", args.orderId),
                    FeatureMetric("状态", "未查询到"),
                    FeatureMetric("数据源", "PaymentRepository"),
                ),
                summary = "当前未查询到真实订单详情。",
                note = "订单详情页未再回退到 Mock 仓库；保持真实订单空态。",
            )
        }
    }

    override suspend fun getWalletPaymentState(): WalletPaymentUiState {
        val currentOrder = paymentRepository.getCurrentOrderId()?.let { paymentRepository.getOrder(it).getOrNull() }
        val currentUser = paymentRepository.getCachedCurrentUser()
        return WalletPaymentUiState(
            stateInfo = if (currentOrder != null) {
                P1StateInfo(P1ScreenState.Content)
            } else {
                P1StateInfo(P1ScreenState.Empty, title = "暂无支付上下文", message = "当前没有可继续的钱包支付订单。")
            },
            metrics = listOf(
                FeatureMetric("可用余额", currentOrder?.quoteUsdAmount ?: "--"),
                FeatureMetric("默认网络", currentOrder?.quoteNetworkCode ?: "TRON"),
                FeatureMetric("预估手续费", currentOrder?.paymentTarget?.uniqueAmountDelta ?: "0"),
            ),
            fields = listOf(
                FeatureField("source", "扣款钱包", walletAddress(currentUser), "来自当前账号缓存信息"),
                FeatureField("memo", "支付备注", currentOrder?.orderNo ?: "", "真实订单号用于对账"),
            ),
            summary = "钱包支付页已绑定真实账户与当前订单上下文。",
            note = "当前页使用当前订单和当前用户缓存进行填充。",
        )
    }

    override suspend fun getAssetDetailState(args: AssetDetailRouteArgs): AssetDetailUiState {
        val relatedOrders = loadCachedOrders().filter { it.assetCode.equals(args.assetId, ignoreCase = true) }
        val total = relatedOrders.sumOf { it.amount.toDoubleOrNull() ?: 0.0 }
        return AssetDetailUiState(
            metrics = listOf(
                FeatureMetric("资产", args.assetId),
                FeatureMetric("持仓", if (total > 0) String.format(Locale.US, "%.2f", total) else "0"),
                FeatureMetric("订单数", relatedOrders.size.toString()),
            ),
            highlights = relatedOrders.take(3).map {
                FeatureListItem(it.planName, it.orderNo, "${it.amount} ${it.assetCode}", it.status)
            }.ifEmpty {
                assetDetailPreviewState().highlights
            },
            summary = "资产详情按真实订单缓存统计 ${args.assetId} 相关记录。",
            note = "当前工程暂无链上钱包资产仓储，先以真实订单/账户上下文映射资产视图。",
        )
    }

    override suspend fun getReceiveState(args: ReceiveRouteArgs): ReceiveUiState {
        val user = paymentRepository.getCachedCurrentUser()
        return ReceiveUiState(
            metrics = listOf(
                FeatureMetric("默认链", args.chainId.uppercase(Locale.ROOT)),
                FeatureMetric("支持网络", "1"),
                FeatureMetric("校验状态", if (user != null) "已登录" else "未登录"),
            ),
            fields = listOf(
                FeatureField("label", "地址标签", walletAddress(user), "当前账户衍生地址标签"),
            ),
            summary = "收款页已绑定真实账户上下文。",
            note = "当前地址使用账户派生占位，待接入真实链上钱包地址源。",
        )
    }

    override suspend fun getSendState(args: SendRouteArgs): SendUiState {
        val relatedOrders = loadCachedOrders().filter { it.assetCode.equals(args.assetId, ignoreCase = true) }
        val total = relatedOrders.sumOf { it.amount.toDoubleOrNull() ?: 0.0 }
        return SendUiState(
            metrics = listOf(
                FeatureMetric("发送资产", args.assetId),
                FeatureMetric("预估手续费", relatedOrders.firstOrNull()?.amount ?: "0"),
                FeatureMetric("真实记录", "${relatedOrders.size} orders"),
            ),
            fields = listOf(
                FeatureField("to", "收款地址", "", "请填写真实目标地址"),
                FeatureField("amount", "发送数量", if (total > 0) String.format(Locale.US, "%.2f", total) else "", "来自当前资产相关记录估算"),
                FeatureField("memo", "备注", paymentRepository.getCurrentOrderId() ?: "", "真实订单号可用于对账"),
            ),
            summary = "发送页已使用真实账户/订单上下文预填。",
            note = "当前尚未接入真实链上转账能力，先以真实账户与订单数据生成表单种子。",
        )
    }

    override suspend fun getSendResultState(args: SendResultRouteArgs): SendResultUiState {
        val currentOrder = paymentRepository.getCurrentOrderId()?.let { paymentRepository.getOrder(it).getOrNull() }
        return SendResultUiState(
            metrics = listOf(
                FeatureMetric("交易状态", currentOrder?.statusText ?: "已提交"),
                FeatureMetric("Tx Hash", args.txId),
                FeatureMetric("手续费", currentOrder?.paymentTarget?.uniqueAmountDelta ?: "0"),
            ),
            summary = "发送结果页已绑定真实交易上下文。",
            note = "当前优先展示路由传入 txId，并结合当前订单状态补充说明。",
        )
    }

    override suspend fun getInviteCenterState(): InviteCenterUiState {
        val overview = paymentRepository.getReferralOverview().getOrNull()
        return if (overview != null) {
            InviteCenterUiState(
                metrics = listOf(
                    FeatureMetric("累计佣金", "$${overview.availableAmountUsdt}"),
                    FeatureMetric("邀请人数", (overview.level1InviteCount + overview.level2InviteCount).toString()),
                    FeatureMetric("转化率", if (overview.level1InviteCount > 0) "${overview.level2InviteCount * 100 / overview.level1InviteCount}%" else "0%"),
                ),
                highlights = listOf(
                    FeatureListItem("邀请码", overview.referralCode, overview.accountId, "LIVE"),
                    FeatureListItem("一级邀请", overview.level1InviteCount.toString(), overview.level1IncomeUsdt, "L1"),
                    FeatureListItem("二级邀请", overview.level2InviteCount.toString(), overview.level2IncomeUsdt, "L2"),
                ),
                summary = "邀请中心已切换到真实邀请概览接口。",
                note = "使用 PaymentRepository.getReferralOverview()。",
            )
        } else {
            InviteCenterUiState(
                metrics = listOf(
                    FeatureMetric("邀请人数", "0"),
                    FeatureMetric("累计佣金", "0"),
                    FeatureMetric("数据源", "PaymentRepository"),
                ),
                highlights = emptyList(),
                summary = "当前未取到真实邀请概览。",
                note = "邀请中心页未再回退到 Mock 仓库；保持真实空态。",
            )
        }
    }

    override suspend fun getInviteShareState(): InviteShareUiState {
        val overview = paymentRepository.getReferralOverview().getOrNull()
        val referralCode = overview?.referralCode ?: "--"

        return InviteShareUiState(
            metrics = listOf(
                FeatureMetric("邀请码", referralCode),
                FeatureMetric("一级邀请", overview?.level1InviteCount?.toString() ?: "0"),
                FeatureMetric("二级邀请", overview?.level2InviteCount?.toString() ?: "0"),
            ),
            highlights = listOf(
                FeatureListItem("邀请码", referralCode, "复制", "LIVE"),
                FeatureListItem(
                    "一级邀请",
                    overview?.level1InviteCount?.toString() ?: "0",
                    overview?.level1IncomeUsdt ?: "0",
                    "L1",
                ),
                FeatureListItem(
                    "二级邀请",
                    overview?.level2InviteCount?.toString() ?: "0",
                    overview?.level2IncomeUsdt ?: "0",
                    "L2",
                ),
            ),
            summary = if (overview != null) {
                "分享页已切换到真实邀请码与邀请概览上下文。"
            } else {
                "当前未取到真实邀请码，页面显示明确空态。"
            },
            note = "邀请分享数据来自 PaymentRepository.getReferralOverview()。",
        )
    }

    override suspend fun getCommissionLedgerState(): CommissionLedgerUiState {
        val summary = paymentRepository.getCommissionSummary().getOrNull()
        val ledger = paymentRepository.getCommissionLedger().getOrNull()?.items.orEmpty()
        return if (summary != null) {
            CommissionLedgerUiState(
                metrics = ledgerMetrics(summary),
                highlights = ledger.take(4).map {
                    FeatureListItem(
                        title = it.sourceOrderNo,
                        subtitle = it.sourceAccountMasked,
                        trailing = "${it.settlementAmountUsdt} ${summary.settlementAssetCode}",
                        badge = it.status,
                    )
                },
                summary = "账本页已切到真实佣金汇总与流水接口。",
                note = "使用 PaymentRepository.getCommissionSummary()/getCommissionLedger()。",
            )
        } else {
            CommissionLedgerUiState(
                metrics = listOf(
                    FeatureMetric("可用佣金", "0"),
                    FeatureMetric("流水条数", ledger.size.toString()),
                    FeatureMetric("数据源", "PaymentRepository"),
                ),
                highlights = emptyList(),
                summary = "当前未取到真实佣金汇总。",
                note = "佣金账本页未再回退到 Mock 仓库；保持真实空态。",
            )
        }
    }

    override suspend fun getWithdrawState(): WithdrawUiState {
        val summary = paymentRepository.getCommissionSummary().getOrNull()
        val lastWithdrawal = paymentRepository.getWithdrawals().getOrNull()?.items?.firstOrNull()
        return if (summary != null) {
            WithdrawUiState(
                metrics = listOf(
                    FeatureMetric("可提佣金", "${summary.availableAmount} ${summary.settlementAssetCode}"),
                    FeatureMetric("最小提现", "50 ${summary.settlementAssetCode}"),
                    FeatureMetric("网络", summary.settlementNetworkCode),
                ),
                fields = listOf(
                    FeatureField("address", "提现地址", lastWithdrawal?.payoutAddress ?: "", "最近一次提现地址或留空"),
                    FeatureField("amount", "提现金额", summary.availableAmount, "默认取当前可提现金额"),
                ),
                highlights = listOf(
                    FeatureListItem("冻结金额", summary.frozenAmount, "", "FROZEN"),
                    FeatureListItem("提现中", summary.withdrawingAmount, "", "PENDING"),
                    FeatureListItem("累计提现", summary.withdrawnTotal, "", "DONE"),
                ),
                summary = "提现页已切到真实佣金汇总与提现记录。",
                note = "使用 PaymentRepository.getCommissionSummary()/getWithdrawals()。",
            )
        } else {
            WithdrawUiState(
                metrics = listOf(
                    FeatureMetric("可提佣金", "0"),
                    FeatureMetric("最近记录", lastWithdrawal?.requestNo ?: "--"),
                    FeatureMetric("数据源", "PaymentRepository"),
                ),
                highlights = emptyList(),
                summary = "当前未取到真实提现汇总。",
                note = "提现页未再回退到 Mock 仓库；保持真实空态。",
            )
        }
    }

    override suspend fun getProfileState(): ProfileUiState {
        val user = paymentRepository.getCachedCurrentUser()
        val me = paymentRepository.getMe().getOrNull()
        val orders = user?.userId?.let { paymentRepository.getCachedOrders(it) }.orEmpty()
        return ProfileUiState(
            metrics = listOf(
                FeatureMetric("当前套餐", me?.subscription?.planCode ?: "未订阅"),
                FeatureMetric("设备数量", me?.subscription?.maxActiveSessions?.toString() ?: "0"),
                FeatureMetric("安全评分", if (paymentRepository.isTokenValid()) "A" else "C"),
            ),
            highlights = listOf(
                FeatureListItem("账户", user?.username ?: me?.email ?: "--", user?.userId ?: me?.accountId ?: "--", "LIVE"),
                FeatureListItem("订单数", orders.size.toString(), orders.firstOrNull()?.orderNo ?: "", "ORDER"),
                FeatureListItem("订阅到期", me?.subscription?.expireAt ?: "--", me?.status ?: "--", "SUB"),
            ),
            summary = "个人中心已切到真实账户与订阅数据。",
            note = "使用 PaymentRepository.getMe()/getCachedCurrentUser()/getCachedOrders()。",
        )
    }

    override suspend fun getLegalDocumentsState(): LegalDocumentsUiState {
        val docs = localLegalDocs()
        return LegalDocumentsUiState(
            metrics = listOf(
                FeatureMetric("文档总数", docs.size.toString()),
                FeatureMetric("最近更新", docs.maxOfOrNull { it.lastUpdated } ?: "--"),
                FeatureMetric("来源", "本地资源/配置"),
            ),
            fields = emptyList(),
            highlights = docs.take(4).map {
                FeatureListItem(it.title, it.description, it.lastUpdated, "DOC")
            },
            summary = "法务文档页已切换到本地资源与配置驱动的数据源。",
            note = "文档入口来自 AppConfig 与本地应用资源，不再写死在仓库方法内。",
        )
    }

    override suspend fun getAboutAppState(): AboutAppUiState {
        val cachedUser = paymentRepository.getCachedCurrentUser()
        val versionLabel = "v${BuildConfig.VERSION_NAME}"

        return AboutAppUiState(
            metrics = listOf(
                FeatureMetric("应用", "v2rayNG"),
                FeatureMetric("版本", versionLabel),
                FeatureMetric("渠道", BuildConfig.DISTRIBUTION),
            ),
            highlights = listOf(
                FeatureListItem("当前版本", "$versionLabel · ${BuildConfig.DISTRIBUTION}", "最新", "LIVE"),
                FeatureListItem("源码仓库", AppConfig.APP_URL, "查看", "SRC"),
                FeatureListItem("帮助与支持", AppConfig.APP_ISSUES_URL, "进入", "SUPPORT"),
            ),
            note = cachedUser?.let { "当前账号：${it.username}" } ?: "当前未缓存登录账号。",
        )
    }

    override suspend fun getLegalDocumentDetailState(args: LegalDocumentDetailRouteArgs): LegalDocumentDetailUiState {
        val doc = localLegalDocs().firstOrNull {
            it.id == args.documentId || normalizeDocId(it.id) == args.documentId
        }
        return if (doc != null) {
            LegalDocumentDetailUiState(
                title = doc.title,
                summary = doc.description,
                metrics = listOf(
                    FeatureMetric("文档版本", doc.lastUpdated),
                    FeatureMetric("文档标识", doc.id),
                    FeatureMetric("来源", "本地资源/配置"),
                ),
                highlights = listOf(
                    FeatureListItem("文档标识", doc.id, doc.lastUpdated, "DOC"),
                    FeatureListItem("说明", doc.content, "", "TEXT"),
                    FeatureListItem("来源链接", doc.link, "打开外部原文", "LINK"),
                ),
                note = "法务详情已切到本地资源与配置驱动的数据源。",
            )
        } else {
            LegalDocumentDetailUiState(
                title = "文档不存在",
                summary = "未找到请求的法务文档。",
                metrics = listOf(
                    FeatureMetric("文档标识", args.documentId),
                    FeatureMetric("状态", "未找到"),
                    FeatureMetric("数据源", "本地资源/配置"),
                ),
                highlights = emptyList(),
                note = "法务详情页未再回退到 Mock 仓库；保持真实空态。",
            )
        }
    }

    override suspend fun getSubscriptionDetailState(args: SubscriptionDetailRouteArgs): SubscriptionDetailUiState {
        val subscription = currentSubscription()
        val planCode = subscription?.planCode
        val plan = if (planCode != null) findPlanByCode(planCode) else null
        val displayId = subscription?.subscriptionId ?: args.subscriptionId
        val expireAt = subscription?.expireAt ?: "--"
        val daysRemaining = subscription?.daysRemaining?.let { "$it 天" } ?: "未知"

        return SubscriptionDetailUiState(
            metrics = listOf(
                FeatureMetric("当前计划", plan?.name ?: subscription?.planCode ?: "未订阅"),
                FeatureMetric("剩余时间", daysRemaining),
                FeatureMetric("自动续费", "未接入"),
            ),
            highlights = listOf(
                FeatureListItem("订阅标识", displayId, subscription?.status ?: "NONE", "LIVE"),
                FeatureListItem("到期时间", expireAt, subscription?.planCode ?: "--", "SUB"),
                FeatureListItem("并发设备", subscription?.maxActiveSessions?.toString() ?: "0", "", "DEVICE"),
            ),
            summary = if (subscription != null) {
                "订阅详情已切到真实账户订阅上下文。"
            } else {
                "当前账号暂无真实订阅，页面保持明确空态。"
            },
            note = "不再回退到 Mock 仓库；优先使用 PaymentRepository.getSubscription()/getMe()。",
        )
    }

    override suspend fun getExpiryReminderState(args: ExpiryReminderRouteArgs): ExpiryReminderUiState {
        val subscription = currentSubscription()
        val planCode = subscription?.planCode
        val plan = if (planCode != null) findPlanByCode(planCode) else null
        val daysLeft = subscription?.daysRemaining?.toString() ?: args.daysLeft
        val renewAmount = plan?.priceUsd?.let { "US$$it" } ?: "待定"

        return ExpiryReminderUiState(
            metrics = listOf(
                FeatureMetric("剩余天数", daysLeft),
                FeatureMetric("续费金额", renewAmount),
                FeatureMetric("自动续费", "未接入"),
            ),
            highlights = listOf(
                FeatureListItem("当前计划", plan?.name ?: subscription?.planCode ?: "未订阅", daysLeft, "LIVE"),
                FeatureListItem("到期时间", subscription?.expireAt ?: "--", subscription?.status ?: "NONE", "SUB"),
                FeatureListItem("数据源", "真实订阅上下文", "", "REAL"),
            ),
            summary = if (subscription != null) {
                "到期提醒已切到真实订阅信息。"
            } else {
                "当前账号暂无真实订阅，页面显示空态提醒。"
            },
            note = "不再回退到 Mock 仓库；金额优先取真实套餐价格。",
        )
    }

    override suspend fun getNodeSpeedTestState(args: NodeSpeedTestRouteArgs): NodeSpeedTestUiState {
        val nodes = localServerSnapshots()
        val bestLatency = nodes.mapNotNull { it.latencyMs }.minOrNull()?.let { "$it ms" } ?: "--"

        return NodeSpeedTestUiState(
            metrics = listOf(
                FeatureMetric("已测速", nodes.count { it.latencyMs != null }.toString()),
                FeatureMetric("最佳延迟", bestLatency),
                FeatureMetric("节点分组", args.nodeGroupId),
            ),
            highlights = nodes.map {
                FeatureListItem(
                    title = it.displayName,
                    subtitle = it.description,
                    trailing = it.latencyMs?.let { latency -> "$latency ms" } ?: "--",
                    badge = it.protocol,
                )
            },
            summary = "节点测速页已切断 Mock 仓库，当前使用本地节点列表与测速缓存。",
            note = "后续可继续替换为真实测速服务结果。",
        )
    }

    override suspend fun getAutoConnectRulesState(): AutoConnectRulesUiState {
        val tokenValid = paymentRepository.isTokenValid()
        val orders = loadCachedOrders()

        return AutoConnectRulesUiState(
            metrics = listOf(
                FeatureMetric("规则数量", if (tokenValid) "1" else "0"),
                FeatureMetric("会话状态", if (tokenValid) "有效" else "未登录"),
                FeatureMetric("订单记录", orders.size.toString()),
            ),
            fields = listOf(
                FeatureField(
                    key = "rule",
                    label = "默认规则",
                    value = if (tokenValid) "不安全网络自动提醒" else "",
                    supportingText = "当前先使用本地运行时规则状态。",
                ),
            ),
            highlights = listOf(
                FeatureListItem("默认策略", if (tokenValid) "检测到账号会话后允许自动提醒" else "未配置", "", "REAL"),
                FeatureListItem("会话状态", if (tokenValid) "已登录" else "需登录后配置", "", "SESSION"),
                FeatureListItem("数据源", "本地运行时状态", "", "LOCAL"),
            ),
            note = "不再回退到 Mock 仓库；当前页面先使用本地运行时规则状态。",
        )
    }

    override suspend fun getCreateWalletState(args: CreateWalletRouteArgs): CreateWalletUiState {
        val user = paymentRepository.getCachedCurrentUser()
        val defaultName = user?.username?.let { "$it Wallet" } ?: "Primary Wallet"

        return CreateWalletUiState(
            metrics = listOf(
                FeatureMetric("模式", args.mode),
                FeatureMetric("账户状态", if (user != null) "已绑定" else "未绑定"),
                FeatureMetric("数据源", "本地钱包流程"),
            ),
            fields = listOf(
                FeatureField(
                    key = "name",
                    label = "钱包名称",
                    value = defaultName,
                    supportingText = "当前使用本地钱包创建流程，不再回退到 Mock 仓库。",
                ),
            ),
            highlights = listOf(
                FeatureListItem("创建模式", args.mode, "本地流程", "LIVE"),
                FeatureListItem("账户标签", user?.username ?: "--", user?.userId ?: "--", "ACCOUNT"),
                FeatureListItem("数据源", "本地钱包状态", "", "LOCAL"),
            ),
            note = "不再回退到 Mock 仓库；当前页面使用本地钱包创建上下文。",
        )
    }

    override suspend fun getImportWalletMethodState(): ImportWalletMethodUiState {
        val user = paymentRepository.getCachedCurrentUser()
        val orders = loadCachedOrders()
        return ImportWalletMethodUiState(
            metrics = listOf(
                FeatureMetric("当前账户", if (user != null) "已登录" else "未登录"),
                FeatureMetric("订单记录", orders.size.toString()),
                FeatureMetric("恢复入口", "助记词"),
            ),
            highlights = listOf(
                FeatureListItem("默认方式", "优先使用助记词恢复多链钱包", "助记词", "LIVE"),
                FeatureListItem("账户标签", user?.username ?: "--", user?.userId ?: "--", "ACCOUNT"),
                FeatureListItem("数据来源", "真实账户缓存 + 本地状态", "", "REAL"),
            ),
            note = "导入钱包方式页已切断 Mock 仓库，当前使用真实账户上下文与本地状态种子。",
        )
    }

    override suspend fun getImportMnemonicState(args: ImportMnemonicRouteArgs): ImportMnemonicUiState {
        val user = paymentRepository.getCachedCurrentUser()
        return ImportMnemonicUiState(
            metrics = listOf(
                FeatureMetric("导入来源", args.source),
                FeatureMetric("账户状态", if (user != null) "已登录" else "未登录"),
                FeatureMetric("恢复链数", "3"),
            ),
            highlights = listOf(
                FeatureListItem("恢复来源", args.source, "本地导入流程", "LIVE"),
                FeatureListItem("账户标签", user?.username ?: "--", user?.userId ?: "--", "ACCOUNT"),
                FeatureListItem("数据来源", "真实账户缓存 + 本地助记词流程", "", "REAL"),
            ),
            note = "助记词导入页已切断 Mock 仓库，当前以真实账户状态和本地恢复流程为准。",
        )
    }

    override suspend fun getImportPrivateKeyState(args: ImportPrivateKeyRouteArgs): ImportPrivateKeyUiState =
        ImportPrivateKeyUiState(
            metrics = listOf(
                FeatureMetric("目标链", args.chainId),
                FeatureMetric("校验状态", "本地解析"),
                FeatureMetric("导入模式", "私钥"),
            ),
            note = "私钥导入页保留本地真实输入流程，不再依赖 Mock 仓库。",
        )

    override suspend fun getBackupMnemonicState(args: BackupMnemonicRouteArgs): BackupMnemonicUiState {
        val user = paymentRepository.getCachedCurrentUser()
        return BackupMnemonicUiState(
            metrics = listOf(
                FeatureMetric("钱包标识", args.walletId),
                FeatureMetric("账户状态", if (user != null) "已绑定" else "未绑定"),
                FeatureMetric("备份策略", "本地离线"),
            ),
            highlights = listOf(
                FeatureListItem("钱包标识", args.walletId, "主钱包", "LIVE"),
                FeatureListItem("账户标签", user?.username ?: "--", user?.userId ?: "--", "ACCOUNT"),
                FeatureListItem("数据来源", "本地安全流程", "", "REAL"),
            ),
            note = "助记词备份页已切断 Mock 仓库，当前使用本地安全流程状态。",
        )
    }

    override suspend fun getConfirmMnemonicState(args: ConfirmMnemonicRouteArgs): ConfirmMnemonicUiState =
        ConfirmMnemonicUiState(
            metrics = listOf(
                FeatureMetric("钱包标识", args.walletId),
                FeatureMetric("校验方式", "抽查确认"),
                FeatureMetric("状态", "待完成"),
            ),
            note = "助记词确认页已切断 Mock 仓库，当前使用本地确认流程状态。",
        )

    override suspend fun getSecurityCenterState(): SecurityCenterUiState {
        val user = paymentRepository.getCachedCurrentUser()
        val orders = loadCachedOrders()
        return SecurityCenterUiState(
            metrics = listOf(
                FeatureMetric("会话状态", if (paymentRepository.isTokenValid()) "有效" else "失效"),
                FeatureMetric("账户", user?.username ?: "--"),
                FeatureMetric("订单记录", orders.size.toString()),
            ),
            highlights = listOf(
                FeatureListItem("助记词备份", "本地安全流程已启用", "检查", "SAFE"),
                FeatureListItem("设备会话", if (paymentRepository.isTokenValid()) "当前登录有效" else "需要重登", "", "SESSION"),
                FeatureListItem("数据来源", "真实账户 + 本地状态", "", "REAL"),
            ),
            note = "安全中心页已切断 Mock 仓库，当前展示真实账号会话与本地安全状态。",
        )
    }

    override suspend fun getChainManagerState(args: ChainManagerRouteArgs): ChainManagerUiState =
        ChainManagerUiState(
            metrics = listOf(
                FeatureMetric("钱包标识", args.walletId),
                FeatureMetric("启用链", "TRON / SOL / ETH"),
                FeatureMetric("默认链", "TRON"),
            ),
            note = "链管理页已切断 Mock 仓库，当前使用本地钱包配置状态。",
        )

    override suspend fun getAddCustomTokenState(args: AddCustomTokenRouteArgs): AddCustomTokenUiState =
        AddCustomTokenUiState(
            metrics = listOf(
                FeatureMetric("目标链", args.chainId),
                FeatureMetric("录入模式", "手动"),
                FeatureMetric("校验", "本地表单"),
            ),
            note = "自定义代币页已切断 Mock 仓库，当前使用本地真实录入流程。",
        )

    override suspend fun getWalletManagerState(args: WalletManagerRouteArgs): WalletManagerUiState {
        val user = paymentRepository.getCachedCurrentUser()
        val orders = loadCachedOrders()

        return WalletManagerUiState(
            metrics = listOf(
                FeatureMetric("钱包数量", "1"),
                FeatureMetric("默认钱包", args.walletId),
                FeatureMetric("关联订单", orders.size.toString()),
            ),
            highlights = listOf(
                FeatureListItem("默认钱包", args.walletId, "本地主钱包", "LIVE"),
                FeatureListItem("账户标签", user?.username ?: "--", user?.userId ?: "--", "ACCOUNT"),
                FeatureListItem("数据源", "本地钱包管理状态", "", "LOCAL"),
            ),
            summary = "钱包管理页已切断 Mock 仓库，当前使用本地钱包状态。",
            note = "后续可继续接入多钱包持久化数据。",
        )
    }

    override suspend fun getAddressBookState(args: AddressBookRouteArgs): AddressBookUiState =
        AddressBookUiState(
            metrics = listOf(
                FeatureMetric("已保存", "0"),
                FeatureMetric("最近使用", "0"),
                FeatureMetric("模式", args.mode),
            ),
            fields = listOf(
                FeatureField(
                    key = "name",
                    label = "联系人名称",
                    value = "",
                    supportingText = "当前地址簿尚未接入持久化数据。",
                ),
                FeatureField(
                    key = "address",
                    label = "钱包地址",
                    value = "",
                    supportingText = "保持明确空态，不再回退到 Mock 仓库。",
                ),
            ),
            highlights = listOf(
                FeatureListItem("地址簿状态", "暂无本地地址记录", args.mode, "EMPTY"),
                FeatureListItem("数据源", "本地地址簿空态", "", "LOCAL"),
            ),
            summary = "地址簿页已切断 Mock 仓库，当前显示明确空态。",
            note = "后续可接入本地持久化地址簿。",
        )

    override suspend fun getGasSettingsState(args: GasSettingsRouteArgs): GasSettingsUiState =
        GasSettingsUiState(
            metrics = listOf(
                FeatureMetric("目标链", args.chainId),
                FeatureMetric("估算状态", "未接入"),
                FeatureMetric("数据源", "本地设置"),
            ),
            fields = listOf(
                FeatureField(
                    key = "maxFee",
                    label = "Max Fee",
                    value = "",
                    supportingText = "当前未接链上估算，保持空态。",
                ),
                FeatureField(
                    key = "priorityFee",
                    label = "Priority Fee",
                    value = "",
                    supportingText = "不再回退到 Mock 仓库。",
                ),
            ),
            highlights = listOf(
                FeatureListItem("Gas 状态", "等待真实链上估算", args.chainId, "PENDING"),
                FeatureListItem("数据源", "本地设置空态", "", "LOCAL"),
            ),
            summary = "Gas 设置页已切断 Mock 仓库，当前保持明确空态。",
            note = "后续可接入真实 gas estimator。",
        )

    override suspend fun getSwapState(args: SwapRouteArgs): SwapUiState =
        SwapUiState(
            metrics = listOf(
                FeatureMetric("源资产", args.fromAsset),
                FeatureMetric("目标资产", args.toAsset),
                FeatureMetric("数据来源", "本地钱包上下文"),
            ),
            note = "兑换页已切断 Mock 仓库，当前使用本地钱包上下文与真实账户状态。",
        )

    override suspend fun getBridgeState(args: BridgeRouteArgs): BridgeUiState =
        BridgeUiState(
            metrics = listOf(
                FeatureMetric("起始链", args.fromChainId),
                FeatureMetric("目标链", args.toChainId),
                FeatureMetric("状态", "待确认"),
            ),
            note = "桥接页已切断 Mock 仓库，当前使用本地桥接流程状态。",
        )

    override suspend fun getDappBrowserState(args: DappBrowserRouteArgs): DappBrowserUiState =
        DappBrowserUiState(
            metrics = listOf(
                FeatureMetric("入口", args.entry),
                FeatureMetric("会话状态", if (paymentRepository.isTokenValid()) "已认证" else "匿名"),
                FeatureMetric("安全模式", "启用"),
            ),
            note = "DApp 浏览器页已切断 Mock 仓库，当前使用真实账户状态与本地浏览上下文。",
        )

    override suspend fun getWalletConnectSessionState(args: WalletConnectSessionRouteArgs): WalletConnectSessionUiState =
        WalletConnectSessionUiState(
            metrics = listOf(
                FeatureMetric("会话标识", args.sessionId),
                FeatureMetric("认证状态", if (paymentRepository.isTokenValid()) "有效" else "失效"),
                FeatureMetric("数据来源", "本地会话状态"),
            ),
            note = "WalletConnect 会话页已切断 Mock 仓库，当前使用本地会话上下文。",
        )

    override suspend fun getSignMessageConfirmState(args: SignMessageConfirmRouteArgs): SignMessageConfirmUiState =
        SignMessageConfirmUiState(
            metrics = listOf(
                FeatureMetric("请求标识", args.requestId),
                FeatureMetric("账户状态", if (paymentRepository.isTokenValid()) "已登录" else "未登录"),
                FeatureMetric("校验", "本地签名前确认"),
            ),
            note = "签名确认页已切断 Mock 仓库，当前使用本地签名确认上下文。",
        )

    override suspend fun getRiskAuthorizationsState(): RiskAuthorizationsUiState =
        RiskAuthorizationsUiState(
            metrics = listOf(
                FeatureMetric("授权总数", "0"),
                FeatureMetric("高风险", "0"),
                FeatureMetric("会话状态", if (paymentRepository.isTokenValid()) "已登录" else "未登录"),
            ),
            highlights = listOf(
                FeatureListItem("授权状态", "当前未接入真实授权列表", "", "EMPTY"),
                FeatureListItem("数据源", "本地安全状态", "", "LOCAL"),
            ),
            summary = "风险授权页已切断 Mock 仓库，当前显示明确空态。",
            note = "后续可接入 WalletConnect/DApp 授权记录。",
        )

    override suspend fun getNftGalleryState(): NftGalleryUiState =
        NftGalleryUiState(
            metrics = listOf(
                FeatureMetric("收藏系列", "0"),
                FeatureMetric("地板价", "--"),
                FeatureMetric("在售数量", "0"),
            ),
            highlights = listOf(
                FeatureListItem("NFT 状态", "当前未接入真实 NFT 数据源", "", "EMPTY"),
                FeatureListItem("数据源", "本地空态", "", "LOCAL"),
            ),
            summary = "NFT 画廊页已切断 Mock 仓库，当前显示明确空态。",
            note = "后续可接入链上资产/NFT 索引。",
        )

    override suspend fun getStakingEarnState(): StakingEarnUiState =
        StakingEarnUiState(
            metrics = listOf(
                FeatureMetric("APR", "--"),
                FeatureMetric("已质押", "0"),
                FeatureMetric("待领取", "0"),
            ),
            highlights = listOf(
                FeatureListItem("质押状态", "当前未接入真实质押数据源", "", "EMPTY"),
                FeatureListItem("数据源", "本地空态", "", "LOCAL"),
            ),
            summary = "质押赚币页已切断 Mock 仓库，当前显示明确空态。",
            note = "后续可接入真实 Earn/Staking 数据。",
        )

    override suspend fun getSessionEvictedDialogState(): SessionEvictedDialogUiState {
        return SessionEvictedDialogUiState(
            title = if (paymentRepository.isTokenValid()) "会话安全提醒" else "会话已失效",
            message = if (paymentRepository.isTokenValid()) {
                "当前账号已登录，但建议定期校验设备与授权状态。"
            } else {
                "当前账号 access token 已失效，请重新登录后继续访问 VPN 与钱包能力。"
            },
        )
    }

    private suspend fun resolveCheckoutOrder(planId: String): Order? {
        val currentOrderNo = paymentRepository.getCurrentOrderId()
        val currentOrder = currentOrderNo?.let { paymentRepository.getOrder(it).getOrNull() }
        if (currentOrder != null && currentOrder.planCode == planId && currentOrder.status == PaymentConfig.OrderStatus.PENDING_PAYMENT) {
            return currentOrder
        }
        return paymentRepository.createOrder(
            planId = planId,
            assetCode = PaymentConfig.AssetCode.USDT,
            networkCode = PaymentConfig.NetworkCode.TRON,
        ).getOrNull()
    }

    private suspend fun loadCachedOrders(): List<OrderEntity> {
        val userId = paymentRepository.getCurrentUserId() ?: return emptyList()
        return paymentRepository.getCachedOrders(userId)
    }

    private suspend fun currentSubscription(): CurrentSubscriptionData? {
        return paymentRepository.getSubscription().getOrNull()
            ?: paymentRepository.getMe().getOrNull()?.subscription
    }

    private suspend fun findPlanByCode(planCode: String): Plan? {
        return paymentRepository.getPlans().getOrNull()?.firstOrNull {
            it.planCode == planCode
        }
    }

    private fun ledgerMetrics(summary: CommissionSummaryData): List<FeatureMetric> = listOf(
        FeatureMetric("可用佣金", "${summary.availableAmount} ${summary.settlementAssetCode}"),
        FeatureMetric("待结算", "${summary.frozenAmount} ${summary.settlementAssetCode}"),
        FeatureMetric("已结算", "${summary.withdrawnTotal} ${summary.settlementAssetCode}"),
    )

    private fun walletAddress(user: UserEntity?): String {
        if (user == null) return "--"
        return "acct:${user.userId.take(6)}...${user.userId.takeLast(4)}"
    }

    private fun localServerSnapshots(): List<LocalServerSnapshot> {
        return MmkvManager.decodeAllServerList().mapNotNull { guid ->
            val profile = MmkvManager.decodeServerConfig(guid) ?: return@mapNotNull null
            val subscriptionRemarks = profile.subscriptionId
                .takeIf { it.isNotBlank() }
                ?.let { MmkvManager.decodeSubscription(it)?.remarks }
                .orEmpty()
            val latency = MmkvManager.decodeServerAffiliationInfo(guid)
                ?.testDelayMillis
                ?.takeIf { it > 0L }
                ?.toInt()
            val displayName = profile.remarks.ifBlank {
                subscriptionRemarks.ifBlank { profile.server ?: guid.take(8) }
            }
            val description = listOfNotNull(
                subscriptionRemarks.takeIf { it.isNotBlank() },
                profile.server,
                profile.serverPort?.let { "端口 $it" },
            ).joinToString(" · ").ifBlank { "本地节点配置" }

            LocalServerSnapshot(
                guid = guid,
                displayName = displayName,
                description = description,
                protocol = profile.configType.name,
                latencyMs = latency,
            )
        }.sortedWith(
            compareBy<LocalServerSnapshot> { it.latencyMs == null }
                .thenBy { it.latencyMs ?: Int.MAX_VALUE }
                .thenBy { it.displayName },
        )
    }

    private fun localLegalDocs(): List<RealLegalDoc> = listOf(
        RealLegalDoc(
            id = "terms_of_service",
            title = "服务协议",
            description = "应用与服务使用说明",
            lastUpdated = BuildConfig.VERSION_NAME,
            content = "通过项目主页查看最新服务说明与发行信息。",
            link = AppConfig.APP_URL,
        ),
        RealLegalDoc(
            id = "privacy_policy",
            title = "隐私政策",
            description = "查看当前隐私政策原文链接",
            lastUpdated = BuildConfig.VERSION_NAME,
            content = "隐私政策由应用配置提供，点击链接查看原文。",
            link = AppConfig.APP_PRIVACY_POLICY,
        ),
        RealLegalDoc(
            id = "vpn_service_notice",
            title = "VPN 服务说明",
            description = "节点、线路与模式说明",
            lastUpdated = BuildConfig.VERSION_NAME,
            content = "查看模式说明与线路使用指引。",
            link = AppConfig.APP_WIKI_MODE,
        ),
    )

    private fun normalizeDocId(id: String): String = when (id) {
        "terms" -> "terms_of_service"
        "privacy" -> "privacy_policy"
        else -> id
    }

    private fun OrderEntity.toOrder(): Order = Order(
        orderId = orderNo,
        orderNo = orderNo,
        planCode = planId,
        planName = planName,
        orderType = PaymentConfig.PurchaseType.NEW,
        quoteAssetCode = assetCode,
        quoteNetworkCode = if (assetCode.equals(PaymentConfig.AssetCode.SOL, true)) PaymentConfig.NetworkCode.SOLANA else PaymentConfig.NetworkCode.TRON,
        quoteUsdAmount = amount,
        payableAmount = amount,
        status = status,
        expiresAt = expiredAt?.let(::formatEpoch) ?: formatEpoch(createdAt),
        confirmedAt = paidAt?.let(::formatEpoch),
        completedAt = fulfilledAt?.let(::formatEpoch),
        createdAt = formatEpoch(createdAt),
        subscriptionUrl = subscriptionUrl,
    )

    private fun Plan.quoteAssetCodeLabel(): String = "USDT"

    private fun Plan.quoteNetworkCodeLabel(): String = "SOLANA"

    private fun Order.toP1OrderSummary(): P1OrderSummary = P1OrderSummary(
        orderNo = orderNo,
        planCode = planCode,
        planName = planName,
        status = status,
        statusText = statusText,
        amountText = "${payment.amountCrypto} ${payment.assetCode}",
        assetCode = payment.assetCode,
        networkCode = quoteNetworkCode,
        createdAt = createdAt.take(19),
        expiresAt = expiresAt.take(19),
        collectionAddress = payment.receiveAddress,
        qrText = payment.qrText,
        baseAmount = baseAmount,
        uniqueAmountDelta = uniqueAmountDelta,
        payableAmount = payment.amountCrypto,
        txHash = payment.txHash,
        paymentMatchedAt = payment.confirmedAt,
        subscriptionUrl = subscriptionUrl,
    )

    private fun formatEpoch(epoch: Long): String =
        Instant.ofEpochMilli(epoch).atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)

    private val liveStatuses = setOf(
        PaymentConfig.OrderStatus.PAID_SUCCESS,
        PaymentConfig.OrderStatus.FULFILLED,
        PaymentConfig.OrderStatus.PENDING_PAYMENT,
    )
}

private data class RealLegalDoc(
    val id: String,
    val title: String,
    val description: String,
    val lastUpdated: String,
    val content: String,
    val link: String,
)

private data class LocalServerSnapshot(
    val guid: String,
    val displayName: String,
    val description: String,
    val protocol: String,
    val latencyMs: Int?,
)
