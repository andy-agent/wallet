package com.v2ray.ang.ui.compose

import android.os.Bundle
import androidx.activity.compose.BackHandler
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.v2ray.ang.R
import com.v2ray.ang.composeui.bridge.auth.ComposeAuthBridge
import com.v2ray.ang.composeui.navigation.AppNavGraph
import com.v2ray.ang.composeui.pages.splash.ComposeUpdateBridge
import com.v2ray.ang.util.Utils

/**
 * ComposeContainerActivity serves as a container for Jetpack Compose screens.
 *
 * This activity provides a minimal Compose runtime environment with Material3 theming,
 * serving as the entry point for vpnui pages migration.
 *
 * Currently displays a placeholder content and can be extended to host actual Compose screens.
 */
class ComposeContainerActivity : ComponentActivity() {
    companion object {
        const val EXTRA_START_ROUTE = "compose_start_route"
        const val EXTRA_FORCE_UPDATE = "compose_force_update"
    }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val startRoute = intent.getStringExtra(EXTRA_START_ROUTE)
        val forceUpdate = intent.getBooleanExtra(EXTRA_FORCE_UPDATE, false)
        val authBridge = ComposeAuthBridge(this)
        val updateBridge = ComposeUpdateBridge(forceUpdateFromIntent = forceUpdate)

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold(
                        topBar = {
                            TopAppBar(
                                title = { Text(text = stringResource(R.string.app_name)) },
                                navigationIcon = {
                                    IconButton(onClick = { finish() }) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                            contentDescription = stringResource(android.R.string.cancel)
                                        )
                                    }
                                },
                                colors = TopAppBarDefaults.topAppBarColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            )
                        }
                    ) { innerPadding ->
                        BackHandler { finish() }
                        AppNavGraph(
                            authBridge = authBridge,
                            updateBridge = updateBridge,
                            startDestination = startRoute ?: com.v2ray.ang.composeui.navigation.Routes.SPLASH,
                            onOpenUrl = { url -> Utils.openUri(this@ComposeContainerActivity, url) },
                            onExitApp = { finish() },
                            onAuthSuccess = { finish() },
                        )
                    }
                }
            }
        }
    }
}
