package com.v2ray.ang.ui.compose

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.v2ray.ang.composeui.navigation.AppNavGraph
import androidx.navigation.compose.rememberNavController
import com.v2ray.ang.composeui.common.repository.RealCryptoVpnRepository
import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.p0.repository.RealP0Repository
import com.v2ray.ang.payment.data.repository.PaymentRepository
import com.v2ray.ang.composeui.theme.CryptoVpnTheme
import kotlinx.coroutines.launch

/**
 * Compose host for the full delivery UI replacement.
 */
class ComposeContainerActivity : ComponentActivity() {
    private val paymentRepository by lazy { PaymentRepository(applicationContext) }

    companion object {
        const val EXTRA_START_ROUTE = "compose_start_route"

        fun createIntent(
            context: Context,
            startRoute: String = CryptoVpnRouteSpec.splash.pattern,
        ): Intent = Intent(context, ComposeContainerActivity::class.java).apply {
            putExtra(EXTRA_START_ROUTE, startRoute)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val startRoute = intent.getStringExtra(EXTRA_START_ROUTE)
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
                        startDestination = startRoute,
                        p0Repository = p0Repository,
                        repository = repository,
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            paymentRepository.refreshCoreSnapshotsOnForeground(force = true)
        }
    }
}
