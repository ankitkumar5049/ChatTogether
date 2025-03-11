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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val userDao = UserDatabase.getDatabase(application).userDao()
    private val repository = UserRepository(userDao)

    // Sign up with Name, Phone, and Password
    fun signUp(name: String, email: String, phone: String, password: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val userId = auth.currentUser?.uid ?: ""
                        val user = hashMapOf(
                            "user_id" to userId,
                            "name" to name,
                            "email" to email,
                            "phone" to phone
                        )


                        // Save user details in Firestore
                        db.collection("users").document(userId).set(user)
                            .addOnSuccessListener {
                                onResult(true, "Sign-up successful!")
                                insertUser(
                                    User(name = name, email = email, password = password, phone = phone)
                                )

                                getUserByEmail(email) { fetchedUser ->
                                    if (fetchedUser != null) {
                                        Log.d("TAG", "signUp: $fetchedUser")
                                    } else {
                                        Log.d("TAG", "signUp: user not found")
                                    }
                                }
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
    fun login(email: String, password: String, onResult: (Boolean) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
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

    fun insertUser(user: User) {
        viewModelScope.launch {
            try {
                repository.insertUser(user)
            } catch (e: Exception) {
                Log.d("TAG", "insertUser: unable to save")
            }
        }
    }

    fun getUserByEmail(email: String, onResult: (User?) -> Unit) {
        viewModelScope.launch {
            val user = repository.getUserByEmail(email)
            onResult(user)
        }
    }
}
