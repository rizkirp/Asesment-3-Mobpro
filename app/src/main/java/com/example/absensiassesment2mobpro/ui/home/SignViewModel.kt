package com.example.absensiassesment2mobpro.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.absensiassesment2mobpro.room.AbsensiDatabase
import com.example.absensiassesment2mobpro.room.absensi.Absensi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class SignViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = AbsensiDatabase.getSaveInstance(application)?.absensiDao()
    private val authDao = AbsensiDatabase.getSaveInstance(application)?.authDao()

    fun allAbsensi() = dao?.allAbsensi()

    fun getUser(username: String) = flow {
        emit(authDao?.getUser(username))
    }

    fun deleteAbsensi(absensi: Absensi) {
        viewModelScope.launch {
            dao?.deleteAbsensi(absensi)
        }
    }
}