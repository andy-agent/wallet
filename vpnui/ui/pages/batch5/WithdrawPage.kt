package com.cryptovpn.ui.pages.withdraw

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cryptovpn.ui.theme.BackgroundDark
import com.cryptovpn.ui.theme.BackgroundMedium
import com.cryptovpn.ui.theme.BackgroundLight
import com.cryptovpn.ui.theme.PrimaryBlue
import com.cryptovpn.ui.theme.SuccessGreen
import com.cryptovpn.ui.theme.WarningYellow
import com.cryptovpn.ui.theme.ErrorRed
import com.cryptovpn.ui.theme.TextPrimary
import com.cryptovpn.ui.theme.TextSecondary
import com.cryptovpn.ui.theme.TextTertiary
import java.text.DecimalFormat

/**
 * 提现申请页
 * 支持USDT提现到Solana网络
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WithdrawPage(
    viewModel: WithdrawViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {},
    onWithdrawSuccess: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "提现",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "返回",
                            tint = TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundDark
                )
            )
        },
        containerColor = BackgroundDark
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (state) {
                is WithdrawState.Loading -> {
                    LoadingContent()
                }
                is WithdrawState.Submitted,
                is WithdrawState.UnderReview,
                is WithdrawState.Completed,
                is WithdrawState.Failed -> {
                    WithdrawResultContent(
                        state = state,
                        onBackClick = onBackClick,
                        onWithdrawSuccess = onWithdrawSuccess
                    )
                }
                else -> {
                    WithdrawFormContent(
                        state = state,
                        onAmountChange = viewModel::onAmountChange,
                        onAddressChange = viewModel::onAddressChange,
                        onNetworkChange = viewModel::onNetworkChange,
                        onWithdrawAll = viewModel::onWithdrawAll,
                        onPasteAddress = viewModel::onPasteAddress,
                        onSubmit = viewModel::onSubmit
                    )
                }
            }
        }
    }
}

@Composable
private fun LoadingContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = PrimaryBlue)
    }
}

@Composable
private fun WithdrawFormContent(
    state: WithdrawState,
    onAmountChange: (String) -> Unit,
    onAddressChange: (String) -> Unit,
    onNetworkChange: (WithdrawNetwork) -> Unit,
    onWithdrawAll: () -> Unit,
    onPasteAddress: () -> Unit,
    onSubmit: () -> Unit
) {
    val scrollState = rememberScrollState()
    val clipboardManager = LocalClipboardManager.current
    
    val currentState = state as? WithdrawState.Idle 
        ?: state as? WithdrawState.Validating
        ?: state as? WithdrawState.Submitting
        ?: WithdrawState.Idle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // 余额卡片
        BalanceCard(
            balanceUsdt = currentState.balanceUsdt,
            balanceCny = currentState.balanceCny
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 提示横幅
        WarningBanner()

        Spacer(modifier = Modifier.height(24.dp))

        // 金额输入
        AmountInputSection(
            amount = currentState.amount,
            onAmountChange = onAmountChange,
            onWithdrawAll = onWithdrawAll,
            error = currentState.amountError,
            isEnabled = state !is WithdrawState.Submitting
        )

        Spacer(modifier = Modifier.height(20.dp))

        // 地址输入
        AddressInputSection(
            address = currentState.address,
            onAddressChange = onAddressChange,
            onPasteClick = {
                val clipboardText = clipboardManager.getText()?.text ?: ""
                onPasteAddress()
            },
            error = currentState.addressError,
            isEnabled = state !is WithdrawState.Submitting
        )

        Spacer(modifier = Modifier.height(20.dp))

        // 网络选择
        NetworkSelectionSection(
            selectedNetwork = currentState.selectedNetwork,
            onNetworkChange = onNetworkChange,
            isEnabled = state !is WithdrawState.Submitting
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 预计到账信息
        EstimatedArrivalSection(
            amount = currentState.amount.toDoubleOrNull() ?: 0.0,
            fee = currentState.networkFee,
            willReceive = currentState.willReceive
        )

        Spacer(modifier = Modifier.height(32.dp))

        // 提交按钮
        SubmitButton(
            isEnabled = currentState.isFormValid && state !is WithdrawState.Submitting,
            isLoading = state is WithdrawState.Submitting,
            onClick = onSubmit
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun BalanceCard(
    balanceUsdt: Double,
    balanceCny: Double
) {
    val decimalFormat = remember { DecimalFormat("#,##0.00") }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            PrimaryBlue.copy(alpha = 0.3f),
                            PrimaryBlue.copy(alpha = 0.1f)
                        )
                    ),
                    shape = RoundedCornerShape(16.dp)
                )
                .border(
                    width = 1.dp,
                    color = PrimaryBlue.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(20.dp)
        ) {
            Column {
                Text(
                    text = "可提现余额",
                    color = TextSecondary,
                    fontSize = 14.sp
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = decimalFormat.format(balanceUsdt),
                        color = TextPrimary,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Text(
                        text = "USDT",
                        color = PrimaryBlue,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = "≈ ¥${decimalFormat.format(balanceCny)}",
                    color = TextSecondary,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
private fun WarningBanner() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = WarningYellow.copy(alpha = 0.1f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = WarningYellow,
                modifier = Modifier.size(20.dp)
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Text(
                text = "请确保提现地址正确，错误的地址将导致资产永久丢失",
                color = WarningYellow,
                fontSize = 13.sp,
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
private fun AmountInputSection(
    amount: String,
    onAmountChange: (String) -> Unit,
    onWithdrawAll: () -> Unit,
    error: String?,
    isEnabled: Boolean
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "提现金额",
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
            
            TextButton(
                onClick = onWithdrawAll,
                enabled = isEnabled
            ) {
                Text(
                    text = "全部提现",
                    color = if (isEnabled) PrimaryBlue else TextTertiary,
                    fontSize = 14.sp
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        OutlinedTextField(
            value = amount,
            onValueChange = { 
                // 只允许数字和小数点
                if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d{0,6}$"))) {
                    onAmountChange(it)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    text = "请输入提现金额",
                    color = TextTertiary
                )
            },
            suffix = {
                Text(
                    text = "USDT",
                    color = TextSecondary,
                    fontSize = 14.sp
                )
            },
            isError = error != null,
            supportingText = error?.let {
                {
                    Text(
                        text = it,
                        color = ErrorRed,
                        fontSize = 12.sp
                    )
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal,
                imeAction = ImeAction.Next
            ),
            singleLine = true,
            enabled = isEnabled,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = BackgroundMedium,
                unfocusedContainerColor = BackgroundMedium,
                focusedBorderColor = PrimaryBlue,
                unfocusedBorderColor = BackgroundLight,
                errorBorderColor = ErrorRed,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            )
        )
    }
}

@Composable
private fun AddressInputSection(
    address: String,
    onAddressChange: (String) -> Unit,
    onPasteClick: () -> Unit,
    error: String?,
    isEnabled: Boolean
) {
    Column {
        Text(
            text = "提现地址",
            color = TextPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        OutlinedTextField(
            value = address,
            onValueChange = onAddressChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    text = "请输入或粘贴Solana地址",
                    color = TextTertiary
                )
            },
            trailingIcon = {
                IconButton(
                    onClick = onPasteClick,
                    enabled = isEnabled
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentPaste,
                        contentDescription = "粘贴",
                        tint = if (isEnabled) PrimaryBlue else TextTertiary
                    )
                }
            },
            isError = error != null,
            supportingText = error?.let {
                {
                    Text(
                        text = it,
                        color = ErrorRed,
                        fontSize = 12.sp
                    )
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            singleLine = true,
            enabled = isEnabled,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = BackgroundMedium,
                unfocusedContainerColor = BackgroundMedium,
                focusedBorderColor = PrimaryBlue,
                unfocusedBorderColor = BackgroundLight,
                errorBorderColor = ErrorRed,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            )
        )
    }
}

@Composable
private fun NetworkSelectionSection(
    selectedNetwork: WithdrawNetwork,
    onNetworkChange: (WithdrawNetwork) -> Unit,
    isEnabled: Boolean
) {
    Column {
        Text(
            text = "选择网络",
            color = TextPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        WithdrawNetwork.entries.forEach { network ->
            NetworkOption(
                network = network,
                isSelected = selectedNetwork == network,
                onClick = { onNetworkChange(network) },
                isEnabled = isEnabled
            )
            
            if (network != WithdrawNetwork.entries.last()) {
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun NetworkOption(
    network: WithdrawNetwork,
    isSelected: Boolean,
    onClick: () -> Unit,
    isEnabled: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = isEnabled, onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) 
                PrimaryBlue.copy(alpha = 0.15f) 
            else 
                BackgroundMedium
        ),
        border = androidx.compose.foundation.BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) PrimaryBlue else BackgroundLight
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 网络图标
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = network.color.copy(alpha = 0.2f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = network.symbol,
                    color = network.color,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = network.displayName,
                    color = TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
                
                Text(
                    text = network.description,
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }
            
            // 单选按钮
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .background(
                        color = if (isSelected) PrimaryBlue else Color.Transparent,
                        shape = CircleShape
                    )
                    .border(
                        width = 2.dp,
                        color = if (isSelected) PrimaryBlue else TextTertiary,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(Color.White, CircleShape)
                    )
                }
            }
        }
    }
}

@Composable
private fun EstimatedArrivalSection(
    amount: Double,
    fee: Double,
    willReceive: Double
) {
    val decimalFormat = remember { DecimalFormat("#,##0.00") }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = BackgroundMedium
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "预计到账",
                color = TextSecondary,
                fontSize = 14.sp
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            InfoRow(
                label = "提现金额",
                value = "${decimalFormat.format(amount)} USDT"
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            InfoRow(
                label = "网络手续费",
                value = "${decimalFormat.format(fee)} USDT"
            )
            
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = BackgroundLight
            )
            
            InfoRow(
                label = "实际到账",
                value = "${decimalFormat.format(willReceive)} USDT",
                valueColor = SuccessGreen,
                isBold = true
            )
        }
    }
}

@Composable
private fun InfoRow(
    label: String,
    value: String,
    valueColor: Color = TextPrimary,
    isBold: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            color = TextSecondary,
            fontSize = 14.sp
        )
        
        Text(
            text = value,
            color = valueColor,
            fontSize = 14.sp,
            fontWeight = if (isBold) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}

@Composable
private fun SubmitButton(
    isEnabled: Boolean,
    isLoading: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        enabled = isEnabled && !isLoading,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = PrimaryBlue,
            disabledContainerColor = PrimaryBlue.copy(alpha = 0.4f)
        )
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = Color.White,
                strokeWidth = 2.dp
            )
        } else {
            Text(
                text = "确认提现",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun WithdrawResultContent(
    state: WithdrawState,
    onBackClick: () -> Unit,
    onWithdrawSuccess: () -> Unit
) {
    val (icon, iconColor, title, message, buttonText, buttonAction) = when (state) {
        is WithdrawState.Submitted -> Quadruple(
            Icons.Default.Info,
            PrimaryBlue,
            "提现申请已提交",
            "您的提现申请已成功提交，我们将在24小时内处理",
            "查看订单",
            onWithdrawSuccess
        )
        is WithdrawState.UnderReview -> Quadruple(
            Icons.Default.Info,
            WarningYellow,
            "提现审核中",
            "您的提现正在审核中，预计1-3个工作日完成",
            "我知道了",
            onWithdrawSuccess
        )
        is WithdrawState.Completed -> Quadruple(
            Icons.Default.Info,
            SuccessGreen,
            "提现成功",
            "您的提现已成功处理，资金已到账",
            "完成",
            onWithdrawSuccess
        )
        is WithdrawState.Failed -> Quadruple(
            Icons.Default.Warning,
            ErrorRed,
            "提现失败",
            (state as WithdrawState.Failed).errorMessage,
            "重新尝试",
            onBackClick
        )
        else -> Quadruple(
            Icons.Default.Info,
            TextSecondary,
            "",
            "",
            "",
            {}
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(
                    color = iconColor.copy(alpha = 0.15f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(40.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = title,
            color = TextPrimary,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Text(
            text = message,
            color = TextSecondary,
            fontSize = 15.sp,
            lineHeight = 22.sp,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(40.dp))
        
        Button(
            onClick = buttonAction,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = PrimaryBlue
            )
        ) {
            Text(
                text = buttonText,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

// 辅助数据类
private data class Quadruple<A, B, C, D, E, F>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D,
    val fifth: E,
    val sixth: F
)

@Preview
@Composable
fun WithdrawPagePreview() {
    MaterialTheme {
        WithdrawPage()
    }
}

@Preview
@Composable
fun WithdrawPageSubmittedPreview() {
    MaterialTheme {
        WithdrawResultContent(
            state = WithdrawState.Submitted,
            onBackClick = {},
            onWithdrawSuccess = {}
        )
    }
}
