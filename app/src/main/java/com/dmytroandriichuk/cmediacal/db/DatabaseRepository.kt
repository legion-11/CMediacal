package com.dmytroandriichuk.cmediacal.db

import com.dmytroandriichuk.cmediacal.db.dao.ClinicDao
import com.dmytroandriichuk.cmediacal.db.entity.Clinic
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DatabaseRepository(private val clinicDao: ClinicDao) {


    fun insert(clinic: Clinic) {
        CoroutineScope(Dispatchers.IO).launch { clinicDao.insert(clinic) }
    }
    fun update(clinic: Clinic) {
        CoroutineScope(Dispatchers.IO).launch { clinicDao.update(clinic) }
    }

    fun delete(clinic: Clinic) {
        CoroutineScope(Dispatchers.IO).launch { clinicDao.delete(clinic) }
    }

    fun getAllClinics(userEmail: String?) {
        CoroutineScope(Dispatchers.IO).launch { clinicDao.getAll(userEmail) }
    }
}