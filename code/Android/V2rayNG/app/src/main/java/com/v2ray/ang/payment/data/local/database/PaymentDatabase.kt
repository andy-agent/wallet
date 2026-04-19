package com.v2ray.ang.payment.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.v2ray.ang.payment.data.local.dao.OrderDao
import com.v2ray.ang.payment.data.local.dao.PaymentHistoryDao
import com.v2ray.ang.payment.data.local.dao.UserDao
import com.v2ray.ang.payment.data.local.dao.LocalWalletChainAccountDao
import com.v2ray.ang.payment.data.local.dao.LocalCustomTokenDao
import com.v2ray.ang.payment.data.local.dao.LocalTokenIconCacheDao
import com.v2ray.ang.payment.data.local.dao.LocalTokenVisibilityEntryDao
import com.v2ray.ang.payment.data.local.dao.LocalWalletDao
import com.v2ray.ang.payment.data.local.dao.VpnNodeCacheDao
import com.v2ray.ang.payment.data.local.dao.VpnNodeRuntimeDao
import com.v2ray.ang.payment.data.local.dao.WalletOverviewCacheDao
import com.v2ray.ang.payment.data.local.dao.WalletPublicAddressCacheDao
import com.v2ray.ang.payment.data.local.dao.WalletReceiveContextCacheDao
import com.v2ray.ang.payment.data.local.entity.LocalWalletChainAccountEntity
import com.v2ray.ang.payment.data.local.entity.LocalCustomTokenEntity
import com.v2ray.ang.payment.data.local.entity.LocalTokenIconCacheEntity
import com.v2ray.ang.payment.data.local.entity.LocalTokenVisibilityEntryEntity
import com.v2ray.ang.payment.data.local.entity.LocalWalletEntity
import com.v2ray.ang.payment.data.local.entity.OrderEntity
import com.v2ray.ang.payment.data.local.entity.PaymentHistoryEntity
import com.v2ray.ang.payment.data.local.entity.UserEntity
import com.v2ray.ang.payment.data.local.entity.VpnNodeCacheEntity
import com.v2ray.ang.payment.data.local.entity.VpnNodeRuntimeEntity
import com.v2ray.ang.payment.data.local.entity.WalletOverviewCacheEntity
import com.v2ray.ang.payment.data.local.entity.WalletPublicAddressCacheEntity
import com.v2ray.ang.payment.data.local.entity.WalletReceiveContextCacheEntity

/**
 * Room数据库 - 支付模块
 */
