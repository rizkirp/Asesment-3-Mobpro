package com.example.absensiassesment2mobpro.ui.home.add

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.absensiassesment2mobpro.room.AbsensiDatabase
import com.example.absensiassesment2mobpro.room.absensi.Absensi
import kotlinx.coroutines.launch

class AddSignViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AbsensiDatabase.getSaveInstance(application)?.absensiDao()
    private val authDao = AbsensiDatabase.getSaveInstance(application)?.authDao()

    fun absensi(absensi: Absensi) {
        viewModelScope.launch {
            dao?.absensi(absensi)
        }
    }

    fun getUserDetail(username: String) = authDao?.getUserDetail(username)
}