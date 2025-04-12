package com.example.chattogether.base

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.chattogether.db.UserDatabase
import com.example.chattogether.db.entities.User
import com.example.chattogether.db.repo.UserRepository
import com.example.chattogether.utils.Constant
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

open class BaseViewModel(application: Application): AndroidViewModel(application) {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val userDao = UserDatabase.getDatabase(application).userDao()
    private val repository = UserRepository(userDao)
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()


    fun insertUser(user: User) {
        viewModelScope.launch {
            try {
                repository.insertUser(user)
            } catch (e: Exception) {
                Log.d("TAG", "insertUser: unable to save")
            }
        }
    }

    fun getSavedUserDetails(onResult: (User?) -> Unit){
        viewModelScope.launch {
            val user = repository.getUserByUserId(auth.currentUser?.uid?:"")
            onResult(user)
        }
    }

    fun updateUserInFirebase(name: String, username: String, dob: String, onComplete: (Boolean) -> Unit) {
        val userId = auth.currentUser?.uid ?: return

        val updates = mapOf(
            "name" to name,
            "username" to username,
            "dob" to dob
        )

        firestore.collection("users").document(userId)
            .update(updates)
            .addOnSuccessListener {
                viewModelScope.launch {
                    repository.updateUserDetails(userId, name, username, dob)
                }
                onComplete(true)
            }
            .addOnFailureListener { onComplete(false) }
    }

    fun getUserDetails( onResult: (User?) -> Unit) {
        getSavedUserDetails { userLocal ->
            if(userLocal==null){
                getDataFromFirebase { user ->
                    if (user != null) {
                        insertUser(
                            User(userId = user.userId, name = user.name, username = user.username, dob = user.dob, password = user.password)
                        )
                        Log.d("TAG", "getUserDetails: fb user")
                        onResult(user)
                    }
                    else{
                        onResult(null)
                    }

                }
            }
            else{
                Log.d("TAG", "getUserDetails: local user")
                onResult(userLocal)
            }
            onResult(null)

        }

    }

    fun getDataFromFirebase(onResult: (User?) -> Unit){
        val userId = auth.currentUser?.uid?:""
        db.collection(Constant.USERS).document(userId).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val name = document.getString("name") ?: ""
                    val username = document.getString("username") ?: ""
                    val dob = document.getString("dob") ?: ""
                    val password = document.getString("password") ?: ""

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


    fun logout() {
        auth.signOut()
    }


}