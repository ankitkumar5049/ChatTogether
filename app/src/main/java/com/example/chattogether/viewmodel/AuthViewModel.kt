package com.example.chattogether.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chattogether.db.UserDatabase
import com.example.chattogether.db.entities.User
import com.example.chattogether.db.repo.UserRepository
import com.example.chattogether.utils.AppSession
import com.example.chattogether.utils.Constant
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val userDao = UserDatabase.getDatabase(application).userDao()
    private val repository = UserRepository(userDao)

    // Sign up with Name, Phone, and Password
    fun signUp(name: String, username_: String, dob: String, password: String, onResult: (Boolean, String) -> Unit) {
        AppSession.putString(Constant.USERNAME, username_)
        AppSession.putString(Constant.PASSWORD, password)
        viewModelScope.launch {
            var username = username_
            if(username_.takeLast(4)!=".com") {
                username = username_ + Constant.MAIL_EXTENSION
            }
            auth.createUserWithEmailAndPassword(username, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val userId = auth.currentUser?.uid ?: ""
                        AppSession.putString("userId", userId)
                        val user = hashMapOf(
                            "user_id" to userId,
                            "name" to name,
                            "username" to username,
                            "dob" to dob
                        )


                        // Save user details in Firestore
                        db.collection(Constant.USERS).document(userId).set(user)
                            .addOnSuccessListener {
                                onResult(true, "Sign-up successful!")
                                insertUser(
                                    User(userId = userId, name = name, username = username, password = password, dob = dob)
                                )
                            }
                            .addOnFailureListener { e ->
                                onResult(false, "Failed to save user: ${e.message}")
                            }
                    } else {
                        onResult(false, "Sign-up failed: ${task.exception?.message}")
                    }
                }
        }
    }

    // Login with Phone and Password
    fun login(username_: String, password: String, onResult: (Boolean) -> Unit) {
        AppSession.putString(Constant.USERNAME, username_)
        AppSession.putString(Constant.PASSWORD, password)
        var username = username_
        if(username_.takeLast(4)!=".com") {
            username = username_ + Constant.MAIL_EXTENSION
        }
        auth.signInWithEmailAndPassword(username, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult(true)  // Success, navigate to Dashboard
                } else {
                    onResult(false)  // Failed, show error
                }
            }
    }

    fun checkValidation(name: String, phone: String, password: String, email: String, confirmPassword: String): Boolean{
        return !(name.isEmpty() || phone.isEmpty() || password.isEmpty()|| confirmPassword.isEmpty() || confirmPassword!=password)
    }

    fun checkValidation(id: String, password: String): Boolean{
        return !(id.isEmpty() || password.isEmpty())
    }

    // Logout
    fun logout() {
        auth.signOut()
    }

    private fun insertUser(user: User) {
        viewModelScope.launch {
            try {
                repository.insertUser(user)
            } catch (e: Exception) {
                Log.d("TAG", "insertUser: unable to save")
            }
        }
    }

}
