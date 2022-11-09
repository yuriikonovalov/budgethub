package com.yuriikonovalov.common.data.sources

import android.net.Uri
import com.yuriikonovalov.common.application.entities.account.Account
import com.yuriikonovalov.common.application.entities.transaction.Transaction
import com.yuriikonovalov.common.application.usecases.datasources.DeleteTransactionSource
import com.yuriikonovalov.common.data.local.AccountsLocalDataSource
import com.yuriikonovalov.common.data.local.TransactionImagesLocalDataSource
import com.yuriikonovalov.common.data.local.TransactionsLocalDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DeleteTransactionSourceImpl @Inject constructor(
    private val transactionsSourceLocal: TransactionsLocalDataSource,
    private val imagesSourceLocal: TransactionImagesLocalDataSource,
    private val accountsSourceLocal: AccountsLocalDataSource
) : DeleteTransactionSource {
    override suspend fun deleteTransaction(transactionId: Long) {
        transactionsSourceLocal.deleteTransactionById(transactionId)
    }

    override fun getTransaction(transactionId: Long): Flow<Transaction?> {
        return transactionsSourceLocal.getTransactionById(transactionId)
    }

    override suspend fun deleteTransactionImage(imageUri: Uri) {
        imagesSourceLocal.deleteImage(imageUri)
    }

    override suspend fun updateAccount(account: Account) {
        accountsSourceLocal.updateAccount(account)
    }
}