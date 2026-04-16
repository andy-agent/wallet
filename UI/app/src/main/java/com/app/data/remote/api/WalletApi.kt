package com.app.data.remote.api

import com.app.data.remote.dto.AssetDto

interface WalletApi {
    suspend fun getAssets(): List<AssetDto>
}
