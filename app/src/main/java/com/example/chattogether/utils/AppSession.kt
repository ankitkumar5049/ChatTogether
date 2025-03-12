package com.example.chattogether.utils

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Build
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.google.gson.Gson
import java.io.File

object AppSession {
    private lateinit var sharedPref: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private const val KEY_TIMESTAMP = "timestamp"
    private var EXPIRY_DURATION_MS = 5 * 60 * 1000 // 10 mins

    fun initialize(context: Context) {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        sharedPref = EncryptedSharedPreferences.create(
            context,
            Constant.PREFERENCE_FILE_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

        editor = sharedPref.edit()
    }

    fun put(key: String, value: String) {
        editor.putString(key, value)
            .apply()
    }

    fun put(key: String, value: Boolean) {
        editor.putBoolean(key, value)
            .apply()
    }

    fun getBoolean(key: String): Boolean {
        return sharedPref.getBoolean(key, false)
    }

    fun getString(key: String): String? {
        return sharedPref.getString(key, null)
    }

    fun clear() {
        editor.clear().commit()
    }

    fun putObject(key: String?, obj: Any?) {
        val gson = Gson()
        putString(key, gson.toJson(obj))
    }

    fun putString(key: String?, value: String?) {
        sharedPref.edit().putString(key, value).apply()
    }

    fun putInt(key: String?, value: Int?) {
        sharedPref.edit().putInt(key, value ?: 0).apply()
    }

    fun getInt(key: String?): Int {
        return sharedPref.getInt(key, 0)
    }

    fun getObject(key: String?, classOfT: Class<*>?): Any? {
        val json = getString(key!!)
        return Gson().fromJson(json, classOfT)
    }

    fun remove(key: String) {
        editor.remove(key).commit()
    }

    fun putStringForLimitedTime(key: String, value: String) {
        val currentTime = System.currentTimeMillis()
        putString(key, value)
        putLong(KEY_TIMESTAMP, currentTime)
    }

    fun getStringForLimitedTime(key: String): String? {
        val value = getString(key)
        val timestamp = getLong(KEY_TIMESTAMP, 0)

        if (value != null && System.currentTimeMillis() - timestamp > EXPIRY_DURATION_MS) {
            clearValue(key)
            return null
        }

        return value
    }

    private fun clearValue(key: String) {
        remove(key)
        remove(KEY_TIMESTAMP)
    }

    fun putLong(key: String, value: Long) {
        editor.putLong(key, value).apply()
    }

    fun getLong(key: String, defaultValue: Long = 0L): Long {
        return sharedPref.getLong(key, defaultValue)
    }

    fun deleteSharedPreferences(context: Context, name: String): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return context.deleteSharedPreferences(name)
        } else {
            context.getSharedPreferences(name, MODE_PRIVATE).edit().clear().commit()
            val dir = File(context.applicationInfo.dataDir, "shared_prefs")
            return File(dir, name).delete()
        }
    }
}