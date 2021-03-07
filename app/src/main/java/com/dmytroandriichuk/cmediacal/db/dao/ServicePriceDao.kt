package com.dmytroandriichuk.cmediacal.db.dao

import androidx.room.*
import com.dmytroandriichuk.cmediacal.db.entity.ServicePrice
import kotlinx.coroutines.flow.Flow

@Dao
interface ServicePriceDao {
    @Insert
    suspend fun insert(servicePrice: ServicePrice)

    @Update
    suspend fun update(servicePrice: ServicePrice)

    @Delete
    suspend fun delete(servicePrice: ServicePrice)
}