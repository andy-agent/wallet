package com.app.feature.vpn.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.R
import com.app.feature.vpn.viewmodel.VpnViewModel

private const val OverviewFrameWidth = 390f
private const val OverviewFrameHeight = 844f

private data class OverviewHotspot(
    val x: Float,
    val y: Float,
    val width: Float,
    val height: Float,
    val onClick: () -> Unit,
)

@Composable
fun VpnHomeScreen(
    viewModel: VpnViewModel = viewModel(),
    onOpenNodes: () -> Unit = {},
    onOpenPlans: () -> Unit = {},
    onOpenSubscription: () -> Unit = {},
    onOpenOrders: () -> Unit = {},
    onOpenWalletHome: () -> Unit = {},
    onOpenMarket: () -> Unit = {},
    onOpenInvite: () -> Unit = {},
    onOpenProfile: () -> Unit = {},
    onOpenLedger: () -> Unit = {},
    onOpenSecurity: () -> Unit = {},
) {
    val interactionSource = remember { MutableInteractionSource() }
    val hotspots = listOf(
        OverviewHotspot(36f, 227f, 96f, 34f, onOpenWalletHome),
        OverviewHotspot(146f, 227f, 96f, 34f, onOpenWalletHome),
        OverviewHotspot(256f, 227f, 96f, 34f, onOpenWalletHome),
        OverviewHotspot(133f, 308f, 124f, 124f, viewModel::connectOrDisconnect),
        OverviewHotspot(329f, 291f, 30f, 30f, onOpenNodes),
        OverviewHotspot(36f, 421f, 43f, 24f, onOpenNodes),
        OverviewHotspot(22f, 476f, 346f, 136f, onOpenMarket),
        OverviewHotspot(24f, 634f, 62f, 58f, onOpenPlans),
        OverviewHotspot(95f, 634f, 60f, 58f, onOpenInvite),
        OverviewHotspot(164f, 634f, 62f, 58f, onOpenProfile),
        OverviewHotspot(234f, 634f, 62f, 58f, onOpenLedger),
        OverviewHotspot(304f, 634f, 62f, 58f, onOpenSubscription),
        OverviewHotspot(24f, 684f, 62f, 58f, onOpenOrders),
        OverviewHotspot(95f, 684f, 60f, 58f, onOpenProfile),
        OverviewHotspot(164f, 684f, 62f, 58f, onOpenInvite),
        OverviewHotspot(234f, 684f, 62f, 58f, onOpenSecurity),
        OverviewHotspot(304f, 684f, 62f, 58f, onOpenProfile),
        OverviewHotspot(45f, 779f, 72f, 22f, onOpenPlans),
        OverviewHotspot(0f, 801f, 78f, 43f, {}),
        OverviewHotspot(78f, 801f, 78f, 43f, onOpenMarket),
        OverviewHotspot(156f, 801f, 78f, 43f, onOpenWalletHome),
        OverviewHotspot(234f, 801f, 78f, 43f, onOpenInvite),
        OverviewHotspot(312f, 801f, 78f, 43f, onOpenProfile),
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState()),
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            val scale = maxWidth / OverviewFrameWidth.dp
            val imageHeight = OverviewFrameHeight.dp * scale
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(imageHeight),
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ui_rebuild_super_home_reference),
                    contentDescription = "UI重构版本总览页",
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier.fillMaxSize(),
                )
                hotspots.forEach { hotspot ->
                    Box(
                        modifier = Modifier
                            .offset(
                                x = hotspot.x.scaled(scale),
                                y = hotspot.y.scaled(scale),
                            )
                            .size(
                                width = hotspot.width.scaled(scale),
                                height = hotspot.height.scaled(scale),
                            )
                            .clickable(
                                interactionSource = interactionSource,
                                indication = null,
                                onClick = hotspot.onClick,
                            ),
                    )
                }
            }
        }
    }
}

private fun Float.scaled(scale: Float): Dp = (this * scale).dp
