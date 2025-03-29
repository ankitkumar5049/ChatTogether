package com.example.chattogether.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.chattogether.base.BaseViewModel
import com.example.chattogether.model.User
import com.google.firebase.firestore.FirebaseFirestore

class HomeViewModel(application: Application): BaseViewModel(application) {
    private val db = FirebaseFirestore.getInstance()
    private val _usersLiveData = MutableLiveData<List<User>>()
    val usersLiveData: LiveData<List<User>> get() = _usersLiveData

    fun fetchFirst20Users() {
        db.collection("users")
            .limit(20)
            .get()
            .addOnSuccessListener { snapshot ->
                val users = snapshot.documents.mapNotNull { it.toObject(User::class.java) }
                _usersLiveData.postValue(users)
                Log.d("DashboardViewModel", "Fetched ${users.size} users")
            }
            .addOnFailureListener { e ->
                Log.e("DashboardViewModel", "Error fetching users", e)
            }
    }

}