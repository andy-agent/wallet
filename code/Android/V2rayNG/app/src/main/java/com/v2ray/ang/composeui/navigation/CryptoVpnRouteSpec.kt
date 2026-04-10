package com.v2ray.ang.composeui.navigation


data class RouteParamInfo(
    val key: String,
    val sample: String,
    val description: String,
)

data class RouteDefinition(
    val name: String,
    val pattern: String,
    val params: List<RouteParamInfo>,
)

object CryptoVpnRouteSpec {
    val splash = RouteDefinition(
        name = "splash",
        pattern = "splash",
        params = emptyList(),
    )

    val emailLogin = RouteDefinition(
        name = "email_login",
        pattern = "email_login",
        params = emptyList(),
    )

    val walletOnboarding = RouteDefinition(
        name = "wallet_onboarding",
        pattern = "wallet_onboarding",
        params = emptyList(),
    )

    val vpnHome = RouteDefinition(
        name = "vpn_home",
        pattern = "vpn_home",
        params = emptyList(),
    )

    val walletHome = RouteDefinition(
        name = "wallet_home",
        pattern = "wallet_home",
        params = emptyList(),
    )

    val forceUpdate = RouteDefinition(
        name = "force_update",
        pattern = "force_update",
        params = emptyList(),
    )

    val optionalUpdate = RouteDefinition(
        name = "optional_update",
        pattern = "optional_update",
        params = emptyList(),
    )

    val emailRegister = RouteDefinition(
        name = "email_register",
        pattern = "email_register",
        params = emptyList(),
    )

    val resetPassword = RouteDefinition(
        name = "reset_password",
        pattern = "reset_password",
        params = emptyList(),
    )

    val plans = RouteDefinition(
        name = "plans",
        pattern = "plans",
        params = emptyList(),
    )

    val regionSelection = RouteDefinition(
        name = "region_selection",
        pattern = "region_selection",
        params = emptyList(),
    )

    val orderCheckout = RouteDefinition(
        name = "order_checkout",
        pattern = "order_checkout/{planId}",
        params = listOf(
        RouteParamInfo(key = "planId", sample = "annual_pro", description = "套餐标识，用于读取当前结算单"),
    ),
    )

    val walletPaymentConfirm = RouteDefinition(
        name = "wallet_payment_confirm",
        pattern = "wallet_payment_confirm/{orderId}",
        params = listOf(
        RouteParamInfo(key = "orderId", sample = "ORD-2025-0001", description = "订单标识，用于读取支付确认信息"),
    ),
    )

    val orderResult = RouteDefinition(
        name = "order_result",
        pattern = "order_result/{orderId}",
        params = listOf(
        RouteParamInfo(key = "orderId", sample = "ORD-2025-0001", description = "已完成订单的唯一标识"),
    ),
    )

    val orderList = RouteDefinition(
        name = "order_list",
        pattern = "order_list",
        params = emptyList(),
    )

    val orderDetail = RouteDefinition(
        name = "order_detail",
        pattern = "order_detail/{orderId}",
        params = listOf(
        RouteParamInfo(key = "orderId", sample = "ORD-2025-0001", description = "待查看的订单标识"),
    ),
    )

    val walletPayment = RouteDefinition(
        name = "wallet_payment",
        pattern = "wallet_payment",
        params = emptyList(),
    )

    val assetDetail = RouteDefinition(
        name = "asset_detail",
        pattern = "asset_detail/{assetId}/{chainId}",
        params = listOf(
        RouteParamInfo(key = "assetId", sample = "USDT", description = "资产标识，例如 USDT / ETH"),
        RouteParamInfo(key = "chainId", sample = "tron", description = "链标识，例如 tron / ethereum"),
    ),
    )

    val receive = RouteDefinition(
        name = "receive",
        pattern = "receive/{assetId}/{chainId}",
        params = listOf(
        RouteParamInfo(key = "assetId", sample = "USDT", description = "收款资产标识"),
        RouteParamInfo(key = "chainId", sample = "tron", description = "当前收款网络"),
    ),
    )

