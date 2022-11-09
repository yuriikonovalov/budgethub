package com.yuriikonovalov.common.application.usecases.datasources

interface RefreshAccountBalanceSource {
    suspend fun getInitialBalanceForAccount(accountId: Long): Double
    suspend fun getTransferAmountForAccount(accountId: Long): Double
    suspend fun getTransactionAmountForAccount(accountId: Long): Double
    suspend fun updateAccountBalance(accountId: Long, newBalance: Double)
}