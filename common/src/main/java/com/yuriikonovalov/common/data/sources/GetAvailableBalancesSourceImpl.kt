package com.yuriikonovalov.common.data.sources

import com.yuriikonovalov.common.application.entities.account.Account
import com.yuriikonovalov.common.application.usecases.datasources.GetAvailableBalancesSource
import com.yuriikonovalov.common.data.local.AccountsLocalDataSource
import com.yuriikonovalov.common.data.local.TransactionsLocalDataSource
import com.yuriikonovalov.common.data.local.TransfersLocalDataSource
import kotlinx.coroutines.flow.Flow
import java.time.OffsetDateTime
import javax.inject.Inject

class GetAvailableBalancesSourceImpl @Inject constructor(
    private val accountsSourceLocal: AccountsLocalDataSource,
    private val transactionsSourceLocal: TransactionsLocalDataSource,
    private val transfersSourceLocal: TransfersLocalDataSource,
) : GetAvailableBalancesSource {

    override suspend fun getTransfersAmountChange(
        accountId: Long,
        endPeriod: OffsetDateTime
    ): Double {
        return transfersSourceLocal.getTransferAmountForAccountBeforeDate(
            accountId,
            endPeriod
        )
    }

    override suspend fun getTransactionsAmountChange(
        accountId: Long,
        endPeriod: OffsetDateTime
    ): Double {
        return transactionsSourceLocal.getTransactionAmountForAccountBeforeDate(
            accountId,
            endPeriod
        )
    }

    override suspend fun getInitialBalance(accountId: Long): Double {
        return accountsSourceLocal.getAccountById(accountId)?.initialBalance!!
    }

    override suspend fun getAccounts(): Flow<List<Account>> {
        return accountsSourceLocal.getAllAccounts()
    }

}