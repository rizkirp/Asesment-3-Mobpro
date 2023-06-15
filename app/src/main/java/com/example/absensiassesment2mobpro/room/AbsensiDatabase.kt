package com.example.absensiassesment2mobpro.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.absensiassesment2mobpro.room.absensi.Absensi
import com.example.absensiassesment2mobpro.room.absensi.AbsensiDao
import com.example.absensiassesment2mobpro.room.auth.Auth
import com.example.absensiassesment2mobpro.room.auth.AuthDao

@Database(entities = [Auth::class, Absensi::class], version = 1, exportSchema = false)
abstract class AbsensiDatabase : RoomDatabase() {
    abstract fun authDao(): AuthDao
    abstract fun absensiDao(): AbsensiDao

    companion object {
        private var INSTANCE: AbsensiDatabase? = null
        fun getSaveInstance(context: Context?): AbsensiDatabase? {
            INSTANCE ?: synchronized(this) {
                INSTANCE =
                    Room.databaseBuilder(
                        context!!,
                        AbsensiDatabase::class.java,
                        "absensi_database.db"
                    )
                        .build()
            }
            return INSTANCE
        }
    }
}