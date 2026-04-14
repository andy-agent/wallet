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
import com.v2ray.ang.payment.data.local.dao.VpnNodeCacheDao
import com.v2ray.ang.payment.data.local.dao.VpnNodeRuntimeDao
import com.v2ray.ang.payment.data.local.entity.OrderEntity
import com.v2ray.ang.payment.data.local.entity.PaymentHistoryEntity
import com.v2ray.ang.payment.data.local.entity.UserEntity
import com.v2ray.ang.payment.data.local.entity.VpnNodeCacheEntity
import com.v2ray.ang.payment.data.local.entity.VpnNodeRuntimeEntity

/**
 * Room数据库 - 支付模块
 */
@Database(
    entities = [
        UserEntity::class,
        OrderEntity::class,
        PaymentHistoryEntity::class,
        VpnNodeCacheEntity::class,
        VpnNodeRuntimeEntity::class,
    ],
    version = 4,
    exportSchema = false
)
abstract class PaymentDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun orderDao(): OrderDao
    abstract fun paymentHistoryDao(): PaymentHistoryDao
    abstract fun vpnNodeCacheDao(): VpnNodeCacheDao
    abstract fun vpnNodeRuntimeDao(): VpnNodeRuntimeDao

    companion object {
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
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
