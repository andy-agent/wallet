package com.v2ray.ang.composeui.components.wallet.chain

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.v2ray.ang.composeui.common.model.FeatureField
import com.v2ray.ang.composeui.components.cards.AppCard
import com.v2ray.ang.composeui.components.inputs.AppTextField
import com.v2ray.ang.composeui.theme.AppTheme

@Composable
fun CustomTokenFormSection(
    fields: List<FeatureField>,
    modifier: Modifier = Modifier,
    onFieldChanged: (String, String) -> Unit,
) {
    AppCard(modifier = modifier) {
        Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.space12)) {
            fields.forEach { field ->
                AppTextField(
                    value = field.value,
                    label = field.label,
                    onValueChange = { onFieldChanged(field.key, it) },
                    placeholder = field.placeholder,
                    supportingText = field.supportingText,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )
            }
        }
    }
}
