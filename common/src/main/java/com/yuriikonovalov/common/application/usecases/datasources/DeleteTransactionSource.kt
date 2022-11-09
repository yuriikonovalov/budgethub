package com.yuriikonovalov.common.application.usecases.datasources

import android.net.Uri
import com.yuriikonovalov.common.application.entities.account.Account
import com.yuriikonovalov.common.application.entities.transaction.Transaction
import kotlinx.coroutines.flow.Flow

interface DeleteTransactionSource {
    suspend fun deleteTransaction(transactionId: Long)
    fun getTransaction(transactionId: Long): Flow<Transaction?>
    suspend fun deleteTransactionImage(imageUri: Uri)
    suspend fun updateAccount(account: Account)
}