package com.example.chattogether.utils

import android.util.Base64
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object EncryptionUtil {
    private const val ALGORITHM = "AES"
    private const val TRANSFORMATION = "AES/CBC/PKCS5Padding"

    private const val STATIC_KEY_BASE64 = "bW9ja2tleWZvcmVuY3J5cHRpb24xMjM=" // 16-byte key

//    fun getSecretKey(): SecretKey {
//        val decodedKey = Base64.decode(STATIC_KEY_BASE64, Base64.DEFAULT)
//
//        // Ensure key length is valid for AES
//        val correctSizeKey = decodedKey.copyOf(16) // Trim or pad to 16 bytes
//        return SecretKeySpec(correctSizeKey, "AES")
//    }

//     Generate a Secret Key (should be stored securely, e.g., Keystore)
    fun getSecretKey(): SecretKey {
        val keyGenerator = KeyGenerator.getInstance(ALGORITHM)
        keyGenerator.init(256)
        return keyGenerator.generateKey()
    }

    // Encrypt message
    fun encryptMessage(secretKey: SecretKey, message: String): String {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        val iv = ByteArray(16) { 0 } // Using static IV (Change for more security)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, IvParameterSpec(iv))
        val encryptedBytes = cipher.doFinal(message.toByteArray(Charsets.UTF_8))
        return Base64.encodeToString(encryptedBytes, Base64.DEFAULT)
    }

    fun decryptMessage(secretKey: SecretKey, encryptedMessage: String): String {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        val iv = ByteArray(16) { 0 } // Ensure IV size is correct
        cipher.init(Cipher.DECRYPT_MODE, secretKey, IvParameterSpec(iv))
        val decodedBytes = Base64.decode(encryptedMessage, Base64.DEFAULT)
        val decryptedBytes = cipher.doFinal(decodedBytes)
        return String(decryptedBytes, Charsets.UTF_8)
    }
}
