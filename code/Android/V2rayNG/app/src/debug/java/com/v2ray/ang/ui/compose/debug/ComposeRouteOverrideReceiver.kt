package com.v2ray.ang.ui.compose.debug

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class ComposeRouteOverrideReceiver : BroadcastReceiver() {
    companion object {
        private const val PREFS_NAME = "debug_compose_route_override"
        private const val KEY_ROUTE = "route"
        private const val EXTRA_ROUTE = "route"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val route = intent.getStringExtra(EXTRA_ROUTE)
            ?.trim()
            ?.takeIf { it.isNotEmpty() }

        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .apply {
                if (route == null) {
                    remove(KEY_ROUTE)
                } else {
                    putString(KEY_ROUTE, route)
                }
            }
            .apply()

        Toast.makeText(
            context,
            if (route == null) "Compose route override cleared" else "Compose route override set: $route",
            Toast.LENGTH_SHORT,
        ).show()
    }
}
