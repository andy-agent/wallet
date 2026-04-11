package com.v2ray.ang.composeui.p0.repository

import com.v2ray.ang.composeui.p0.model.CodeRequestResult
import com.v2ray.ang.composeui.p0.model.LoginResult
import com.v2ray.ang.composeui.p0.model.LoginUiState
import com.v2ray.ang.composeui.p0.model.SplashUiState
import com.v2ray.ang.composeui.p0.model.SubmitResult
import com.v2ray.ang.composeui.p0.model.VpnHomeUiState
import com.v2ray.ang.composeui.p0.model.WalletHomeUiState
import com.v2ray.ang.composeui.p0.model.WalletCreationMode
import com.v2ray.ang.composeui.p0.model.WalletOnboardingUiState

interface P0Repository {
    suspend fun getSplashState(): SplashUiState
    suspend fun getLoginSeed(): LoginUiState
    suspend fun login(email: String, password: String): LoginResult
    suspend fun requestRegisterCode(email: String): CodeRequestResult
    suspend fun register(email: String, password: String, code: String): SubmitResult
    suspend fun requestResetCode(email: String): CodeRequestResult
    suspend fun resetPassword(email: String, code: String, newPassword: String): SubmitResult
    suspend fun getWalletOnboardingState(selectedMode: WalletCreationMode? = null): WalletOnboardingUiState
    suspend fun getVpnHomeState(selectedRegionCode: String? = null): VpnHomeUiState
    suspend fun getWalletHomeState(selectedChainId: String? = null): WalletHomeUiState
}
