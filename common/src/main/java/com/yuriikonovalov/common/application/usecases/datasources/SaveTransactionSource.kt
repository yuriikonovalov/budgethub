package com.yuriikonovalov.common.application.usecases.datasources

import android.net.Uri
import com.yuriikonovalov.common.application.entities.account.Account
import com.yuriikonovalov.common.application.entities.transaction.Transaction

interface SaveTransactionSource {
    suspend fun updateAccount(account: Account)
    suspend fun saveTransaction(transaction: Transaction): Long
    suspend fun saveTransactionImage(originImageUri: Uri): String?
}