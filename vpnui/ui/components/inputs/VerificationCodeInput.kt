package com.cryptovpn.ui.components.inputs

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cryptovpn.ui.theme.*
import kotlinx.coroutines.delay

/**
 * 验证码输入框组件
 * 
 * 带倒计时功能的验证码输入
 * 
 * @param value 输入值
 * @param onValueChange 值变化回调
 * @param onSendCode 发送验证码回调
 * @param modifier 修饰符
 * @param length 验证码长度
 * @param countdownSeconds 倒计时秒数
 * @param enabled 是否可用
 * @param isError 是否错误状态
 * @param errorMessage 错误提示信息
 */
@Composable
fun VerificationCodeInput(
    value: String,
    onValueChange: (String) -> Unit,
    onSendCode: () -> Unit,
    modifier: Modifier = Modifier,
    length: Int = 6,
    countdownSeconds: Int = 60,
    enabled: Boolean = true,
    isError: Boolean = false,
    errorMessage: String? = null
) {
    var countdown by remember { mutableStateOf(0) }
    var isSending by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    
    // 倒计时逻辑
    LaunchedEffect(countdown) {
        if (countdown > 0) {
            delay(1000)
            countdown--
        }
    }
    
    val canSend = countdown == 0 && enabled && !isSending
    val buttonText = when {
        isSending -> "发送中..."
        countdown > 0 -> "${countdown}s后重发"
        else -> "获取验证码"
    }
    
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Code input
            BasicTextField(
                value = value,
                onValueChange = { newValue ->
                    if (newValue.length <= length && newValue.all { it.isDigit() }) {
                        onValueChange(newValue)
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .focusRequester(focusRequester),
                enabled = enabled,
                textStyle = AppTypography.NumberMedium.copy(
                    color = TextPrimary,
                    textAlign = TextAlign.Center
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.NumberPassword,
                    imeAction = ImeAction.Done
                ),
                singleLine = true,
                cursorBrush = SolidColor(Primary),
                decorationBox = { innerTextField ->
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        repeat(length) { index ->
                            val char = value.getOrNull(index)
                            val isFocused = value.length == index
                            
                            CodeBox(
                                char = char,
                                isFocused = isFocused,
                                isError = isError
                            )
                        }
                    }
                    innerTextField()
                }
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Send button
            TextButton(
                onClick = {
                    if (canSend) {
                        isSending = true
                        onSendCode()
                        countdown = countdownSeconds
                        isSending = false
                    }
                },
                enabled = canSend,
                modifier = Modifier.width(100.dp)
            ) {
                Text(
                    text = buttonText,
                    style = AppTypography.LabelMedium,
                    color = if (canSend) Primary else TextDisabled
                )
            }
        }
        
        // Error message
        AnimatedVisibility(
            visible = isError && !errorMessage.isNullOrEmpty(),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Text(
                text = errorMessage ?: "",
                style = AppTypography.BodySmall,
                color = Error,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

/**
 * 验证码输入框（单个）
 */
@Composable
private fun CodeBox(
    char: Char?,
    isFocused: Boolean,
    isError: Boolean
) {
    Box(
        modifier = Modifier
            .size(44.dp)
            .background(
                color = BackgroundSecondary,
                shape = AppShape.Input
            )
            .border(
                width = when {
                    isError -> 1.dp
                    isFocused -> 2.dp
                    else -> 0.dp
                },
                color = when {
                    isError -> BorderError
                    isFocused -> BorderFocused
                    else -> Color.Transparent
                },
                shape = AppShape.Input
            ),
        contentAlignment = Alignment.Center
    ) {
        if (char != null) {
            Text(
                text = char.toString(),
                style = AppTypography.NumberMedium,
                color = TextPrimary
            )
        }
    }
}

/**
 * 简化的验证码输入（单行输入框样式）
 */
@Composable
fun VerificationCodeInputSimple(
    value: String,
    onValueChange: (String) -> Unit,
    onSendCode: () -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "请输入验证码",
    label: String? = null,
    countdownSeconds: Int = 60,
    enabled: Boolean = true,
    isError: Boolean = false,
    errorMessage: String? = null
) {
    var countdown by remember { mutableStateOf(0) }
    var isSending by remember { mutableStateOf(false) }
    
    // 倒计时逻辑
    LaunchedEffect(countdown) {
        if (countdown > 0) {
            delay(1000)
            countdown--
        }
    }
    
    val canSend = countdown == 0 && enabled && !isSending
    val buttonText = when {
        isSending -> "发送中..."
        countdown > 0 -> "${countdown}s"
        else -> "获取验证码"
    }
    
    Column(modifier = modifier.fillMaxWidth()) {
        if (label != null) {
            Text(
                text = label,
                style = AppTypography.LabelMedium,
                color = TextSecondary,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextInputField(
                value = value,
                onValueChange = { newValue ->
                    if (newValue.length <= 6 && newValue.all { it.isDigit() }) {
                        onValueChange(newValue)
                    }
                },
                modifier = Modifier.weight(1f),
                placeholder = placeholder,
                isError = isError,
                errorMessage = null, // 在下方统一显示
                enabled = enabled,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.NumberPassword
                )
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            androidx.compose.material3.Button(
                onClick = {
                    if (canSend) {
                        isSending = true
                        onSendCode()
                        countdown = countdownSeconds
                        isSending = false
                    }
                },
                enabled = canSend,
                modifier = Modifier.height(48.dp),
                shape = AppShape.Button
            ) {
                Text(
                    text = buttonText,
                    style = AppTypography.LabelMedium
                )
            }
        }
        
        // Error message
        AnimatedVisibility(
            visible = isError && !errorMessage.isNullOrEmpty(),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Text(
                text = errorMessage ?: "",
                style = AppTypography.BodySmall,
                color = Error,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Preview(name = "Verification Code Input")
@Composable
fun VerificationCodeInputPreview() {
    CryptoVPNTheme {
        Column(
            modifier = Modifier
                .background(BackgroundPrimary)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            var code1 by remember { mutableStateOf("1234") }
            var code2 by remember { mutableStateOf("") }
            
            // Box style
            VerificationCodeInput(
                value = code1,
                onValueChange = { code1 = it },
                onSendCode = {},
                length = 6
            )
            
            // Simple style
            VerificationCodeInputSimple(
                value = code2,
                onValueChange = { code2 = it },
                onSendCode = {},
                label = "验证码",
                isError = true,
                errorMessage = "验证码错误"
            )
        }
    }
}
