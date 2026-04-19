package com.v2ray.ang.composeui.pages.p2extended

import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.v2ray.ang.composeui.components.feature.FeaturePageTemplate
import com.v2ray.ang.composeui.components.cards.TechCard
import com.v2ray.ang.composeui.effects.MotionProfile
import com.v2ray.ang.composeui.p2extended.model.CreateWalletEvent
import com.v2ray.ang.composeui.p2extended.model.CreateWalletUiState
import com.v2ray.ang.composeui.p2extended.model.createWalletPreviewState
import com.v2ray.ang.composeui.p2extended.viewmodel.CreateWalletViewModel
import com.v2ray.ang.composeui.theme.AppTheme
import com.v2ray.ang.composeui.theme.CryptoVpnTheme
import com.v2ray.ang.composeui.theme.TextMuted
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

@Composable
fun CreateWalletRoute(
    viewModel: CreateWalletViewModel,
    onPrimaryAction: (String?) -> Unit = { _ -> },
    onSecondaryAction: (() -> Unit)? = null,
    onBottomNav: (String) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.onEvent(CreateWalletEvent.Refresh)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
    CreateWalletScreen(
        uiState = uiState,
        onEvent = { event ->
            viewModel.onEvent(event)
            when (event) {
                CreateWalletEvent.PrimaryActionClicked -> {
                    if (!uiState.isLoading) {
                        viewModel.submitCreate(
                            onSuccess = onPrimaryAction,
                            onError = { message ->
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                            },
                        )
                    }
                }
                CreateWalletEvent.SecondaryActionClicked -> if (!uiState.isLoading) onSecondaryAction?.invoke()
                else -> Unit
            }
        },
        onBottomNav = onBottomNav,
    )
}

@Composable
fun CreateWalletScreen(
    uiState: CreateWalletUiState,
    onEvent: (CreateWalletEvent) -> Unit,
    onBottomNav: (String) -> Unit = {},
) {
    FeaturePageTemplate(
        title = uiState.title,
        subtitle = "",
        badge = uiState.badge,
        summary = uiState.summary,
        heroAccent = uiState.heroAccent,
        metrics = uiState.metrics,
        fields = uiState.fields,
        highlights = uiState.highlights,
        checklist = uiState.checklist,
        note = uiState.note,
        primaryActionLabel = uiState.primaryActionLabel,
        secondaryActionLabel = uiState.secondaryActionLabel,
        showBottomBar = false,
        currentRoute = "create_wallet",
        motionProfile = MotionProfile.L1,
        onBottomNav = onBottomNav,
        onFieldChanged = { key, value ->
            onEvent(CreateWalletEvent.FieldChanged(key = key, value = value))
        },
        onPrimaryAction = {
            onEvent(CreateWalletEvent.PrimaryActionClicked)
        },
        onSecondaryAction = {
            onEvent(CreateWalletEvent.SecondaryActionClicked)
        },
    )
    if (uiState.progressVisible) {
        WalletCreationProgressDialog(
            progress = uiState.progressValue,
            stageLabel = uiState.progressStageLabel,
            etaLabel = uiState.progressEtaLabel,
        )
    }
}

@Composable
private fun WalletCreationProgressDialog(
    progress: Float,
    stageLabel: String,
    etaLabel: String,
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        label = "wallet_create_progress",
    )
    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
        ),
    ) {
        TechCard(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(
                    text = "正在创建钱包",
                    style = MaterialTheme.typography.headlineSmall,
                )
                Text(
                    text = etaLabel,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextMuted,
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(999.dp))
                        .background(AppTheme.colors.surfaceElevated),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(animatedProgress)
                            .fillMaxHeight()
                            .background(
                                brush = AppTheme.gradients.primaryGradient,
                                shape = RoundedCornerShape(999.dp),
                            ),
                    )
                }
                Text(
                    text = stageLabel,
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    text = "${(animatedProgress * 100).roundToInt()}%",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted,
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun CreateWalletPreview() {
    CryptoVpnTheme {
        CreateWalletScreen(
            uiState = createWalletPreviewState(),
            onEvent = {},
        )
    }
}
