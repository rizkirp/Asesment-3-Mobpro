package com.example.absensiassesment2mobpro.room.auth

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface AuthDao {

    @Insert
    suspend fun register(auth: Auth)

    @Query("SELECT EXISTS (SELECT * FROM auth WHERE username  =:username)")
    suspend fun isExist(username: String): Boolean

    @Query("SELECT password FROM auth WHERE username  =:username")
    suspend fun login(username: String): String

    @Query("SELECT gender FROM auth WHERE username =:username")
    suspend fun getUser(username: String): Int

    @Query("SELECT * FROM auth WHERE username =:username")
    fun getUserDetail(username: String): LiveData<Auth>
}