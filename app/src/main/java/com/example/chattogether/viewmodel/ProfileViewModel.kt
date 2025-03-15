package com.example.chattogether.viewmodel

import android.app.Application
import com.example.chattogether.base.BaseViewModel
import com.example.chattogether.db.UserDatabase
import com.example.chattogether.db.repo.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileViewModel(application: Application): BaseViewModel(application) {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val userDao = UserDatabase.getDatabase(application).userDao()
    private val repository = UserRepository(userDao)
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()



}