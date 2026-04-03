package com.cryptovpn.ui.components.inputs

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cryptovpn.ui.theme.*

/**
 * 密码输入框组件
 * 
 * 带可见性切换功能
 * 
 * @param value 输入值
 * @param onValueChange 值变化回调
 * @param modifier 修饰符
 * @param placeholder 占位符文字
 * @param label 标签文字（可选）
 * @param leadingIcon 左侧图标（可选）
 * @param isError 是否错误状态
 * @param errorMessage 错误提示信息
 * @param enabled 是否可用
 * @param keyboardOptions 键盘选项
 * @param keyboardActions 键盘动作
 * @param imeAction 键盘动作类型
 * @param onDone 完成回调
 */
@Composable
fun PasswordInputField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "请输入密码",
    label: String? = null,
    leadingIcon: ImageVector? = null,
    isError: Boolean = false,
    errorMessage: String? = null,
    enabled: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    imeAction: ImeAction = ImeAction.Done,
    onDone: (() -> Unit)? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val focusRequester = remember { FocusRequester() }
    
    var passwordVisible by remember { mutableStateOf(false) }
    
    Column(modifier = modifier.fillMaxWidth()) {
        // Label
        if (label != null) {
            Text(
                text = label,
                style = AppTypography.LabelMedium,
                color = TextSecondary,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        
        // Input container
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(Dimens.InputHeightLarge)
                .background(
                    color = BackgroundSecondary,
                    shape = AppShape.Input
                )
                .border(
                    width = if (isFocused || isError) 1.dp else 0.dp,
                    color = when {
                        isError -> BorderError
                        isFocused -> BorderFocused
                        else -> Color.Transparent
                    },
                    shape = AppShape.Input
                )
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Leading icon
            if (leadingIcon != null) {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = if (isError) Error else TextTertiary
                )
                Spacer(modifier = Modifier.width(12.dp))
            }
            
            // Text field
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier
                    .weight(1f)
                    .focusRequester(focusRequester),
                enabled = enabled,
                textStyle = AppTypography.Body.copy(color = TextPrimary),
                keyboardOptions = keyboardOptions.copy(
                    keyboardType = KeyboardType.Password,
                    imeAction = imeAction
                ),
                keyboardActions = if (onDone != null) {
                    KeyboardActions(onDone = { onDone() })
                } else keyboardActions,
                singleLine = true,
                visualTransformation = if (passwordVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                interactionSource = interactionSource,
                cursorBrush = SolidColor(Primary),
                decorationBox = { innerTextField ->
                    Box {
                        if (value.isEmpty() && placeholder.isNotEmpty()) {
                            Text(
                                text = placeholder,
                                style = AppTypography.Body,
                                color = TextTertiary
                            )
                        }
                        innerTextField()
                    }
                }
            )
            
            // Visibility toggle
            if (value.isNotEmpty()) {
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = { passwordVisible = !passwordVisible },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (passwordVisible) {
                            androidx.compose.material.icons.Icons.Default.Visibility
                        } else {
                            androidx.compose.material.icons.Icons.Default.VisibilityOff
                        },
                        contentDescription = if (passwordVisible) "隐藏密码" else "显示密码",
                        modifier = Modifier.size(20.dp),
                        tint = TextTertiary
                    )
                }
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

/**
 * 确认密码输入框（带密码强度指示）
 */
@Composable
fun ConfirmPasswordInputField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "请确认密码",
    label: String = "确认密码",
    originalPassword: String = "",
    enabled: Boolean = true
) {
    val isMatch = value.isEmpty() || value == originalPassword
    
    PasswordInputField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        placeholder = placeholder,
        label = label,
        isError = !isMatch,
        errorMessage = if (!isMatch) "两次输入的密码不一致" else null,
        enabled = enabled
    )
}

@Preview(name = "Password Input Field - All States")
@Composable
fun PasswordInputFieldPreview() {
    CryptoVPNTheme {
        Column(
            modifier = Modifier
                .background(BackgroundPrimary)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            var password1 by remember { mutableStateOf("") }
            var password2 by remember { mutableStateOf("mypassword123") }
            var password3 by remember { mutableStateOf("short") }
            
            // Empty
            PasswordInputField(
                value = password1,
                onValueChange = { password1 = it },
                label = "密码",
                leadingIcon = androidx.compose.material.icons.Icons.Default.Lock
            )
            
            // With value
            PasswordInputField(
                value = password2,
                onValueChange = { password2 = it },
                label = "密码",
                leadingIcon = androidx.compose.material.icons.Icons.Default.Lock
            )
            
            // Error state
            PasswordInputField(
                value = password3,
                onValueChange = { password3 = it },
                label = "密码",
                isError = true,
                errorMessage = "密码长度至少8位",
                leadingIcon = androidx.compose.material.icons.Icons.Default.Lock
            )
        }
    }
}
