package com.app.feature.vpn.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.common.components.*
import com.app.common.widgets.*
import com.app.core.theme.AppDimens
import com.app.core.ui.AppScaffold
import com.app.feature.vpn.components.*
import com.app.feature.vpn.viewmodel.VpnViewModel
import com.app.vpncore.model.VpnState
import com.app.core.utils.Formatters


@Composable
fun OrderResultScreen(
    orderId: String,
    onDone: () -> Unit = {},
) {
    AppScaffold(title = "结果页") { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(18.dp), verticalArrangement = Arrangement.Center) {
            GradientCard(title = "套餐已生效", subtitle = orderId) {
                Text("订单支付完成，已可前往订阅页刷新节点并开始连接。")
                Spacer(Modifier.height(16.dp))
                PrimaryButton(text = "进入 VPN", onClick = onDone)
            }
        }
    }
}
