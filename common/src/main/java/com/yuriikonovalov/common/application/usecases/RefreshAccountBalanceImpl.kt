package com.yuriikonovalov.common.application.usecases

import com.yuriikonovalov.common.application.Resource
import com.yuriikonovalov.common.application.usecases.datasources.RefreshAccountBalanceSource
import javax.inject.Inject

class RefreshAccountBalanceImpl @Inject constructor(private val source: RefreshAccountBalanceSource) :
    RefreshAccountBalance {
    override suspend operator fun invoke(accountId: Long): Resource<Unit> {
        return try {
            val initialBalance = source.getInitialBalanceForAccount(accountId)
            val transactionAmount = source.getTransactionAmountForAccount(accountId)
            val transferAmount = source.getTransferAmountForAccount(accountId)
            val newBalance = initialBalance + transactionAmount + transferAmount
            source.updateAccountBalance(accountId, newBalance)
            Resource.unit()
        } catch (e: Exception) {
            Resource.Failure(e)
        }
    }
}