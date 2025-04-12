package com.example.chattogether.viewmodel

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.example.chattogether.base.BaseViewModel
import com.example.chattogether.db.UserDatabase
import com.example.chattogether.db.repo.UserRepository
import com.example.chattogether.model.User
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class ProfileViewModel(application: Application): BaseViewModel(application) {
    private val userDao = UserDatabase.getDatabase(application).userDao()
    private val repository = UserRepository(userDao)

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    // Get user details from Firestore
    fun getUserFromFirebase(onResult: (User?) -> Unit) {
        val userId = auth.currentUser?.uid ?: return
        firestore.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val user = document.toObject(User::class.java)
                    onResult(user)
                } else {
                    onResult(null)
                }
            }
            .addOnFailureListener {
                onResult(null)
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

    fun changePassword(
        currentPassword: String,
        newPassword: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val user = FirebaseAuth.getInstance().currentUser
        val email = user?.email ?: return onError("User not found")

        val credential = EmailAuthProvider.getCredential(email, currentPassword)

        user.reauthenticate(credential)
            .addOnSuccessListener {
                user.updatePassword(newPassword)
                    .addOnSuccessListener { onSuccess() }
                    .addOnFailureListener { onError(it.localizedMessage ?: "Error updating password") }
            }
            .addOnFailureListener {
                onError("Current password is incorrect")
            }
    }



}