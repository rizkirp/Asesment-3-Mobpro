package com.example.absensiassesment2mobpro.room.absensi

import androidx.room.ColumnInfo
import androidx.room.ColumnInfo.Companion.INTEGER
import androidx.room.ColumnInfo.Companion.TEXT
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Absensi(
    @PrimaryKey()
    @ColumnInfo(name = "id", typeAffinity = INTEGER)
    val id: Int,
    @ColumnInfo(name = "id_user", typeAffinity = TEXT)
    val idUser: String,
    @ColumnInfo(name = "full_name", typeAffinity = TEXT)
    val fullName: String,
    @ColumnInfo(name = "desc", typeAffinity = TEXT)
    val desc: String,
    @ColumnInfo(name = "time", typeAffinity = TEXT)
    val time: String,
    @ColumnInfo(name = "date", typeAffinity = TEXT)
    val date: String
)