package com.dmytroandriichuk.cmediacal

import android.app.Application
import com.dmytroandriichuk.cmediacal.db.AppDatabase
import com.dmytroandriichuk.cmediacal.db.DatabaseRepository

class CMedicalApplication: Application() {
    private val database by lazy { AppDatabase.getDatabase(this) }
    val repository by lazy { DatabaseRepository(database.clinicDao(), database.servicePriceDao()) }
}