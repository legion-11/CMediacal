package com.dmytroandriichuk.cmediacal.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.dmytroandriichuk.cmediacal.db.entity.Clinic
import com.dmytroandriichuk.cmediacal.db.entity.ClinicAndServicePrices
import kotlinx.coroutines.flow.Flow

@Dao
interface ClinicDao {
    @Insert
    suspend fun insert(clinic: Clinic)

    @Update
    suspend fun update(clinic: Clinic)

    @Delete
    suspend fun delete(clinic: Clinic)

    @Query("SELECT * FROM clinic WHERE userEmail = :userEmail")
    fun getAll(userEmail: String?): Flow<Array<Clinic>>

    @Transaction
    @Query("SELECT * FROM clinic WHERE userEmail= :userEmail")
    fun getAllClinicWithPrices(userEmail: String?): Flow<Array<ClinicAndServicePrices>>
}