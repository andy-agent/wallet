package com.cryptovpn.ui.components.inputs

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cryptovpn.ui.theme.*

/**
 * 文本输入框组件
 * 
 * 支持图标、清除按钮、错误提示等
 * 
 * @param value 输入值
 * @param onValueChange 值变化回调
 * @param modifier 修饰符
 * @param placeholder 占位符文字
 * @param label 标签文字（可选）
 * @param leadingIcon 左侧图标（可选）
 * @param trailingIcon 右侧图标（可选）
 * @param isError 是否错误状态
 * @param errorMessage 错误提示信息
 * @param enabled 是否可用
 * @param readOnly 是否只读
 * @param keyboardOptions 键盘选项
 * @param keyboardActions 键盘动作
 * @param visualTransformation 视觉转换
 * @param singleLine 是否单行
 * @param maxLines 最大行数
 * @param minLines 最小行数
 */
@Composable
fun TextInputField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    label: String? = null,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    onTrailingIconClick: (() -> Unit)? = null,
    isError: Boolean = false,
    errorMessage: String? = null,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    singleLine: Boolean = true,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val focusRequester = remember { FocusRequester() }
    
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
                readOnly = readOnly,
                textStyle = AppTypography.Body.copy(color = TextPrimary),
                keyboardOptions = keyboardOptions,
                keyboardActions = keyboardActions,
                singleLine = singleLine,
                maxLines = maxLines,
                minLines = minLines,
                visualTransformation = visualTransformation,
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
            
            // Clear button (shown when has text)
            AnimatedVisibility(
                visible = value.isNotEmpty() && enabled && !readOnly,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Row {
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = androidx.compose.material.icons.Icons.Default.Clear,
                        contentDescription = "清除",
                        modifier = Modifier
                            .size(20.dp)
                            .clickable { onValueChange("") },
                        tint = TextTertiary
                    )
                }
            }
            
            // Trailing icon
            if (trailingIcon != null) {
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = trailingIcon,
                    contentDescription = null,
                    modifier = Modifier
                        .size(20.dp)
                        .then(
                            if (onTrailingIconClick != null) {
                                Modifier.clickable(onClick = onTrailingIconClick)
                            } else Modifier
                        ),
                    tint = if (isError) Error else TextTertiary
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

@Preview(name = "Text Input Field - All States")
@Composable
fun TextInputFieldPreview() {
    CryptoVPNTheme {
        Column(
            modifier = Modifier
                .background(BackgroundPrimary)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            var text1 by remember { mutableStateOf("") }
            var text2 by remember { mutableStateOf("hello@example.com") }
            var text3 by remember { mutableStateOf("") }
            
            // Empty with placeholder
            TextInputField(
                value = text1,
                onValueChange = { text1 = it },
                placeholder = "请输入邮箱",
                label = "邮箱地址"
            )
            
            // With value
            TextInputField(
                value = text2,
                onValueChange = { text2 = it },
                placeholder = "请输入邮箱",
                label = "邮箱地址",
                leadingIcon = androidx.compose.material.icons.Icons.Default.Email
            )
            
            // Error state
            TextInputField(
                value = text3,
                onValueChange = { text3 = it },
                placeholder = "请输入密码",
                label = "密码",
                isError = true,
                errorMessage = "密码不能为空",
                leadingIcon = androidx.compose.material.icons.Icons.Default.Lock
            )
            
            // Disabled
            TextInputField(
                value = "disabled@example.com",
                onValueChange = {},
                label = "邮箱（只读）",
                enabled = false,
                leadingIcon = androidx.compose.material.icons.Icons.Default.Email
            )
        }
    }
}
