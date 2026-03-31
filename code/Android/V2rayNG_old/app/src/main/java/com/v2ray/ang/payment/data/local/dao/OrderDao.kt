package com.v2ray.ang.payment.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.v2ray.ang.payment.data.local.entity.OrderEntity

/**
 * 订单数据访问对象
 */
@Dao
interface OrderDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(order: OrderEntity)

    @Update
    suspend fun update(order: OrderEntity)

    @Query("SELECT * FROM orders WHERE orderNo = :orderNo LIMIT 1")
    suspend fun getByOrderNo(orderNo: String): OrderEntity?

    @Query("SELECT * FROM orders WHERE userId = :userId ORDER BY createdAt DESC")
    suspend fun getAllByUserId(userId: String): List<OrderEntity>

    @Query("SELECT * FROM orders WHERE userId = :userId AND status = :status ORDER BY createdAt DESC")
    suspend fun getByUserIdAndStatus(userId: String, status: String): List<OrderEntity>

    @Query("SELECT * FROM orders WHERE userId = :userId AND expiredAt > :currentTime ORDER BY expiredAt ASC")
    suspend fun getActiveOrders(userId: String, currentTime: Long = System.currentTimeMillis()): List<OrderEntity>

    @Query("SELECT * FROM orders WHERE userId = :userId AND expiredAt > :currentTime AND expiredAt <= :threshold ORDER BY expiredAt ASC")
    suspend fun getExpiringOrders(
        userId: String,
        currentTime: Long = System.currentTimeMillis(),
        threshold: Long
    ): List<OrderEntity>

    @Delete
    suspend fun delete(order: OrderEntity)

    @Query("DELETE FROM orders WHERE orderNo = :orderNo")
    suspend fun deleteByOrderNo(orderNo: String)

    @Query("DELETE FROM orders WHERE userId = :userId")
    suspend fun deleteByUserId(userId: String)
}
