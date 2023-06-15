package com.example.absensiassesment2mobpro.ui.dashboard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.absensiassesment2mobpro.room.AbsensiDatabase
import com.example.absensiassesment2mobpro.room.auth.AuthDao

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = AbsensiDatabase.getSaveInstance(application)?.authDao()

    fun getUserDetail(username: String) = dao?.getUserDetail(username)
}