    val send = RouteDefinition(
        name = "send",
        pattern = "send/{assetId}/{chainId}",
        params = listOf(
        RouteParamInfo(key = "assetId", sample = "USDT", description = "发送资产标识"),
        RouteParamInfo(key = "chainId", sample = "tron", description = "发送所处网络"),
    ),
    )

    val sendResult = RouteDefinition(
        name = "send_result",
        pattern = "send_result/{txId}",
        params = listOf(
        RouteParamInfo(key = "txId", sample = "TX-9F32", description = "链上交易标识"),
    ),
    )

    val inviteCenter = RouteDefinition(
        name = "invite_center",
        pattern = "invite_center",
        params = emptyList(),
    )

    val commissionLedger = RouteDefinition(
        name = "commission_ledger",
        pattern = "commission_ledger",
        params = emptyList(),
    )

    val withdraw = RouteDefinition(
        name = "withdraw",
        pattern = "withdraw",
        params = emptyList(),
    )

    val profile = RouteDefinition(
        name = "profile",
        pattern = "profile",
        params = emptyList(),
    )

    val legalDocuments = RouteDefinition(
        name = "legal_documents",
        pattern = "legal_documents",
        params = emptyList(),
    )

    val legalDocumentDetail = RouteDefinition(
        name = "legal_document_detail",
        pattern = "legal_document_detail/{documentId}",
        params = listOf(
        RouteParamInfo(key = "documentId", sample = "terms_of_service", description = "法务文档标识，例如服务协议或隐私政策"),
    ),
    )

    val subscriptionDetail = RouteDefinition(
        name = "subscription_detail",
        pattern = "subscription_detail/{subscriptionId}",
        params = listOf(
        RouteParamInfo(key = "subscriptionId", sample = "pro_mesh_30d", description = "订阅标识，用于读取计划详情"),
    ),
    )

    val expiryReminder = RouteDefinition(
        name = "expiry_reminder",
        pattern = "expiry_reminder/{daysLeft}",
        params = listOf(
        RouteParamInfo(key = "daysLeft", sample = "5", description = "剩余到期天数"),
    ),
    )

    val nodeSpeedTest = RouteDefinition(
        name = "node_speed_test",
        pattern = "node_speed_test/{nodeGroupId}",
        params = listOf(
        RouteParamInfo(key = "nodeGroupId", sample = "premium_apac", description = "节点分组标识，用于读取测速结果"),
    ),
    )

    val autoConnectRules = RouteDefinition(
        name = "auto_connect_rules",
        pattern = "auto_connect_rules",
        params = emptyList(),
    )

    val createWallet = RouteDefinition(
        name = "create_wallet",
        pattern = "create_wallet/{mode}",
        params = listOf(
        RouteParamInfo(key = "mode", sample = "create", description = "创建模式，可用于区分冷/热钱包初始化"),
    ),
    )

    val importWalletMethod = RouteDefinition(
        name = "import_wallet_method",
        pattern = "import_wallet_method",
        params = emptyList(),
    )

    val importMnemonic = RouteDefinition(
        name = "import_mnemonic",
        pattern = "import_mnemonic/{source}",
        params = listOf(
        RouteParamInfo(key = "source", sample = "onboarding", description = "导入来源，例如 onboarding / settings"),
    ),
    )

    val importPrivateKey = RouteDefinition(
        name = "import_private_key",
        pattern = "import_private_key/{chainId}",
        params = listOf(
        RouteParamInfo(key = "chainId", sample = "ethereum", description = "私钥对应的目标链"),
    ),
    )

    val backupMnemonic = RouteDefinition(
        name = "backup_mnemonic",
        pattern = "backup_mnemonic/{walletId}",
        params = listOf(
        RouteParamInfo(key = "walletId", sample = "primary_wallet", description = "待备份的钱包标识"),
    ),
    )

