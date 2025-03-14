package com.example.chattogether.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.chattogether.db.UserDatabase
import com.example.chattogether.db.entities.User
import com.example.chattogether.db.repo.UserRepository
import com.example.chattogether.utils.AppSession
import com.example.chattogether.utils.Constant
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class ProfileViewModel(application: Application): AndroidViewModel(application) {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val userDao = UserDatabase.getDatabase(application).userDao()
    private val repository = UserRepository(userDao)
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()


    fun getUserDetails( onResult: (User?) -> Unit) {
        var userId = AppSession.getString("userId")?:""
        if(userId==""){
            userId = auth.currentUser!!.uid
        }
        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val name = document.getString("name") ?: ""
                    val username = document.getString("username") ?: ""
                    val dob = document.getString("dob") ?: ""
                    val password = document.getString("password") ?: "" // If you saved password (not recommended for security reasons)

                    val user = User(
                        userId=userId,
                        name = name,
                        username = username,
                        dob = dob,
                        password = password
                    )
                    onResult(user)
                } else {
                    onResult(null) // User not found
                }
            }
            .addOnFailureListener { e ->
                Log.e("TAG", "Error fetching user details: ${e.message}")
                onResult(null)
            }
    }


    fun getUserByUsername(username: String, onResult: (User?) -> Unit) {
        viewModelScope.launch {
            val user = repository.getUserByUsername(username)
            onResult(user)
        }
    }

    fun logout() {
        AppSession.remove(Constant.USERS_LIST)
        auth.signOut()
    }


}