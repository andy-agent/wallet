package com.v2ray.ang.ui.compose.debug

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.v2ray.ang.composeui.common.repository.RealCryptoVpnRepository
import com.v2ray.ang.composeui.navigation.AppNavGraph
import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.p0.repository.RealP0Repository
import com.v2ray.ang.composeui.theme.CryptoVpnTheme

/**
 * Debug-only route harness for adb-driven screenshot automation.
 */
class ComposeRouteHarnessActivity : ComponentActivity() {
    companion object {
        const val EXTRA_ROUTE = "route"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val route = intent.getStringExtra(EXTRA_ROUTE)
            ?.trim()
            ?.takeIf { it.isNotEmpty() }
            ?: CryptoVpnRouteSpec.splash.pattern

        setContent {
            CryptoVpnTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    val navController = rememberNavController()
                    val p0Repository = remember { RealP0Repository(applicationContext) }
                    val repository = remember { RealCryptoVpnRepository(applicationContext) }
                    AppNavGraph(
                        navController = navController,
                        startDestination = route,
                        p0Repository = p0Repository,
                        repository = repository,
                    )
                }
            }
        }
    }
}
