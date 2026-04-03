package com.cryptovpn.ui.components.buttons

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cryptovpn.ui.theme.*

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    size: ButtonSize = ButtonSize.LARGE
) {
    val height = when (size) {
        ButtonSize.LARGE -> 52.dp
        ButtonSize.MEDIUM -> 44.dp
        ButtonSize.SMALL -> 36.dp
    }

    Button(
        onClick = onClick,
        enabled = enabled && !isLoading,
        modifier = modifier.height(height),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Primary,
            contentColor = Color.White,
            disabledContainerColor = PrimaryDisabled,
            disabledContentColor = Color.White.copy(alpha = 0.5f)
        )
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = Color.White,
                strokeWidth = 2.dp
            )
        } else {
            Text(
                text = text,
                fontSize = if (size == ButtonSize.LARGE) 16.sp else 14.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

enum class ButtonSize {
    LARGE, MEDIUM, SMALL
}

@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.height(44.dp),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = TextPrimary
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderDefault)
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Preview
@Composable
fun PrimaryButtonPreview() {
    CryptoVPNTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            PrimaryButton(text = "登录", onClick = {})
            Spacer(modifier = Modifier.height(8.dp))
            PrimaryButton(text = "登录中...", onClick = {}, isLoading = true)
            Spacer(modifier = Modifier.height(8.dp))
            PrimaryButton(text = "禁用", onClick = {}, enabled = false)
        }
    }
}