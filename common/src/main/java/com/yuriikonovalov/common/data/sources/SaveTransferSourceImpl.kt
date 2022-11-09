package com.yuriikonovalov.common.data.sources

import com.yuriikonovalov.common.application.entities.Transfer
import com.yuriikonovalov.common.application.entities.account.Account
import com.yuriikonovalov.common.application.usecases.datasources.SaveTransferSource
import com.yuriikonovalov.common.data.local.AccountsLocalDataSource
import com.yuriikonovalov.common.data.local.TransfersLocalDataSource
import javax.inject.Inject

class SaveTransferSourceImpl @Inject constructor(
    private val accountsSourceLocal: AccountsLocalDataSource,
    private val transferTransactionsSourceLocal: TransfersLocalDataSource
) : SaveTransferSource {
    override suspend fun saveTransferTransaction(transaction: Transfer): Long {
        return transferTransactionsSourceLocal.saveTransfer(transaction)
    }

    override suspend fun updateAccount(account: Account) {
        accountsSourceLocal.updateAccount(account)
    }
}