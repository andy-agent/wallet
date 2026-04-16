package com.app

import android.app.Application
import android.content.Context
import com.app.data.local.AppDatabase
import com.app.data.local.prefs.AppPreferences
import com.app.data.local.prefs.VpnPreferences
import com.app.data.local.prefs.WalletPreferences
import com.app.data.remote.mock.MockAuthDataSource
import com.app.data.remote.mock.MockMarketDataSource
import com.app.data.remote.mock.MockVpnDataSource
import com.app.data.remote.mock.MockWalletDataSource
import com.app.data.repository.AuthRepository
import com.app.data.repository.AuthRepositoryImpl
import com.app.data.repository.MarketRepository
import com.app.data.repository.MarketRepositoryImpl
import com.app.data.repository.SettingsRepository
import com.app.data.repository.SettingsRepositoryImpl
import com.app.data.repository.VpnRepository
import com.app.data.repository.VpnRepositoryImpl
import com.app.data.repository.WalletRepository
import com.app.data.repository.WalletRepositoryImpl
import com.app.vpncore.manager.VpnManager
import com.app.vpncore.parser.VpnParser
import com.app.vpncore.storage.VpnStorage

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        AppGraph.init(this)
    }
}

object AppGraph {
    private lateinit var appContext: Context

    fun init(context: Context) {
        if (!::appContext.isInitialized) {
            appContext = context.applicationContext
        }
    }

    private val database by lazy { AppDatabase.create() }
    private val appPreferences by lazy { AppPreferences(appContext) }
    private val vpnPreferences by lazy { VpnPreferences(appContext) }
    private val walletPreferences by lazy { WalletPreferences(appContext) }

    private val authRemote by lazy { MockAuthDataSource(appPreferences) }
    private val marketRemote by lazy { MockMarketDataSource() }
    private val walletRemote by lazy { MockWalletDataSource() }
    private val vpnRemote by lazy { MockVpnDataSource() }

    val vpnStorage by lazy { VpnStorage(vpnPreferences) }
    val vpnParser by lazy { VpnParser() }
    val vpnManager by lazy { VpnManager(vpnStorage, vpnParser) }

    val authRepository: AuthRepository by lazy { AuthRepositoryImpl(authRemote, appPreferences) }
    val marketRepository: MarketRepository by lazy {
        MarketRepositoryImpl(database.marketDao, marketRemote)
    }
    val walletRepository: WalletRepository by lazy {
        WalletRepositoryImpl(database.assetDao, database.transactionDao, walletRemote, walletPreferences)
    }
    val vpnRepository: VpnRepository by lazy {
        VpnRepositoryImpl(database.orderDao, database.vpnNodeDao, vpnRemote, vpnManager, vpnStorage)
    }
    val settingsRepository: SettingsRepository by lazy {
        SettingsRepositoryImpl(walletRemote, appPreferences)
    }
}
