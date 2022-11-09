package com.yuriikonovalov.common.data.sources

import android.net.Uri
import com.yuriikonovalov.common.application.entities.transaction.Transaction
import com.yuriikonovalov.common.application.usecases.datasources.UpdateTransactionSource
import com.yuriikonovalov.common.data.local.TransactionImagesLocalDataSource
import com.yuriikonovalov.common.data.local.TransactionsLocalDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UpdateTransactionSourceImpl @Inject constructor(
    private val transactionsSourceLocal: TransactionsLocalDataSource,
    private val imagesSourceLocal: TransactionImagesLocalDataSource
) : UpdateTransactionSource {
    override fun getTransaction(transactionId: Long): Flow<Transaction?> {
        return transactionsSourceLocal.getTransactionById(transactionId)
    }

    override suspend fun updateTransaction(transaction: Transaction) {
        transactionsSourceLocal.updateTransaction(transaction)
    }

    override suspend fun saveTransactionImage(originImageUri: Uri): String? {
        return imagesSourceLocal.saveImage(originImageUri)
    }

    override suspend fun deleteTransactionImage(imageUri: Uri) {
        imagesSourceLocal.deleteImage(imageUri)
    }
}
