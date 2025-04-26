package com.example.chattogether.utils

import android.util.Base64
import com.example.chattogether.utils.Constant.SEC
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

object AESHelper {

    private fun getKeySpec(): SecretKeySpec {
        return SecretKeySpec(SEC.toByteArray(), "AES")
    }

    fun encrypt(input: String): String {
        val cipher = Cipher.getInstance("AES")
        cipher.init(Cipher.ENCRYPT_MODE, getKeySpec())
        val encrypted = cipher.doFinal(input.toByteArray())
        return Base64.encodeToString(encrypted, Base64.DEFAULT)
    }

    fun decrypt(encrypted: String): String {
        val cipher = Cipher.getInstance("AES")
        cipher.init(Cipher.DECRYPT_MODE, getKeySpec())
        val decodedBytes = Base64.decode(encrypted, Base64.DEFAULT)
        val decrypted = cipher.doFinal(decodedBytes)
        return String(decrypted)
    }
}
