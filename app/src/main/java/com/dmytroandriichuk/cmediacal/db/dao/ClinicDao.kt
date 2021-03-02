package com.dmytroandriichuk.cmediacal.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.dmytroandriichuk.cmediacal.db.entity.Clinic

@Dao
interface ClinicDao {
    @Insert
    suspend fun insert(clinic: Clinic)

    @Update
    suspend fun update(clinic: Clinic)

//    @Query("DELETE FROM clinic WHERE id = :clinicId AND userEmail = :userEmail")
//    suspend fun delete(clinicId: String, userEmail: String?)

    @Delete
    suspend fun delete(clinic: Clinic)

    @Query("SELECT * FROM clinic WHERE userEmail = :userEmail")
    fun getAll(userEmail: String?): LiveData<Clinic?>

}