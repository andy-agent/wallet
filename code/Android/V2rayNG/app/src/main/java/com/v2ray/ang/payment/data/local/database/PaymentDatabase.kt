package com.v2ray.ang.payment.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.v2ray.ang.payment.data.local.dao.OrderDao
import com.v2ray.ang.payment.data.local.dao.PaymentHistoryDao
import com.v2ray.ang.payment.data.local.dao.UserDao
import com.v2ray.ang.payment.data.local.entity.OrderEntity
import com.v2ray.ang.payment.data.local.entity.PaymentHistoryEntity
import com.v2ray.ang.payment.data.local.entity.UserEntity

/**
 * Room数据库 - 支付模块
 */
@Database(
    entities = [
        UserEntity::class,
        OrderEntity::class,
        PaymentHistoryEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class PaymentDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun orderDao(): OrderDao
    abstract fun paymentHistoryDao(): PaymentHistoryDao

    companion object {
        @Volatile
        private var INSTANCE: PaymentDatabase? = null

        fun getDatabase(context: Context): PaymentDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PaymentDatabase::class.java,
                    "payment_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
