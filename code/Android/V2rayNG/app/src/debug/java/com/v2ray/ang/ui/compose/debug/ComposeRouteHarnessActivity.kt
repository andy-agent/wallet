package com.v2ray.ang.ui.compose.debug

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.ui.compose.ComposeContainerActivity

/**
 * Debug-only route harness for adb-driven screenshot automation.
 */
class ComposeRouteHarnessActivity : ComponentActivity() {
    companion object {
        const val EXTRA_ROUTE = "route"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val route = intent.getStringExtra(EXTRA_ROUTE)
            ?.trim()
            ?.takeIf { it.isNotEmpty() }
            ?: CryptoVpnRouteSpec.splash.pattern

        startActivity(
            ComposeContainerActivity.createIntent(
                context = this,
                startRoute = route,
            ),
        )
        Toast.makeText(this, "Opening route: $route", Toast.LENGTH_SHORT).show()
        finish()
    }
}
