package com.yuriikonovalov.common.application.usecases.datasources

import com.yuriikonovalov.common.application.entities.account.Account
import kotlinx.coroutines.flow.Flow
import java.time.OffsetDateTime

interface GetAvailableBalancesSource {
    suspend fun getTransfersAmountChange(accountId: Long, endPeriod: OffsetDateTime): Double
    suspend fun getTransactionsAmountChange(accountId: Long, endPeriod: OffsetDateTime): Double
    suspend fun getInitialBalance(accountId: Long): Double
    suspend fun getAccounts(): Flow<List<Account>>
}