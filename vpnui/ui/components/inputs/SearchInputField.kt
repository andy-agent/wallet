package com.cryptovpn.ui.components.inputs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cryptovpn.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchInputField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "搜索...",
    modifier: Modifier = Modifier
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .height(48.dp)
            .background(BackgroundSecondary, RoundedCornerShape(12.dp)),
        placeholder = {
            Text(
                text = placeholder,
                color = TextTertiary,
                fontSize = 14.sp
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "搜索",
                tint = TextTertiary,
                modifier = Modifier.size(20.dp)
            )
        },
        trailingIcon = {
            if (value.isNotEmpty()) {
                IconButton(onClick = { onValueChange("") }) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "清除",
                        tint = TextTertiary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        },
        singleLine = true,
        textStyle = TextStyle(
            color = TextPrimary,
            fontSize = 14.sp
        ),
        colors = TextFieldDefaults.textFieldColors(
            containerColor = BackgroundSecondary,
            focusedIndicatorColor = BorderFocus,
            unfocusedIndicatorColor = Color.Transparent,
            cursorColor = Primary
        )
    )
}

@Preview
@Composable
fun SearchInputFieldPreview() {
    CryptoVPNTheme {
        var text by remember { mutableStateOf("") }
        SearchInputField(
            value = text,
            onValueChange = { text = it },
            placeholder = "搜索国家或地区...",
            modifier = Modifier.fillMaxWidth()
        )
    }
}