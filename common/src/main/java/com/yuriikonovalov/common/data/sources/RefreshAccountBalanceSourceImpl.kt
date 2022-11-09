package com.yuriikonovalov.common.data.sources

import com.yuriikonovalov.common.application.usecases.datasources.RefreshAccountBalanceSource
import com.yuriikonovalov.common.data.local.AccountsLocalDataSource
import com.yuriikonovalov.common.data.local.TransactionsLocalDataSource
import com.yuriikonovalov.common.data.local.TransfersLocalDataSource
import javax.inject.Inject

class RefreshAccountBalanceSourceImpl @Inject constructor(
    private val accountsLocalDataSource: AccountsLocalDataSource,
    private val transfersDataSourceLocal: TransfersLocalDataSource,
    private val transactionsLocalDataSource: TransactionsLocalDataSource
) : RefreshAccountBalanceSource {
    override suspend fun getInitialBalanceForAccount(accountId: Long): Double {
        return accountsLocalDataSource.getInitialBalance(accountId)
    }

    override suspend fun getTransferAmountForAccount(accountId: Long): Double {
        return transfersDataSourceLocal.getTransferAmountForAccount(accountId)
    }

    override suspend fun getTransactionAmountForAccount(accountId: Long): Double {
        return transactionsLocalDataSource.getTransactionAmountForAccount(accountId)
    }

    override suspend fun updateAccountBalance(accountId: Long, newBalance: Double) {
        accountsLocalDataSource.updateAccountBalance(accountId, newBalance)
    }
}