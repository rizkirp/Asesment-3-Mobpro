package com.example.absensiassesment2mobpro.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.absensiassesment2mobpro.room.AbsensiDatabase
import com.example.absensiassesment2mobpro.room.auth.Auth
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    val dao = AbsensiDatabase.getSaveInstance(application)?.authDao()

    fun login(username: String) = flow {
        emit(dao?.login(username))
    }

    fun isExist(username: String) = flow {
        dao?.isExist(username)?.let { emit(it) }
    }

    fun register(auth: Auth) {
        viewModelScope.launch {
            dao?.register(auth)
        }
    }
}