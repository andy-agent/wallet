# Wallet Create/Import Route Inventory

更新日期：2026-04-15

## Scope

本清单覆盖 Android 钱包创建/导入/备份/确认链路，以及当前后端钱包生命周期真状态的接入边界。

## Route Flow

1. `walletOnboarding`
   - 文件：
     - `/Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/pages/p0/WalletOnboardingPage.kt`
     - `/Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/p0/repository/RealP0Repository.kt`
     - `/Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/navigation/P0NavGraph.kt`
   - 当前状态：
     - 已接真实账户 + 订阅 + `wallet/lifecycle`
     - 已根据 `walletExists` 分流：
       - 已有钱包 -> `walletHome`
       - 无钱包且选创建 -> `create_wallet`
       - 无钱包且选导入 -> `import_wallet_method`
   - 结论：`REAL`

2. `create_wallet/{mode}`
   - 文件：
     - `/Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/pages/p2extended/CreateWalletPage.kt`
     - `/Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/common/repository/RealCryptoVpnRepository.kt`
     - `/Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/p2extended/viewmodel/CreateWalletViewModel.kt`
   - 当前状态：
     - 页面字段不再使用 mock 钱包名
     - 主按钮调用后端 `wallet/lifecycle` `CREATE`
     - 创建成功后进入 `backup_mnemonic/{walletId}`
   - 结论：`REAL (minimal lifecycle)`

3. `import_wallet_method`
   - 文件：
     - `/Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/pages/p2extended/ImportWalletMethodPage.kt`
     - `/Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/common/repository/RealCryptoVpnRepository.kt`
   - 当前状态：
     - 已切断 Mock repository
     - 显示真实账户上下文和本地订单数量
     - 主按钮进入 `import_mnemonic/onboarding`
   - 结论：`REAL`

4. `import_mnemonic/{source}`
   - 文件：
     - `/Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/pages/p2extended/ImportMnemonicPage.kt`
     - `/Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/common/repository/RealCryptoVpnRepository.kt`
   - 当前状态：
     - 助记词输入框已去掉示例 mock 内容
     - 当前只做最小真实校验：
       - 本地词数校验（12/24）
       - 后端 `wallet/lifecycle` `IMPORT`
     - 成功后回 `walletHome`
   - 结论：`PARTIAL REAL`
   - 后续缺口：
     - 未接真实助记词解析/派生/持久化

5. `backup_mnemonic/{walletId}`
   - 文件：
     - `/Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/pages/p2extended/BackupMnemonicPage.kt`
     - `/Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/common/repository/RealCryptoVpnRepository.kt`
   - 当前状态：
     - 不再显示假助记词
     - 主按钮调用后端 `wallet/lifecycle` `ACKNOWLEDGE_BACKUP`
     - 成功后进入 `confirm_mnemonic/{walletId}`
   - 结论：`REAL (minimal lifecycle)`

6. `confirm_mnemonic/{walletId}`
   - 文件：
     - `/Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/pages/p2extended/ConfirmMnemonicPage.kt`
     - `/Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/common/repository/RealCryptoVpnRepository.kt`
   - 当前状态：
     - 主按钮调用后端 `wallet/lifecycle` `CONFIRM_BACKUP`
     - 成功后进入 `walletHome`
   - 结论：`REAL (minimal lifecycle)`

7. `receive/{assetId}/{chainId}`
   - 文件：
     - `/Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/pages/p2/ReceivePage.kt`
     - `/Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/common/repository/RealCryptoVpnRepository.kt`
   - 当前状态：
     - 进入收款页前先看后端 `wallet/lifecycle`
     - `NO_WALLET` -> 自动跳 `walletOnboarding`
     - `NO_ADDRESS/READY` -> 保持收款页
   - 结论：`REAL`

## Typography Status

- 以上页面均运行在当前自适应 Typography token 体系下
- 当前没有单页级 `fontScale` 对抗逻辑
- 当前未发现这些路由页继续散落新的硬编码 mock 字号

## Current Backend Contract Used By Android

- `GET /api/client/v1/wallet/lifecycle`
- `POST /api/client/v1/wallet/lifecycle`
- `GET /api/client/v1/wallet/receive-context`

关键字段：

- `walletExists`
- `receiveState`
  - `NO_WALLET`
  - `NO_ADDRESS`
  - `READY`
- `lifecycleStatus`
  - `NOT_CREATED`
  - `CREATED`
  - `IMPORTED`
  - `ACTIVE`
- `status`
  - 细粒度后端生命周期，例如 `CREATED_PENDING_BACKUP`
- `nextAction`
  - `CREATE_OR_IMPORT`
  - `BACKUP_MNEMONIC`
  - `CONFIRM_MNEMONIC`
  - `READY`

## Remaining Follow-up

1. 助记词/私钥的真实解析、派生与安全持久化
2. `wallet/public-addresses` 与真实钱包地址生成/同步打通
3. 设备侧更完整的钱包创建/导入真机回归
