package com.v2ray.ang.ui.compose

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.v2ray.ang.composeui.bridge.auth.ComposeAuthBridge
import com.v2ray.ang.composeui.navigation.AppNavGraph
import com.v2ray.ang.composeui.navigation.DeepLinkHandler
import com.v2ray.ang.composeui.navigation.LegacyDestination
import com.v2ray.ang.composeui.navigation.Routes
import com.v2ray.ang.composeui.pages.splash.ComposeUpdateBridge
import com.v2ray.ang.ui.AboutActivity
import com.v2ray.ang.ui.SettingsActivity
import com.v2ray.ang.util.Utils

/**
 * ComposeContainerActivity serves as the Compose host for the migrated vpnui routes.
 *
 * Route stack, back behavior, and legacy fallbacks are delegated to AppNavGraph.
 */
class ComposeContainerActivity : ComponentActivity() {
    companion object {
        const val EXTRA_START_ROUTE = "compose_start_route"
        const val EXTRA_FORCE_UPDATE = "compose_force_update"

        fun createIntent(
            context: Context,
            startRoute: String = Routes.SPLASH,
            forceUpdate: Boolean = false,
        ): Intent = Intent(context, ComposeContainerActivity::class.java).apply {
            putExtra(EXTRA_START_ROUTE, startRoute)
            putExtra(EXTRA_FORCE_UPDATE, forceUpdate)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val deepLinkRoute = when (val parsed = DeepLinkHandler().parse(intent)) {
            is DeepLinkHandler.DeepLinkType.Navigation -> parsed.route
            else -> null
        }
        val startRoute = Routes.normalize(intent.getStringExtra(EXTRA_START_ROUTE) ?: deepLinkRoute)
        val forceUpdate = intent.getBooleanExtra(EXTRA_FORCE_UPDATE, false)
        val authBridge = ComposeAuthBridge(this)
        val updateBridge = ComposeUpdateBridge(forceUpdateFromIntent = forceUpdate)

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    AppNavGraph(
                        authBridge = authBridge,
                        updateBridge = updateBridge,
                        startDestination = startRoute,
                        onOpenUrl = { url -> Utils.openUri(this@ComposeContainerActivity, url) },
                        onOpenLegacyDestination = ::openLegacyDestination,
                        onExitApp = { finish() },
                        onAuthSuccess = {},
                    )
                }
            }
        }
    }

    private fun openLegacyDestination(destination: LegacyDestination) {
        when (destination) {
            LegacyDestination.SETTINGS -> {
                startActivity(Intent(this, SettingsActivity::class.java))
            }

            LegacyDestination.ABOUT -> {
                startActivity(Intent(this, AboutActivity::class.java))
            }

            LegacyDestination.SUPPORT -> {
                Utils.openUri(this, "mailto:support@cryptovpn.app")
            }
        }
    }
}
