package com.example.chattogether.utils

import android.content.Context
import android.util.Base64
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

object SecureStorageUtil {
    private const val PREFS_NAME = "chat_prefs"

    fun saveSecretKeyForChat(chatId: String, key: SecretKey, context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val encodedKey = Base64.encodeToString(key.encoded, Base64.DEFAULT)
        prefs.edit().putString("key_$chatId", encodedKey).apply()
    }

    fun getSecretKeyForChat(chatId: String, context: Context): SecretKey {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val encodedKey = prefs.getString("key_$chatId", null) ?: return EncryptionUtil.getSecretKey()
        val decodedKey = Base64.decode(encodedKey, Base64.DEFAULT)
        return SecretKeySpec(decodedKey, 0, decodedKey.size, "AES")
    }
}