@Database(
    entities = [
        UserEntity::class,
        OrderEntity::class,
        PaymentHistoryEntity::class,
        LocalWalletEntity::class,
        LocalWalletChainAccountEntity::class,
        LocalTokenVisibilityEntryEntity::class,
        LocalCustomTokenEntity::class,
        LocalTokenIconCacheEntity::class,
        VpnNodeCacheEntity::class,
        VpnNodeRuntimeEntity::class,
        WalletPublicAddressCacheEntity::class,
        WalletReceiveContextCacheEntity::class,
        WalletOverviewCacheEntity::class,
    ],
    version = 7,
    exportSchema = false
)
abstract class PaymentDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun orderDao(): OrderDao
    abstract fun paymentHistoryDao(): PaymentHistoryDao
    abstract fun localWalletDao(): LocalWalletDao
    abstract fun localWalletChainAccountDao(): LocalWalletChainAccountDao
    abstract fun localTokenVisibilityEntryDao(): LocalTokenVisibilityEntryDao
    abstract fun localCustomTokenDao(): LocalCustomTokenDao
    abstract fun localTokenIconCacheDao(): LocalTokenIconCacheDao
    abstract fun vpnNodeCacheDao(): VpnNodeCacheDao
    abstract fun vpnNodeRuntimeDao(): VpnNodeRuntimeDao
    abstract fun walletPublicAddressCacheDao(): WalletPublicAddressCacheDao
    abstract fun walletReceiveContextCacheDao(): WalletReceiveContextCacheDao
    abstract fun walletOverviewCacheDao(): WalletOverviewCacheDao

    companion object {
        private val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `wallet_public_address_cache` (
                        `userId` TEXT NOT NULL,
                        `addressId` TEXT NOT NULL,
                        `accountId` TEXT NOT NULL,
                        `networkCode` TEXT NOT NULL,
                        `assetCode` TEXT NOT NULL,
                        `address` TEXT NOT NULL,
                        `isDefault` INTEGER NOT NULL,
                        `createdAt` TEXT NOT NULL,
                        `updatedAt` INTEGER NOT NULL,
                        PRIMARY KEY(`userId`, `addressId`),
                        FOREIGN KEY(`userId`) REFERENCES `users`(`userId`) ON UPDATE NO ACTION ON DELETE CASCADE
                    )
                    """.trimIndent()
                )
                database.execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_wallet_public_address_cache_userId` ON `wallet_public_address_cache` (`userId`)"
                )
                database.execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_wallet_public_address_cache_networkCode` ON `wallet_public_address_cache` (`networkCode`)"
                )
                database.execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_wallet_public_address_cache_assetCode` ON `wallet_public_address_cache` (`assetCode`)"
                )

                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `wallet_receive_context_cache` (
                        `userId` TEXT NOT NULL,
                        `requestNetworkCode` TEXT NOT NULL,
                        `requestAssetCode` TEXT NOT NULL,
                        `selectedNetworkCode` TEXT NOT NULL,
                        `selectedAssetCode` TEXT NOT NULL,
                        `chainItemsJson` TEXT NOT NULL,
                        `assetItemsJson` TEXT NOT NULL,
                        `defaultAddress` TEXT,
                        `canShare` INTEGER NOT NULL,
                        `walletExists` INTEGER NOT NULL,
                        `receiveState` TEXT,
                        `status` TEXT NOT NULL,
                        `note` TEXT NOT NULL,
                        `shareText` TEXT,
                        `updatedAt` INTEGER NOT NULL,
                        PRIMARY KEY(`userId`, `requestNetworkCode`, `requestAssetCode`),
                        FOREIGN KEY(`userId`) REFERENCES `users`(`userId`) ON UPDATE NO ACTION ON DELETE CASCADE
                    )
                    """.trimIndent()
                )
                database.execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_wallet_receive_context_cache_userId` ON `wallet_receive_context_cache` (`userId`)"
                )
                database.execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_wallet_receive_context_cache_selectedNetworkCode` ON `wallet_receive_context_cache` (`selectedNetworkCode`)"
                )
                database.execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_wallet_receive_context_cache_selectedAssetCode` ON `wallet_receive_context_cache` (`selectedAssetCode`)"
                )

                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `wallet_overview_cache` (
                        `userId` TEXT NOT NULL,
                        `accountId` TEXT NOT NULL,
                        `accountEmail` TEXT NOT NULL,
                        `selectedNetworkCode` TEXT NOT NULL,
                        `chainItemsJson` TEXT NOT NULL,
                        `assetItemsJson` TEXT NOT NULL,
                        `alertsJson` TEXT NOT NULL,
                        `updatedAt` INTEGER NOT NULL,
                        PRIMARY KEY(`userId`),
                        FOREIGN KEY(`userId`) REFERENCES `users`(`userId`) ON UPDATE NO ACTION ON DELETE CASCADE
                    )
                    """.trimIndent()
                )
                database.execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_wallet_overview_cache_userId` ON `wallet_overview_cache` (`userId`)"
                )
            }
        }

        private val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `local_token_visibility_entries` (
                        `userId` TEXT NOT NULL,
                        `walletId` TEXT NOT NULL,
                        `chainId` TEXT NOT NULL,
                        `tokenKey` TEXT NOT NULL,
                        `visibilityState` TEXT NOT NULL,
                        `updatedAt` INTEGER NOT NULL,
                        PRIMARY KEY(`userId`, `walletId`, `chainId`, `tokenKey`),
                        FOREIGN KEY(`userId`) REFERENCES `users`(`userId`) ON UPDATE NO ACTION ON DELETE CASCADE
                    )
                    """.trimIndent(),
                )
                database.execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_local_token_visibility_entries_userId` ON `local_token_visibility_entries` (`userId`)",
                )
                database.execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_local_token_visibility_entries_walletId` ON `local_token_visibility_entries` (`walletId`)",
                )
                database.execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_local_token_visibility_entries_chainId` ON `local_token_visibility_entries` (`chainId`)",
                )
                database.execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_local_token_visibility_entries_visibilityState` ON `local_token_visibility_entries` (`visibilityState`)",
                )

                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `local_custom_tokens` (
                        `userId` TEXT NOT NULL,
                        `customTokenId` TEXT NOT NULL,
                        `walletId` TEXT NOT NULL,
                        `chainId` TEXT NOT NULL,
                        `tokenAddress` TEXT NOT NULL,
                        `tokenKey` TEXT NOT NULL,
                        `name` TEXT NOT NULL,
                        `symbol` TEXT NOT NULL,
                        `decimals` INTEGER NOT NULL,
                        `iconUrl` TEXT,
                        `createdAt` TEXT NOT NULL,
                        `updatedAt` TEXT NOT NULL,
                        `cachedAt` INTEGER NOT NULL,
                        PRIMARY KEY(`userId`, `customTokenId`),
                        FOREIGN KEY(`userId`) REFERENCES `users`(`userId`) ON UPDATE NO ACTION ON DELETE CASCADE
                    )
                    """.trimIndent(),
                )
                database.execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_local_custom_tokens_userId` ON `local_custom_tokens` (`userId`)",
                )
                database.execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_local_custom_tokens_walletId` ON `local_custom_tokens` (`walletId`)",
                )
                database.execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_local_custom_tokens_chainId` ON `local_custom_tokens` (`chainId`)",
                )
                database.execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_local_custom_tokens_tokenKey` ON `local_custom_tokens` (`tokenKey`)",
                )

                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `local_token_icon_cache` (
                        `tokenKey` TEXT NOT NULL,
                        `iconUrl` TEXT,
                        `localPath` TEXT,
                        `updatedAt` INTEGER NOT NULL,
                        `lastFetchSucceeded` INTEGER NOT NULL,
                        PRIMARY KEY(`tokenKey`)
                    )
                    """.trimIndent(),
                )
                database.execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_local_token_icon_cache_updatedAt` ON `local_token_icon_cache` (`updatedAt`)",
                )
                database.execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_local_token_icon_cache_lastFetchSucceeded` ON `local_token_icon_cache` (`lastFetchSucceeded`)",
                )
            }
        }

        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `vpn_node_cache` (
                        `userId` TEXT NOT NULL,
                        `nodeId` TEXT NOT NULL,
                        `nodeName` TEXT NOT NULL,
                        `lineCode` TEXT NOT NULL,
                        `lineName` TEXT NOT NULL,
                        `regionCode` TEXT NOT NULL,
                        `regionName` TEXT NOT NULL,
                        `host` TEXT NOT NULL,
                        `port` INTEGER NOT NULL,
                        `status` TEXT NOT NULL,
                        `source` TEXT NOT NULL,
                        `remark` TEXT,
                        `updatedAt` INTEGER NOT NULL,
                        PRIMARY KEY(`userId`, `nodeId`),
                        FOREIGN KEY(`userId`) REFERENCES `users`(`userId`) ON UPDATE NO ACTION ON DELETE CASCADE
                    )
                    """.trimIndent()
                )
                database.execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_vpn_node_cache_userId` ON `vpn_node_cache` (`userId`)"
                )
                database.execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_vpn_node_cache_lineCode` ON `vpn_node_cache` (`lineCode`)"
                )
                database.execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_vpn_node_cache_regionCode` ON `vpn_node_cache` (`regionCode`)"
                )

                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `vpn_node_runtime` (
                        `userId` TEXT NOT NULL,
                        `nodeId` TEXT NOT NULL,
                        `lineCode` TEXT NOT NULL,
                        `healthStatus` TEXT NOT NULL,
                        `pingMs` INTEGER,
                        `selected` INTEGER NOT NULL,
                        `lastSeenAt` INTEGER NOT NULL,
                        PRIMARY KEY(`userId`, `nodeId`)
                    )
                    """.trimIndent()
                )
                database.execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_vpn_node_runtime_userId` ON `vpn_node_runtime` (`userId`)"
                )
                database.execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_vpn_node_runtime_lineCode` ON `vpn_node_runtime` (`lineCode`)"
                )
            }
        }

        @Volatile
        private var INSTANCE: PaymentDatabase? = null

        fun getDatabase(context: Context): PaymentDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PaymentDatabase::class.java,
                    "payment_database"
                )
                    .addMigrations(MIGRATION_3_4)
                    .addMigrations(MIGRATION_4_5)
                    .addMigrations(MIGRATION_6_7)
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
