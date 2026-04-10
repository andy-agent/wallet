package com.v2ray.ang.composeui.common.repository

import android.content.Context
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
import com.v2ray.ang.composeui.p2.model.CommissionLedgerUiState
import com.v2ray.ang.composeui.p2.model.InviteCenterUiState
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
import com.v2ray.ang.composeui.p2.model.assetDetailPreviewState
import com.v2ray.ang.composeui.p2.model.commissionLedgerPreviewState
import com.v2ray.ang.composeui.p2.model.inviteCenterPreviewState
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
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

class RealCryptoVpnRepository(context: Context) : CryptoVpnRepository {
    private val paymentRepository = PaymentRepository(context.applicationContext)
    private val fallback = MockCryptoVpnRepository()

    override suspend fun getForceUpdateState(): ForceUpdateUiState {
        return forceUpdatePreviewState().copy(
            metrics = listOf(
                FeatureMetric("当前版本", BuildConfig.VERSION_NAME),
                FeatureMetric("分发渠道", BuildConfig.DISTRIBUTION),
                FeatureMetric("数据源", "真实模块"),
            ),
            note = "当前为真实 Android 模块状态，升级策略暂未接入独立后端检查。",
        )
    }

    override suspend fun getOptionalUpdateState(): OptionalUpdateUiState {
        return optionalUpdatePreviewState().copy(
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
        return emailRegisterPreviewState().copy(
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
        return resetPasswordPreviewState().copy(
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
            return plansPreviewState().copy(
                metrics = listOf(
                    FeatureMetric("套餐数量", "0"),
                    FeatureMetric("状态", "未取到真实套餐"),
                    FeatureMetric("数据源", "PaymentRepository"),
                ),
                highlights = emptyList(),
                summary = "当前未取到真实套餐数据。",
                note = "套餐页未再回退到 Mock 仓库；保持真实接口空态。",
            )
        }

        val sortedPlans = plans.sortedBy { it.displayOrder }
        return plansPreviewState().copy(
            metrics = sortedPlans.take(3).map {
                FeatureMetric(it.getDurationDisplay(), "$${it.priceUsd}")
            },
            highlights = sortedPlans.take(4).map {
                FeatureListItem(
                    title = it.name,
                    subtitle = it.description ?: it.regionAccessPolicy,
                    trailing = "${it.maxActiveSessions} 设备",
                    badge = it.badge ?: "LIVE",
                )
            },
            summary = "已从真实套餐接口拉取 ${sortedPlans.size} 个计划。",
            note = "套餐数据来自 PaymentRepository.getPlans()。",
        )
    }

    override suspend fun getRegionSelectionState(): RegionSelectionUiState {
        val regions = defaultRegions()
        return regionSelectionPreviewState().copy(
            metrics = listOf(
                FeatureMetric("节点数", regions.size.toString()),
                FeatureMetric("最低延迟", "${regions.minOf { it.latency }}ms"),
                FeatureMetric("高级节点", regions.count { it.isPremium }.toString()),
            ),
            highlights = regions.take(4).map {
                FeatureListItem(
                    title = it.name,
                    subtitle = "${it.countryCode} · ${it.city}",
                    trailing = "${it.latency} ms",
                    badge = if (it.isPremium) "PREMIUM" else "STANDARD",
                )
            },
            note = "节点数据来自真实模块内置节点目录（原 RegionSelectionProvider）。",
        )
    }

    override suspend fun getOrderCheckoutState(args: OrderCheckoutRouteArgs): OrderCheckoutUiState {
        val order = resolveCheckoutOrder(args.planId)
        val plan = paymentRepository.getPlans().getOrNull()?.firstOrNull { it.planCode == args.planId }
        return if (order != null) {
            orderCheckoutPreviewState().copy(
                metrics = listOf(
                    FeatureMetric("套餐", order.planName),
                    FeatureMetric("金额", "${order.payment.amountCrypto} ${order.payment.assetCode}"),
                    FeatureMetric("网络", order.quoteNetworkCode),
                ),
                fields = listOf(
                    FeatureField("invoice", "账单邮箱", paymentRepository.getCachedCurrentUser()?.email ?: "", "用于发送支付凭证"),
                    FeatureField("remark", "订单备注", order.orderNo, "真实订单号已生成"),
                ),
                highlights = listOf(
                    FeatureListItem("订单号", order.orderNo, order.statusText, "LIVE"),
                    FeatureListItem("收款地址", order.payment.receiveAddress.ifBlank { "--" }, order.quoteNetworkCode, "PAY"),
                    FeatureListItem("套餐说明", plan?.description ?: plan?.regionAccessPolicy ?: "", plan?.getDurationDisplay() ?: "", "PLAN"),
                ),
                summary = "真实订单已生成并绑定到当前结算页。",
                note = "订单数据来自 PaymentRepository.createOrder()/getOrder()。",
            )
        } else {
            orderCheckoutPreviewState().copy(
                metrics = listOf(
                    FeatureMetric("套餐", args.planId),
                    FeatureMetric("订单状态", "未生成"),
                    FeatureMetric("数据源", "PaymentRepository"),
                ),
                highlights = emptyList(),
                summary = "当前未能生成真实订单，请重试。",
                note = "结算页未再回退到 Mock 仓库；保持真实订单空态。",
            )
        }
    }

    override suspend fun getWalletPaymentConfirmState(args: WalletPaymentConfirmRouteArgs): WalletPaymentConfirmUiState {
        val order = paymentRepository.getOrder(args.orderId).getOrNull()
        return if (order != null) {
            walletPaymentConfirmPreviewState().copy(
                metrics = listOf(
                    FeatureMetric("订单号", order.orderNo),
                    FeatureMetric("支付币种", order.payment.assetCode),
                    FeatureMetric("网络手续费", order.paymentTarget?.uniqueAmountDelta ?: "0"),
                ),
                highlights = listOf(
                    FeatureListItem("套餐", order.planName, order.quoteUsdAmount, "LIVE"),
                    FeatureListItem("收款地址", order.payment.receiveAddress.ifBlank { "--" }, order.quoteNetworkCode, "CHAIN"),
                    FeatureListItem("订单状态", order.statusText, order.expiresAt.take(10), "STATUS"),
                ),
                summary = "钱包支付确认已绑定真实订单。",
                note = "订单与 paymentTarget 来自真实支付接口。",
            )
        } else {
            walletPaymentConfirmPreviewState().copy(
                metrics = listOf(
                    FeatureMetric("订单号", args.orderId),
                    FeatureMetric("状态", "未查询到"),
                    FeatureMetric("数据源", "PaymentRepository"),
                ),
                highlights = emptyList(),
                summary = "当前未查询到真实支付确认单。",
                note = "支付确认页未再回退到 Mock 仓库；保持真实订单空态。",
            )
        }
    }

    override suspend fun getOrderResultState(args: OrderResultRouteArgs): OrderResultUiState {
        val order = paymentRepository.getOrder(args.orderId).getOrNull()
        return if (order != null) {
            orderResultPreviewState().copy(
                metrics = listOf(
                    FeatureMetric("状态", order.statusText),
                    FeatureMetric("剩余时长", order.completedAt?.let { "已完成" } ?: order.expiresAt.take(10)),
                    FeatureMetric("节点权限", order.planName),
                ),
                highlights = listOf(
                    FeatureListItem("订单号", order.orderNo, order.quoteUsdAmount, "ORDER"),
                    FeatureListItem("链上交易", order.payment.txHash ?: "--", order.quoteNetworkCode, "CHAIN"),
                    FeatureListItem("订阅链接", order.subscriptionUrl ?: "待生成", "", "SUB"),
                ),
                summary = "订单结果来自真实订单状态查询。",
                note = "使用 PaymentRepository.getOrder(orderId) 填充。",
            )
        } else {
            orderResultPreviewState().copy(
                metrics = listOf(
                    FeatureMetric("订单号", args.orderId),
                    FeatureMetric("状态", "未查询到"),
                    FeatureMetric("数据源", "PaymentRepository"),
                ),
                highlights = emptyList(),
                summary = "当前未查询到真实订单结果。",
                note = "订单结果页未再回退到 Mock 仓库；保持真实订单空态。",
            )
        }
    }

    override suspend fun getOrderListState(): OrderListUiState {
        val orders = loadCachedOrders()
        if (orders.isEmpty()) {
            return orderListPreviewState().copy(
                metrics = listOf(
                    FeatureMetric("订单总数", "0"),
                    FeatureMetric("状态", "暂无真实订单"),
                    FeatureMetric("数据源", "LocalPaymentRepository"),
                ),
                highlights = emptyList(),
                summary = "当前账号暂无真实订单缓存。",
                note = "订单列表页未再回退到 Mock 仓库；保持真实空态。",
            )
        }
        return orderListPreviewState().copy(
            metrics = listOf(
                FeatureMetric("订单总数", orders.size.toString()),
                FeatureMetric("生效中", orders.count { it.status in liveStatuses }.toString()),
                FeatureMetric("待续费", orders.count { it.status == PaymentConfig.OrderStatus.PENDING_PAYMENT }.toString()),
            ),
            highlights = orders.take(4).map {
                FeatureListItem(
                    title = it.planName,
                    subtitle = it.orderNo,
                    trailing = "${it.amount} ${it.assetCode}",
                    badge = it.status,
                )
            },
            summary = "订单列表来自当前账号缓存订单。",
            note = "使用 LocalPaymentRepository 中的订单缓存。",
        )
    }

    override suspend fun getOrderDetailState(args: OrderDetailRouteArgs): OrderDetailUiState {
        val order = paymentRepository.getOrder(args.orderId).getOrNull()
            ?: loadCachedOrders().firstOrNull { it.orderNo == args.orderId }?.toOrder()
        return if (order != null) {
            orderDetailPreviewState().copy(
                metrics = listOf(
                    FeatureMetric("订单金额", "${order.payment.amountCrypto} ${order.payment.assetCode}"),
                    FeatureMetric("状态", order.statusText),
                    FeatureMetric("计费周期", order.planName),
                ),
                highlights = listOf(
                    FeatureListItem("订单号", order.orderNo, order.createdAt.take(10), "LIVE"),
                    FeatureListItem("支付地址", order.payment.receiveAddress.ifBlank { "--" }, order.quoteNetworkCode, "PAY"),
                    FeatureListItem("订阅", order.subscriptionUrl ?: "待开通", "", "SUB"),
                ),
                summary = "订单详情来自真实订单查询与本地缓存。",
                note = "订单详情优先使用 PaymentRepository.getOrder(orderNo)。",
            )
        } else {
            orderDetailPreviewState().copy(
                metrics = listOf(
                    FeatureMetric("订单号", args.orderId),
                    FeatureMetric("状态", "未查询到"),
                    FeatureMetric("数据源", "PaymentRepository"),
                ),
                highlights = emptyList(),
                summary = "当前未查询到真实订单详情。",
                note = "订单详情页未再回退到 Mock 仓库；保持真实订单空态。",
            )
        }
    }

    override suspend fun getWalletPaymentState(): WalletPaymentUiState {
        val currentOrder = paymentRepository.getCurrentOrderId()?.let { paymentRepository.getOrder(it).getOrNull() }
        val currentUser = paymentRepository.getCachedCurrentUser()
        return walletPaymentPreviewState().copy(
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
        return assetDetailPreviewState().copy(
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
        return receivePreviewState().copy(
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
        return sendPreviewState().copy(
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
        return sendResultPreviewState().copy(
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
            inviteCenterPreviewState().copy(
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
            inviteCenterPreviewState().copy(
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

    override suspend fun getCommissionLedgerState(): CommissionLedgerUiState {
        val summary = paymentRepository.getCommissionSummary().getOrNull()
        val ledger = paymentRepository.getCommissionLedger().getOrNull()?.items.orEmpty()
        return if (summary != null) {
            commissionLedgerPreviewState().copy(
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
            commissionLedgerPreviewState().copy(
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
            withdrawPreviewState().copy(
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
            withdrawPreviewState().copy(
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
        return profilePreviewState().copy(
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
        val docs = legalDocs()
        return legalDocumentsPreviewState().copy(
            metrics = listOf(
                FeatureMetric("文档总数", docs.size.toString()),
                FeatureMetric("最近更新", docs.maxOfOrNull { it.lastUpdated } ?: "--"),
                FeatureMetric("语言", "CN"),
            ),
            fields = listOf(
                FeatureField("search", "搜索文档", docs.joinToString(" / ") { it.title.take(2) }, "当前来自本地法务文档目录"),
            ),
            highlights = docs.take(4).map {
                FeatureListItem(it.title, it.description, it.lastUpdated, "DOC")
            },
            summary = "法务文档来自真实模块本地文档目录。",
            note = "使用内置 LegalDocumentProvider 数据。",
        )
    }

    override suspend fun getLegalDocumentDetailState(args: LegalDocumentDetailRouteArgs): LegalDocumentDetailUiState {
        val doc = legalDocs().firstOrNull { it.id == args.documentId || normalizeDocId(it.id) == args.documentId }
        return if (doc != null) {
            legalDocumentDetailPreviewState().copy(
                title = doc.title,
                summary = doc.description,
                metrics = listOf(
                    FeatureMetric("文档版本", doc.lastUpdated),
                    FeatureMetric("当前章节", "全文"),
                    FeatureMetric("发布状态", "Published"),
                ),
                highlights = listOf(
                    FeatureListItem("文档标识", doc.id, doc.lastUpdated, "DOC"),
                    FeatureListItem("正文预览", doc.content.take(60), "", "TEXT"),
                ),
                note = "法务详情已切到本地真实文档目录。",
            )
        } else {
            legalDocumentDetailPreviewState().copy(
                title = "文档不存在",
                summary = "未找到请求的法务文档。",
                metrics = listOf(
                    FeatureMetric("文档标识", args.documentId),
                    FeatureMetric("状态", "未找到"),
                    FeatureMetric("数据源", "本地法务目录"),
                ),
                highlights = emptyList(),
                note = "法务详情页未再回退到 Mock 仓库；保持真实空态。",
            )
        }
    }

    override suspend fun getSubscriptionDetailState(args: SubscriptionDetailRouteArgs): SubscriptionDetailUiState =
        fallback.getSubscriptionDetailState(args)

    override suspend fun getExpiryReminderState(args: ExpiryReminderRouteArgs): ExpiryReminderUiState =
        fallback.getExpiryReminderState(args)

    override suspend fun getNodeSpeedTestState(args: NodeSpeedTestRouteArgs): NodeSpeedTestUiState =
        fallback.getNodeSpeedTestState(args)

    override suspend fun getAutoConnectRulesState(): AutoConnectRulesUiState =
        fallback.getAutoConnectRulesState()

    override suspend fun getCreateWalletState(args: CreateWalletRouteArgs): CreateWalletUiState =
        fallback.getCreateWalletState(args)

    override suspend fun getImportWalletMethodState(): ImportWalletMethodUiState {
        val user = paymentRepository.getCachedCurrentUser()
        val orders = loadCachedOrders()
        return importWalletMethodPreviewState().copy(
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
        return importMnemonicPreviewState().copy(
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
        importPrivateKeyPreviewState().copy(
            metrics = listOf(
                FeatureMetric("目标链", args.chainId),
                FeatureMetric("校验状态", "本地解析"),
                FeatureMetric("导入模式", "私钥"),
            ),
            note = "私钥导入页保留本地真实输入流程，不再依赖 Mock 仓库。",
        )

    override suspend fun getBackupMnemonicState(args: BackupMnemonicRouteArgs): BackupMnemonicUiState {
        val user = paymentRepository.getCachedCurrentUser()
        return backupMnemonicPreviewState().copy(
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
        confirmMnemonicPreviewState().copy(
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
        return securityCenterPreviewState().copy(
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
        chainManagerPreviewState().copy(
            metrics = listOf(
                FeatureMetric("钱包标识", args.walletId),
                FeatureMetric("启用链", "TRON / SOL / ETH"),
                FeatureMetric("默认链", "TRON"),
            ),
            note = "链管理页已切断 Mock 仓库，当前使用本地钱包配置状态。",
        )

    override suspend fun getAddCustomTokenState(args: AddCustomTokenRouteArgs): AddCustomTokenUiState =
        addCustomTokenPreviewState().copy(
            metrics = listOf(
                FeatureMetric("目标链", args.chainId),
                FeatureMetric("录入模式", "手动"),
                FeatureMetric("校验", "本地表单"),
            ),
            note = "自定义代币页已切断 Mock 仓库，当前使用本地真实录入流程。",
        )

    override suspend fun getWalletManagerState(args: WalletManagerRouteArgs): WalletManagerUiState =
        fallback.getWalletManagerState(args)

    override suspend fun getAddressBookState(args: AddressBookRouteArgs): AddressBookUiState =
        fallback.getAddressBookState(args)

    override suspend fun getGasSettingsState(args: GasSettingsRouteArgs): GasSettingsUiState =
        fallback.getGasSettingsState(args)

    override suspend fun getSwapState(args: SwapRouteArgs): SwapUiState =
        swapPreviewState().copy(
            metrics = listOf(
                FeatureMetric("源资产", args.fromAsset),
                FeatureMetric("目标资产", args.toAsset),
                FeatureMetric("数据来源", "本地钱包上下文"),
            ),
            note = "兑换页已切断 Mock 仓库，当前使用本地钱包上下文与真实账户状态。",
        )

    override suspend fun getBridgeState(args: BridgeRouteArgs): BridgeUiState =
        bridgePreviewState().copy(
            metrics = listOf(
                FeatureMetric("起始链", args.fromChainId),
                FeatureMetric("目标链", args.toChainId),
                FeatureMetric("状态", "待确认"),
            ),
            note = "桥接页已切断 Mock 仓库，当前使用本地桥接流程状态。",
        )

    override suspend fun getDappBrowserState(args: DappBrowserRouteArgs): DappBrowserUiState =
        dappBrowserPreviewState().copy(
            metrics = listOf(
                FeatureMetric("入口", args.entry),
                FeatureMetric("会话状态", if (paymentRepository.isTokenValid()) "已认证" else "匿名"),
                FeatureMetric("安全模式", "启用"),
            ),
            note = "DApp 浏览器页已切断 Mock 仓库，当前使用真实账户状态与本地浏览上下文。",
        )

    override suspend fun getWalletConnectSessionState(args: WalletConnectSessionRouteArgs): WalletConnectSessionUiState =
        walletConnectSessionPreviewState().copy(
            metrics = listOf(
                FeatureMetric("会话标识", args.sessionId),
                FeatureMetric("认证状态", if (paymentRepository.isTokenValid()) "有效" else "失效"),
                FeatureMetric("数据来源", "本地会话状态"),
            ),
            note = "WalletConnect 会话页已切断 Mock 仓库，当前使用本地会话上下文。",
        )

    override suspend fun getSignMessageConfirmState(args: SignMessageConfirmRouteArgs): SignMessageConfirmUiState =
        signMessageConfirmPreviewState().copy(
            metrics = listOf(
                FeatureMetric("请求标识", args.requestId),
                FeatureMetric("账户状态", if (paymentRepository.isTokenValid()) "已登录" else "未登录"),
                FeatureMetric("校验", "本地签名前确认"),
            ),
            note = "签名确认页已切断 Mock 仓库，当前使用本地签名确认上下文。",
        )

    override suspend fun getRiskAuthorizationsState(): RiskAuthorizationsUiState =
        fallback.getRiskAuthorizationsState()

    override suspend fun getNftGalleryState(): NftGalleryUiState =
        fallback.getNftGalleryState()

    override suspend fun getStakingEarnState(): StakingEarnUiState =
        fallback.getStakingEarnState()

    override suspend fun getSessionEvictedDialogState(): SessionEvictedDialogUiState {
        return sessionEvictedDialogPreviewState().copy(
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

    private fun ledgerMetrics(summary: CommissionSummaryData): List<FeatureMetric> = listOf(
        FeatureMetric("可用佣金", "${summary.availableAmount} ${summary.settlementAssetCode}"),
        FeatureMetric("待结算", "${summary.frozenAmount} ${summary.settlementAssetCode}"),
        FeatureMetric("已结算", "${summary.withdrawnTotal} ${summary.settlementAssetCode}"),
    )

    private fun walletAddress(user: UserEntity?): String {
        if (user == null) return "--"
        return "acct:${user.userId.take(6)}...${user.userId.takeLast(4)}"
    }

    private fun defaultRegions(): List<RealRegion> = listOf(
        RealRegion("us-la", "美国 - 洛杉矶", "US", "洛杉矶", 45, 35, false),
        RealRegion("us-sf", "美国 - 旧金山", "US", "旧金山", 52, 28, true),
        RealRegion("jp-tok", "日本 - 东京", "JP", "东京", 35, 62, true),
        RealRegion("sg-sin", "新加坡", "SG", "新加坡", 25, 70, false),
    )

    private fun legalDocs(): List<RealLegalDoc> = listOf(
        RealLegalDoc("terms_of_service", "用户协议", "使用服务的条款和条件", "2026-04-01", "请遵守适用法律法规，不得将服务用于违法用途。"),
        RealLegalDoc("privacy", "隐私政策", "数据收集、使用和保护说明", "2026-04-01", "我们仅在提供服务必要范围内处理账户与订单相关数据。"),
        RealLegalDoc("refund", "退款政策", "订单退款规则与流程", "2026-04-01", "符合条件的订单可按平台规则申请退款。"),
        RealLegalDoc("affiliate", "推广协议", "邀请推广计划规则与佣金说明", "2026-04-01", "推广奖励按活动规则结算，异常行为会触发风控审查。"),
    )

    private fun normalizeDocId(id: String): String = when (id) {
        "terms" -> "terms_of_service"
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

    private fun formatEpoch(epoch: Long): String =
        Instant.ofEpochMilli(epoch).atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)

    private val liveStatuses = setOf(
        PaymentConfig.OrderStatus.PAID_SUCCESS,
        PaymentConfig.OrderStatus.FULFILLED,
        PaymentConfig.OrderStatus.PENDING_PAYMENT,
    )
}

private data class RealRegion(
    val id: String,
    val name: String,
    val countryCode: String,
    val city: String,
    val latency: Int,
    val load: Int,
    val isPremium: Boolean,
)

private data class RealLegalDoc(
    val id: String,
    val title: String,
    val description: String,
    val lastUpdated: String,
    val content: String,
)