    val confirmMnemonic = RouteDefinition(
        name = "confirm_mnemonic",
        pattern = "confirm_mnemonic/{walletId}",
        params = listOf(
        RouteParamInfo(key = "walletId", sample = "primary_wallet", description = "待确认的钱包标识"),
    ),
    )

    val securityCenter = RouteDefinition(
        name = "security_center",
        pattern = "security_center",
        params = emptyList(),
    )

    val chainManager = RouteDefinition(
        name = "chain_manager",
        pattern = "chain_manager/{walletId}",
        params = listOf(
        RouteParamInfo(key = "walletId", sample = "primary_wallet", description = "当前钱包标识，用于读取启用链列表"),
    ),
    )

    val addCustomToken = RouteDefinition(
        name = "add_custom_token",
        pattern = "add_custom_token/{chainId}",
        params = listOf(
        RouteParamInfo(key = "chainId", sample = "base", description = "将要添加代币的目标链"),
    ),
    )

    val walletManager = RouteDefinition(
        name = "wallet_manager",
        pattern = "wallet_manager/{walletId}",
        params = listOf(
        RouteParamInfo(key = "walletId", sample = "primary_wallet", description = "默认选中的钱包标识"),
    ),
    )

    val addressBook = RouteDefinition(
        name = "address_book",
        pattern = "address_book/{mode}",
        params = listOf(
        RouteParamInfo(key = "mode", sample = "send", description = "地址簿进入模式，例如 send / receive / select"),
    ),
    )

    val gasSettings = RouteDefinition(
        name = "gas_settings",
        pattern = "gas_settings/{chainId}",
        params = listOf(
        RouteParamInfo(key = "chainId", sample = "ethereum", description = "当前 gas 参数生效的目标链"),
    ),
    )

    val swap = RouteDefinition(
        name = "swap",
        pattern = "swap/{fromAsset}/{toAsset}",
        params = listOf(
        RouteParamInfo(key = "fromAsset", sample = "USDT", description = "源资产标识"),
        RouteParamInfo(key = "toAsset", sample = "SOL", description = "目标资产标识"),
    ),
    )

    val bridge = RouteDefinition(
        name = "bridge",
        pattern = "bridge/{fromChainId}/{toChainId}",
        params = listOf(
        RouteParamInfo(key = "fromChainId", sample = "tron", description = "桥接起始链"),
        RouteParamInfo(key = "toChainId", sample = "solana", description = "桥接目标链"),
    ),
    )

    val dappBrowser = RouteDefinition(
        name = "dapp_browser",
        pattern = "dapp_browser/{entry}",
        params = listOf(
        RouteParamInfo(key = "entry", sample = "jup.ag", description = "DApp 浏览器初始入口或域名"),
    ),
    )

    val walletConnectSession = RouteDefinition(
        name = "wallet_connect_session",
        pattern = "wallet_connect_session/{sessionId}",
        params = listOf(
        RouteParamInfo(key = "sessionId", sample = "session_jupiter", description = "WalletConnect 会话标识"),
    ),
    )

    val signMessageConfirm = RouteDefinition(
        name = "sign_message_confirm",
        pattern = "sign_message_confirm/{requestId}",
        params = listOf(
        RouteParamInfo(key = "requestId", sample = "req_001", description = "待签名请求的唯一标识"),
    ),
    )

    val riskAuthorizations = RouteDefinition(
        name = "risk_authorizations",
        pattern = "risk_authorizations",
        params = emptyList(),
    )

    val nftGallery = RouteDefinition(
        name = "nft_gallery",
        pattern = "nft_gallery",
        params = emptyList(),
    )

    val stakingEarn = RouteDefinition(
        name = "staking_earn",
        pattern = "staking_earn",
        params = emptyList(),
    )

