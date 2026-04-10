package com.v2ray.ang.composeui.p0.repository

import com.v2ray.ang.composeui.p0.model.LoginResult
import com.v2ray.ang.composeui.p0.model.LoginUiState
import com.v2ray.ang.composeui.p0.model.SplashUiState
import com.v2ray.ang.composeui.p0.model.VpnHomeUiState
import com.v2ray.ang.composeui.p0.model.WalletHomeUiState
import com.v2ray.ang.composeui.p0.model.WalletOnboardingUiState

interface P0Repository {
    suspend fun getSplashState(): SplashUiState
    suspend fun getLoginSeed(): LoginUiState
    suspend fun login(email: String, password: String): LoginResult
    suspend fun getWalletOnboardingState(): WalletOnboardingUiState
    suspend fun getVpnHomeState(): VpnHomeUiState
    suspend fun getWalletHomeState(): WalletHomeUiState
}
