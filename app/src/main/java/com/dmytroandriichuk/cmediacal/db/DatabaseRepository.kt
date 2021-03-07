package com.dmytroandriichuk.cmediacal.db

import com.dmytroandriichuk.cmediacal.db.dao.ClinicDao
import com.dmytroandriichuk.cmediacal.db.dao.ServicePriceDao
import com.dmytroandriichuk.cmediacal.db.entity.Clinic
import com.dmytroandriichuk.cmediacal.db.entity.ClinicAndServicePrices
import com.dmytroandriichuk.cmediacal.db.entity.ServicePrice
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class DatabaseRepository(private val clinicDao: ClinicDao, private val servicePriceDao: ServicePriceDao) {

    fun insert(clinic: Clinic) {
        CoroutineScope(Dispatchers.IO).launch { clinicDao.insert(clinic) }
    }

    fun update(clinic: Clinic) {
        CoroutineScope(Dispatchers.IO).launch { clinicDao.update(clinic) }
    }

    fun delete(clinic: Clinic) {
        CoroutineScope(Dispatchers.IO).launch { clinicDao.delete(clinic) }
    }

    fun getAllClinics(userEmail: String?): Flow<Array<Clinic>> {
        return clinicDao.getAll(userEmail)
    }

    fun getAllClinicsWithPrices(userEmail: String?): Flow<Array<ClinicAndServicePrices>> {
        return clinicDao.getAllClinicWithPrices(userEmail)
    }

    fun insert(servicePrice: ServicePrice) {
        CoroutineScope(Dispatchers.IO).launch { servicePriceDao.insert(servicePrice) }
    }

    fun update(servicePrice: ServicePrice) {
        CoroutineScope(Dispatchers.IO).launch { servicePriceDao.update(servicePrice) }
    }

    fun delete(servicePrice: ServicePrice) {
        CoroutineScope(Dispatchers.IO).launch { servicePriceDao.delete(servicePrice) }
    }
}