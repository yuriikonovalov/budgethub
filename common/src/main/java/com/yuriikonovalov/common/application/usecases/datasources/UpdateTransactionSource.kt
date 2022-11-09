package com.yuriikonovalov.common.application.usecases.datasources

import android.net.Uri
import com.yuriikonovalov.common.application.entities.transaction.Transaction
import kotlinx.coroutines.flow.Flow

interface UpdateTransactionSource {
    fun getTransaction(transactionId: Long): Flow<Transaction?>
    suspend fun updateTransaction(transaction: Transaction)
    suspend fun saveTransactionImage(originImageUri: Uri): String?
    suspend fun deleteTransactionImage(imageUri: Uri)
}