    fun orderCheckoutRoute(planId: String): String = "order_checkout" + "/" + planId
    fun walletPaymentConfirmRoute(orderId: String): String = "wallet_payment_confirm" + "/" + orderId
    fun orderResultRoute(orderId: String): String = "order_result" + "/" + orderId
    fun orderDetailRoute(orderId: String): String = "order_detail" + "/" + orderId
    fun assetDetailRoute(assetId: String, chainId: String): String = "asset_detail" + "/" + assetId + "/" + chainId
    fun receiveRoute(assetId: String, chainId: String): String = "receive" + "/" + assetId + "/" + chainId
    fun sendRoute(assetId: String, chainId: String): String = "send" + "/" + assetId + "/" + chainId
    fun sendResultRoute(txId: String): String = "send_result" + "/" + txId
    fun legalDocumentDetailRoute(documentId: String): String = "legal_document_detail" + "/" + documentId
    fun subscriptionDetailRoute(subscriptionId: String): String = "subscription_detail" + "/" + subscriptionId
    fun expiryReminderRoute(daysLeft: String): String = "expiry_reminder" + "/" + daysLeft
    fun nodeSpeedTestRoute(nodeGroupId: String): String = "node_speed_test" + "/" + nodeGroupId
    fun createWalletRoute(mode: String): String = "create_wallet" + "/" + mode
    fun importMnemonicRoute(source: String): String = "import_mnemonic" + "/" + source
    fun importPrivateKeyRoute(chainId: String): String = "import_private_key" + "/" + chainId
    fun backupMnemonicRoute(walletId: String): String = "backup_mnemonic" + "/" + walletId
    fun confirmMnemonicRoute(walletId: String): String = "confirm_mnemonic" + "/" + walletId
    fun chainManagerRoute(walletId: String): String = "chain_manager" + "/" + walletId
    fun addCustomTokenRoute(chainId: String): String = "add_custom_token" + "/" + chainId
    fun walletManagerRoute(walletId: String): String = "wallet_manager" + "/" + walletId
    fun addressBookRoute(mode: String): String = "address_book" + "/" + mode
    fun gasSettingsRoute(chainId: String): String = "gas_settings" + "/" + chainId
    fun swapRoute(fromAsset: String, toAsset: String): String = "swap" + "/" + fromAsset + "/" + toAsset
    fun bridgeRoute(fromChainId: String, toChainId: String): String = "bridge" + "/" + fromChainId + "/" + toChainId
    fun dappBrowserRoute(entry: String): String = "dapp_browser" + "/" + entry
    fun walletConnectSessionRoute(sessionId: String): String = "wallet_connect_session" + "/" + sessionId
    fun signMessageConfirmRoute(requestId: String): String = "sign_message_confirm" + "/" + requestId

    val allRoutes: List<RouteDefinition> = listOf(
        splash,
        emailLogin,
        walletOnboarding,
        vpnHome,
        walletHome,
        forceUpdate,
        optionalUpdate,
        emailRegister,
        resetPassword,
        plans,
        regionSelection,
        orderCheckout,
        walletPaymentConfirm,
        orderResult,
        orderList,
        orderDetail,
        walletPayment,
        assetDetail,
        receive,
        send,
        sendResult,
        inviteCenter,
        commissionLedger,
        withdraw,
        profile,
        legalDocuments,
        legalDocumentDetail,
        subscriptionDetail,
        expiryReminder,
        nodeSpeedTest,
        autoConnectRules,
        createWallet,
        importWalletMethod,
        importMnemonic,
        importPrivateKey,
        backupMnemonic,
        confirmMnemonic,
        securityCenter,
        chainManager,
        addCustomToken,
        walletManager,
        addressBook,
        gasSettings,
        swap,
        bridge,
        dappBrowser,
        walletConnectSession,
        signMessageConfirm,
        riskAuthorizations,
        nftGallery,
        stakingEarn,
    )
}
