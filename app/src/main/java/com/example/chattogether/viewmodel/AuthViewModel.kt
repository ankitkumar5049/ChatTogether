package com.example.chattogether.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

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
}
