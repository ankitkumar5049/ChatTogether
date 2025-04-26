package com.example.chattogether.viewmodel

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.chattogether.base.BaseViewModel
import com.example.chattogether.domain.RetrofitInstance
import com.example.chattogether.dto.HuggingFaceRequest
import com.example.chattogether.model.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class HomeViewModel(application: Application): BaseViewModel(application) {
    private val db = FirebaseFirestore.getInstance()
    private val _usersLiveData = MutableLiveData<List<User>>()
    val usersLiveData: LiveData<List<User>> get() = _usersLiveData

    private val _isLoading = MutableLiveData(true)
    val isLoading: LiveData<Boolean> get() = _isLoading

    var conversationHistory by mutableStateOf("")
        private set

    fun fetchFirst20Users() {
        db.collection("users")
            .limit(20)
            .get()
            .addOnSuccessListener { snapshot ->
                val users = snapshot.documents.mapNotNull { it.toObject(User::class.java) }
                _usersLiveData.postValue(users)
                _isLoading.postValue(false)
                Log.d("DashboardViewModel", "Fetched ${users.size} users")
            }
            .addOnFailureListener { e ->
                Log.e("DashboardViewModel", "Error fetching users", e)
            }
    }


    fun updateConversationHistory(message: String) {
        conversationHistory += message
    }

    fun talkToChatBot(){
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.queryModel(
                    HuggingFaceRequest(inputs = "What are you doing today?")
                )
                Log.d("BotReply", "Bot says: ${response}")
            } catch (e: Exception) {
                Log.e("BotReply", "Error: ${e.message}")
            }
        }

    }

}