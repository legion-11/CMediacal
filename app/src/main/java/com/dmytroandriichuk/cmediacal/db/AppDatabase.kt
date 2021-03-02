package com.dmytroandriichuk.cmediacal.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.dmytroandriichuk.cmediacal.db.dao.ClinicDao
import com.dmytroandriichuk.cmediacal.db.entity.Clinic


@Database(
    entities = [Clinic::class],
    version = 1
)
//create singleton of db
abstract class AppDatabase : RoomDatabase() {

    abstract fun clinicDao(): ClinicDao

    companion object {

        @Volatile private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "com.cmedical.database"
                ).build()

                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}