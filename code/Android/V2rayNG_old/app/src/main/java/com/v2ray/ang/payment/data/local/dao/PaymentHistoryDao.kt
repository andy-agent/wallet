package com.v2ray.ang.payment.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.v2ray.ang.payment.data.local.entity.PaymentHistoryEntity

/**
 * 支付历史数据访问对象
 */
@Dao
interface PaymentHistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(paymentHistory: PaymentHistoryEntity)

    @Query("SELECT * FROM payment_history WHERE orderNo = :orderNo ORDER BY paidAt DESC")
    suspend fun getByOrderNo(orderNo: String): List<PaymentHistoryEntity>

    @Query("SELECT * FROM payment_history ORDER BY paidAt DESC")
    suspend fun getAll(): List<PaymentHistoryEntity>

    @Query("SELECT * FROM payment_history WHERE orderNo IN (SELECT orderNo FROM orders WHERE userId = :userId) ORDER BY paidAt DESC")
    suspend fun getAllByUserId(userId: String): List<PaymentHistoryEntity>

    @Delete
    suspend fun delete(paymentHistory: PaymentHistoryEntity)

    @Query("DELETE FROM payment_history WHERE orderNo = :orderNo")
    suspend fun deleteByOrderNo(orderNo: String)

    @Query("DELETE FROM payment_history")
    suspend fun deleteAll()
}
