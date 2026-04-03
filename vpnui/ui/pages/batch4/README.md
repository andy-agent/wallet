# CryptoVPN Android App - 第4批页面实现

## 批次概述
本批次实现了钱包操作和增长相关的5个页面。

## 创建的文件列表

### 钱包操作页面 (3页)

1. **ReceivePage.kt** - 收款页
   - 二维码展示区域 (240dp白色背景)
   - 钱包地址卡片
   - 复制/分享按钮
   - 网络提示警告
   - 状态: Loading/SyncingAddress/Ready/Error

2. **ReceiveViewModel.kt** - 收款页ViewModel
   - 加载钱包地址
   - 同步公钥地址
   - 复制/分享地址功能
   - 二维码生成

3. **SendPage.kt** - 发送页
   - 资产选择卡片
   - 收款地址输入 (支持扫码)
   - 金额输入 (显示USD估值+全部按钮)
   - 手续费卡片
   - 确认对话框
   - 状态: Loading/Editing/ReadyToSign/Broadcasting/Pending/Success/Error

4. **SendViewModel.kt** - 发送页ViewModel
   - 资产选择管理
   - 地址验证
   - 金额计算和验证
   - 交易广播
   - 状态流转控制

5. **SendResultPage.kt** - 发送结果页
   - 成功/失败状态展示
   - 交易详情卡片
   - 查看交易/返回钱包按钮
   - 成功图标动画效果

### 增长页面 (2页)

6. **InviteCenterPage.kt** - 邀请中心页
   - 邀请码卡片 (32sp Bold)
   - 统计网格 (一级/二级邀请数+收益)
   - 余额卡片 (渐变背景)
   - 提示横幅
   - 分佣规则展示
   - 状态: Loading/Loaded/WithdrawDisabled/BindingLocked/Error

7. **InviteViewModel.kt** - 邀请中心ViewModel
   - 加载邀请数据
   - 分享邀请链接
   - 检查提现/锁定状态

8. **CommissionLedgerPage.kt** - 佣金账本页
   - 摘要卡片 (本月收益+累计收益)
   - 筛选标签 (全部/一级/二级/冻结)
   - 交易列表项
   - 层级标签+金额+来源+状态+时间
   - 详情对话框

9. **CommissionLedgerViewModel.kt** - 佣金账本ViewModel
   - 加载账本数据
   - 筛选交易记录
   - 计算收益统计

## 页面状态定义

### ReceivePageState
```kotlin
sealed class ReceivePageState {
    data object Loading : ReceivePageState()
    data class SyncingAddress(val message: String) : ReceivePageState()
    data class Ready(val walletAddress: String, val network: String, val qrCodeBitmap: Bitmap?) : ReceivePageState()
    data class Error(val message: String, val canRetry: Boolean) : ReceivePageState()
}
```

### SendPageState
```kotlin
sealed class SendPageState {
    data object Loading : SendPageState()
    data class Editing(
        val selectedAsset: AssetInfo,
        val recipientAddress: String,
        val amount: String,
        val usdValue: String,
        val fee: FeeInfo,
        val availableBalance: BigDecimal,
        val isAddressValid: Boolean,
        val isAmountValid: Boolean
    ) : SendPageState()
    data class ReadyToSign(...) : SendPageState()
    data class Broadcasting(val message: String) : SendPageState()
    data class Pending(val txHash: String) : SendPageState()
    data class Success(val txHash: String, val amount: BigDecimal) : SendPageState()
    data class Error(val message: String) : SendPageState()
}
```

### InviteCenterState
```kotlin
sealed class InviteCenterState {
    data object Loading : InviteCenterState()
    data class Loaded(
        val inviteCode: String,
        val level1Count: Int,
        val level2Count: Int,
        val totalEarnings: BigDecimal,
        val withdrawableBalance: BigDecimal,
        val commissionRates: CommissionRates
    ) : InviteCenterState()
    data class WithdrawDisabled(val reason: String) : InviteCenterState()
    data class BindingLocked(val message: String) : InviteCenterState()
    data class Error(val message: String) : InviteCenterState()
}
```

### CommissionLedgerState
```kotlin
sealed class CommissionLedgerState {
    data object Loading : CommissionLedgerState()
    data class Loaded(
        val currentMonthEarnings: BigDecimal,
        val totalEarnings: BigDecimal,
        val selectedFilter: CommissionFilter,
        val transactions: List<CommissionTransaction>
    ) : CommissionLedgerState()
    data class Empty(val message: String) : CommissionLedgerState()
    data class Error(val message: String) : CommissionLedgerState()
}
```

## 关键代码片段

### 二维码生成
```kotlin
private fun generateQRCode(content: String, size: Int): Bitmap? {
    return try {
        val writer = QRCodeWriter()
        val bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, size, size)
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        for (x in 0 until size) {
            for (y in 0 until size) {
                bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
            }
        }
        bitmap
    } catch (e: Exception) { null }
}
```

### 成功图标动画
```kotlin
val scale by rememberInfiniteTransition().animateFloat(
    initialValue = 1f,
    targetValue = 1.05f,
    animationSpec = infiniteRepeatable(
        animation = tween(1000, easing = EaseInOutCubic),
        repeatMode = RepeatMode.Reverse
    )
)
```

### 筛选标签
```kotlin
@Composable
private fun FilterTabs(
    selectedFilter: CommissionFilter,
    onFilterSelected: (CommissionFilter) -> Unit
) {
    val filters = listOf(
        CommissionFilter.ALL to "全部",
        CommissionFilter.LEVEL1 to "一级",
        CommissionFilter.LEVEL2 to "二级",
        CommissionFilter.FROZEN to "冻结"
    )
    // ...
}
```

## 设计系统

### 颜色
- 背景色: #0B1020, #111827, #1F2937
- 主色: #1D4ED8
- 成功: #22C55E
- 警告: #F59E0B
- 错误: #EF4444
- Solana: #9945FF
- TRON: #FF060A

### 技术栈
- Jetpack Compose
- MVVM架构
- Hilt依赖注入
- Material3组件
- Zxing二维码生成

## 依赖说明

需要添加以下依赖:
```gradle
// QR Code
implementation 'com.google.zxing:core:3.5.2'

// Compose
implementation 'androidx.compose.ui:ui:1.6.0'
implementation 'androidx.compose.material3:material3:1.2.0'
implementation 'androidx.hilt:hilt-navigation-compose:1.1.0'
```

## 文件路径

所有文件位于: `/mnt/okcomputer/output/cryptovpn/ui/pages/batch4/`
