package com.v2ray.ang.ui.compose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import com.v2ray.ang.composeui.common.viewmodel.cryptoVpnViewModelFactory
import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.pages.p0.SplashRoute
import com.v2ray.ang.composeui.p0.repository.RealP0Repository
import com.v2ray.ang.composeui.p0.viewmodel.SplashViewModel
import com.v2ray.ang.composeui.theme.CryptoVpnTheme

class LaunchSplashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            CryptoVpnTheme {
                val repository = remember { RealP0Repository(applicationContext) }
                val viewModel: SplashViewModel = viewModel(
                    factory = cryptoVpnViewModelFactory { SplashViewModel(repository) },
                )
                SplashRoute(
                    viewModel = viewModel,
                    onFinished = {
                        startActivity(
                            ComposeContainerActivity.createIntent(
                                context = this,
                                startRoute = CryptoVpnRouteSpec.emailLogin.pattern,
                            ),
                        )
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                        finish()
                    },
                )
            }
        }
    }
}
