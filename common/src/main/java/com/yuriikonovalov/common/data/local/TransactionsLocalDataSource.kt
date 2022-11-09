package com.yuriikonovalov.common.data.local

import androidx.paging.PagingData
import com.yuriikonovalov.common.application.entities.transaction.Transaction
import kotlinx.coroutines.flow.Flow
import java.time.OffsetDateTime

interface TransactionsLocalDataSource {
    suspend fun saveTransaction(transaction: Transaction): Long
    suspend fun deleteAll()
    suspend fun deleteTransactionById(transactionId: Long)
    suspend fun updateTransaction(transaction: Transaction)
    fun getTransactionById(id: Long): Flow<Transaction?>

    fun getTransactionsForPeriod(
        startPeriod: OffsetDateTime?,
        endPeriod: OffsetDateTime?
    ): Flow<List<Transaction>>

    suspend fun getTransactionAmountForAccount(accountId: Long): Double
    suspend fun getTransactionAmountForAccountBeforeDate(
        accountId: Long,
        endPeriod: OffsetDateTime
    ): Double

    fun getIncomeTransactionsForPeriod(
        startPeriod: OffsetDateTime?,
        endPeriod: OffsetDateTime?
    ): Flow<List<Transaction>>

    fun getExpenseTransactionsForPeriod(
        startPeriod: OffsetDateTime?,
        endPeriod: OffsetDateTime?
    ): Flow<List<Transaction>>


    fun getTransactionsPaged(
        pageSize: Int,
        startPeriod: OffsetDateTime?,
        endPeriod: OffsetDateTime?
    ): Flow<PagingData<Transaction>>
}