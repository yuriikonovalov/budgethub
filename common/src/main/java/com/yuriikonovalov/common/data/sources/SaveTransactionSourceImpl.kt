package com.yuriikonovalov.common.data.sources

import android.net.Uri
import com.yuriikonovalov.common.application.entities.account.Account
import com.yuriikonovalov.common.application.entities.transaction.Transaction
import com.yuriikonovalov.common.application.usecases.datasources.SaveTransactionSource
import com.yuriikonovalov.common.data.local.AccountsLocalDataSource
import com.yuriikonovalov.common.data.local.TransactionImagesLocalDataSource
import com.yuriikonovalov.common.data.local.TransactionsLocalDataSource
import javax.inject.Inject

class SaveTransactionSourceImpl @Inject constructor(
    private val accountsSourceLocal: AccountsLocalDataSource,
    private val transactionsSourceLocal: TransactionsLocalDataSource,
    private val transactionImagesSourceLocal: TransactionImagesLocalDataSource
) : SaveTransactionSource {
    override suspend fun updateAccount(account: Account) {
        accountsSourceLocal.updateAccount(account)
    }

    override suspend fun saveTransaction(transaction: Transaction): Long {
        return transactionsSourceLocal.saveTransaction(transaction)
    }

    override suspend fun saveTransactionImage(originImageUri: Uri): String? {
        return transactionImagesSourceLocal.saveImage(originImageUri)
    }
}