package com.yuriikonovalov.common.framework.data.local.database.backup

import android.annotation.SuppressLint
import java.io.File
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

object DatabaseCryptoFile {
    private const val ALGORITHM = "AES"
    private const val TRANSFORMATION = "AES"
    private const val CIPHER_KEY = "dbcipher"

    fun encrypt(inputFile: File, outputFile: File) {
        doCrypto(Cipher.ENCRYPT_MODE, inputFile, outputFile)
    }

    fun decrypt(inputFile: File, outputFile: File) {
        doCrypto(Cipher.DECRYPT_MODE, inputFile, outputFile)
    }

    @SuppressLint("GetInstance")
    private fun doCrypto(cipherMode: Int, inputFile: File, outputFile: File) {
        val salt = ByteArray(256)
        val keyChar = CIPHER_KEY.toCharArray()
        val pbKeySpec = PBEKeySpec(keyChar, salt, 1234, 128)
        val secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
        val keyBytes = secretKeyFactory.generateSecret(pbKeySpec).encoded

        val secretKey = SecretKeySpec(keyBytes, ALGORITHM)
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(cipherMode, secretKey)
        val inputStream = inputFile.inputStream()
        val inputBytes = inputStream.readBytes()

        val outputBytes = cipher.doFinal(inputBytes)
        val outputStream = outputFile.outputStream()
        outputStream.write(outputBytes)

        inputStream.close()
        outputStream.close()
    }
}