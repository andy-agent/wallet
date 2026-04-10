(function () {
  const routes = [
    { id: "p0/splash", title: "启动页", copy: "CryptoVPN 启动与连接准备。", page: "./pages/p0/splash.html" },
    { id: "p0/email_login", title: "登录页", copy: "邮箱登录与账户同步。", page: "./pages/p0/email_login.html" },
    { id: "p0/wallet_onboarding", title: "钱包引导", copy: "配置你的多链钱包。", page: "./pages/p0/wallet_onboarding.html" },
    { id: "p0/vpn_home", title: "VPN核心", copy: "节点健康、延迟、套餐状态总览。", page: "./pages/p0/vpn_home.html" },
    { id: "p0/wallet_home", title: "钱包总览", copy: "多链钱包资产列表。", page: "./pages/p0/wallet_home.html" },
    { id: "p0/force_update", title: "强制更新", copy: "系统更新强制拦截页。", page: "./pages/p0/force_update.html" },
    { id: "p0/optional_update", title: "发现新版本", copy: "可稍后更新的系统提示。", page: "./pages/p0/optional_update.html" },
    { id: "p0/email_register", title: "创建你的账户", copy: "邮箱注册。", page: "./pages/p0/email_register.html" },
    { id: "p0/reset_password", title: "重置密码", copy: "验证码找回。", page: "./pages/p0/reset_password.html" },

    { id: "p1/plans", title: "购买你的套餐", copy: "套餐选择与支付入口。", page: "./pages/p1/plans.html" },
    { id: "p1/region_selection", title: "选择最佳节点", copy: "智能路由与区域选择。", page: "./pages/p1/region_selection.html" },
    { id: "p1/order_checkout", title: "订单收银台", copy: "确认套餐与支付网络。", page: "./pages/p1/order_checkout.html" },
    { id: "p1/wallet_payment_confirm", title: "钱包支付确认", copy: "订单摘要与支付确认。", page: "./pages/p1/wallet_payment_confirm.html" },
    { id: "p1/order_result", title: "订单已生效", copy: "支付成功与开通结果。", page: "./pages/p1/order_result.html" },
    { id: "p1/order_list", title: "订单中心", copy: "历史订单与状态筛选。", page: "./pages/p1/order_list.html" },
    { id: "p1/order_detail", title: "订单详情", copy: "支付与激活时间线。", page: "./pages/p1/order_detail.html" },

    { id: "p2-core/asset_detail", title: "资产详情", copy: "USDT 资产走势与交易明细。", page: "./pages/p2-core/asset_detail.html" },
    { id: "p2-core/receive", title: "收款", copy: "二维码与地址分享。", page: "./pages/p2-core/receive.html" },
    { id: "p2-core/send", title: "发送资产", copy: "地址、链和安全检查。", page: "./pages/p2-core/send.html" },
    { id: "p2-core/send_result", title: "发送完成", copy: "转账广播结果。", page: "./pages/p2-core/send_result.html" },
    { id: "p2-core/invite_center", title: "邀请中心", copy: "邀请码与增长数据。", page: "./pages/p2-core/invite_center.html" },
    { id: "p2-core/commission_ledger", title: "佣金账本", copy: "收益趋势与结算记录。", page: "./pages/p2-core/commission_ledger.html" },
    { id: "p2-core/withdraw", title: "提现佣金", copy: "结算收益提现。", page: "./pages/p2-core/withdraw.html" },
    { id: "p2-core/profile", title: "我的", copy: "账户、安全、法务入口。", page: "./pages/p2-core/profile.html" },
    { id: "p2-core/legal_documents", title: "法务文档", copy: "协议、隐私与免责声明。", page: "./pages/p2-core/legal_documents.html" },
    { id: "p2-core/legal_document_detail", title: "服务协议", copy: "法务正文详情。", page: "./pages/p2-core/legal_document_detail.html" },

    { id: "p2-extended/import_wallet_method", title: "导入多链钱包", copy: "钱包导入方式。", page: "./pages/p2-extended/import_wallet_method.html" },
    { id: "p2-extended/import_mnemonic", title: "输入助记词", copy: "本地解析助记词。", page: "./pages/p2-extended/import_mnemonic.html" },
    { id: "p2-extended/backup_mnemonic", title: "备份助记词", copy: "离线备份与导出模板。", page: "./pages/p2-extended/backup_mnemonic.html" },
    { id: "p2-extended/confirm_mnemonic", title: "确认助记词", copy: "抽查验证备份。", page: "./pages/p2-extended/confirm_mnemonic.html" },
    { id: "p2-extended/security_center", title: "安全中心", copy: "助记词、设备与授权。", page: "./pages/p2-extended/security_center.html" },
    { id: "p2-extended/chain_manager", title: "链管理", copy: "网络启用与排序。", page: "./pages/p2-extended/chain_manager.html" },
    { id: "p2-extended/add_custom_token", title: "添加自定义代币", copy: "手动录入代币。", page: "./pages/p2-extended/add_custom_token.html" },
    { id: "p2-extended/swap", title: "币币兑换", copy: "同链快速兑换。", page: "./pages/p2-extended/swap.html" },
    { id: "p2-extended/bridge", title: "跨链桥接", copy: "跨链迁移与追踪。", page: "./pages/p2-extended/bridge.html" },
    { id: "p2-extended/dapp_browser", title: "DApp 浏览器", copy: "链上应用入口。", page: "./pages/p2-extended/dapp_browser.html" },
    { id: "p2-extended/wallet_connect_session", title: "连接会话", copy: "WalletConnect 会话管理。", page: "./pages/p2-extended/wallet_connect_session.html" },
    { id: "p2-extended/sign_message_confirm", title: "签名确认", copy: "DApp 最终签名确认。", page: "./pages/p2-extended/sign_message_confirm.html" }
  ];

  const phases = [
    { label: "P0", routes: routes.filter((route) => route.id.startsWith("p0/")).map((route) => route.id) },
    { label: "P1", routes: routes.filter((route) => route.id.startsWith("p1/")).map((route) => route.id) },
    { label: "P2 Core", routes: routes.filter((route) => route.id.startsWith("p2-core/")).map((route) => route.id) },
    { label: "P2 Extended", routes: routes.filter((route) => route.id.startsWith("p2-extended/")).map((route) => route.id) }
  ];

  const byId = Object.fromEntries(routes.map((route) => [route.id, route]));
  const byPage = Object.fromEntries(routes.map((route) => {
    const key = route.page.replace(/^\.\//, "/");
    return [key, route.id];
  }));

  window.CryptoVpnRouteCatalog = {
    defaultRoute: "p0/splash",
    routes,
    phases,
    byId,
    byPage
  };
})();
