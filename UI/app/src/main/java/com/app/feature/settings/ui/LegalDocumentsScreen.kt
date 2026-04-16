package com.app.feature.settings.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.common.components.*
import com.app.core.theme.AppDimens
import com.app.core.ui.AppScaffold
import com.app.feature.settings.viewmodel.SettingsViewModel


@Composable
fun LegalDocumentsScreen(
    viewModel: SettingsViewModel = viewModel(),
    onBack: () -> Unit = {},
    onOpenDoc: (String) -> Unit = {},
) {
    val state by viewModel.uiState.collectAsState()
    AppScaffold(title = "法务文档", onBack = onBack) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding).padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(state.legalDocuments) { doc -> GradientCard(title = doc.title, subtitle = doc.summary) { TextButton(onClick = { onOpenDoc(doc.id) }) { Text("阅读全文") } } }
        }
    }
}
