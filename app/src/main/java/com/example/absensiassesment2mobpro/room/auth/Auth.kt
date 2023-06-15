package com.example.absensiassesment2mobpro.room.auth

import androidx.room.ColumnInfo
import androidx.room.ColumnInfo.Companion.INTEGER
import androidx.room.ColumnInfo.Companion.TEXT
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Auth(
    @PrimaryKey
    @ColumnInfo(name = "username", typeAffinity = TEXT)
    val username: String,
    @ColumnInfo(name = "password", typeAffinity = TEXT)
    val password: String,
    @ColumnInfo(name = "full_name", typeAffinity = TEXT)
    val fullName: String,
    @ColumnInfo(name = "birthdate", typeAffinity = INTEGER)
    val birthdate: Long,
    @ColumnInfo(name = "gender", typeAffinity = INTEGER)
    val gender: Int
)
