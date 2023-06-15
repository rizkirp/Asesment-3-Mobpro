package com.example.absensiassesment2mobpro.room.absensi

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface AbsensiDao {
    @Insert
    suspend fun absensi(absensi: Absensi)

    @Query("SELECT * FROM absensi")
    fun allAbsensi(): LiveData<List<Absensi>>

    @Delete
    suspend fun deleteAbsensi(absensi: Absensi)
}