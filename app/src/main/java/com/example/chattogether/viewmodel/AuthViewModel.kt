package com.example.chattogether.viewmodel

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.example.chattogether.base.BaseViewModel
import com.example.chattogether.db.UserDatabase
import com.example.chattogether.db.entities.User
import com.example.chattogether.db.repo.UserRepository
import com.example.chattogether.utils.Constant
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import java.time.LocalDate

class AuthViewModel(application: Application) : BaseViewModel(application) {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val userDao = UserDatabase.getDatabase(application).userDao()
    private val repository = UserRepository(userDao)

    // Sign up with Name, Phone, and Password
    fun signUp(name: String, username_: String, dob: String, password: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            var username = username_
            if(username_.takeLast(4)!=".com") {
                username = username_ + Constant.MAIL_EXTENSION
            }
            auth.createUserWithEmailAndPassword(username, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val userId = auth.currentUser?.uid ?: ""
                        val user = hashMapOf(
                            "user_id" to userId,
                            "name" to name,
                            "username" to username,
                            "dob" to dob,
                            "createdAt" to LocalDate.now().toString(),
                            "userDeleted" to 0
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

    fun checkValidation(name: String, dob: String, password: String, username: String, confirmPassword: String): Boolean{
        return !(name.trim().isEmpty() || dob.trim().isEmpty() || password.trim().isEmpty()|| username.trim().isEmpty()||confirmPassword.isEmpty() || confirmPassword!=password)
    }

    fun checkValidation(username: String, password: String): Boolean{
        return !(username.trim().isEmpty() || password.trim().isEmpty())
    }


}
