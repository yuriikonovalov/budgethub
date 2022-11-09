package com.yuriikonovalov.shared_test.fakes.datasource

import androidx.paging.PagingData
import com.yuriikonovalov.common.application.entities.transaction.Transaction
import com.yuriikonovalov.common.application.entities.transaction.TransactionType
import com.yuriikonovalov.common.data.local.TransactionsLocalDataSource
import kotlinx.coroutines.flow.*
import java.time.OffsetDateTime

class FakeTransactionsLocalDataSource : TransactionsLocalDataSource {
    private val transactions = mutableListOf<Transaction>()
    private val transactionFlow = MutableStateFlow(transactions)

    private var transactionById: Transaction? = null

    suspend fun setData(data: List<Transaction>) {
        transactions.clear()
        transactions.addAll(data)
        transactionFlow.emit(transactions)
    }

    override suspend fun saveTransaction(transaction: Transaction): Long {
        if (transactions.contains(transaction)) {
            transactions.remove(transaction)
        }
        transactions.add(transaction)
        transactionFlow.emit(transactions)
        return transaction.id
    }

    override suspend fun deleteAll() {
        transactions.clear()
        transactionFlow.emit(transactions)
    }

    override suspend fun deleteTransactionById(transactionId: Long) {
        val deleted = transactions.removeIf { it.id == transactionId }
        if (deleted) {
            transactionFlow.emit(transactions)
        }
    }

    override suspend fun updateTransaction(transaction: Transaction) {
        transactions.removeIf { it.id == transaction.id }.also { success ->
            if (success) {
                transactions.add(transaction)
                transactionFlow.emit(transactions)
            }
        }
    }

    override fun getTransactionById(id: Long): Flow<Transaction?> {
//        return transactionFlow.map { it.find { it.id == id } }
        return flowOf(transactionById?.copy(id = id))
    }

    override fun getTransactionsForPeriod(
        startPeriod: OffsetDateTime?,
        endPeriod: OffsetDateTime?,
    ): Flow<List<Transaction>> {
        return when {
            startPeriod != null && endPeriod != null -> {
                transactionFlow.map { list ->
                    list.filter {
                        it.date.isAfter(startPeriod) && it.date.isBefore(endPeriod)
                    }
                }
            }
            startPeriod == null && endPeriod != null -> {
                transactionFlow.map { list ->
                    list.filter {
                        it.date.isBefore(endPeriod)
                    }
                }
            }
            startPeriod != null && endPeriod == null -> {
                transactionFlow.map { list ->
                    list.filter {
                        it.date.isAfter(startPeriod)
                    }
                }
            }
            else -> transactionFlow
        }
    }

    override suspend fun getTransactionAmountForAccount(accountId: Long): Double {
        val list = transactions.filter { it.account.id == accountId }
        val expenses = list
            .filter { it.type == TransactionType.EXPENSE }
            .sumOf { it.amount }
        val incomes = list
            .filter { it.type == TransactionType.INCOME }
            .sumOf { it.amount }
        return incomes - expenses
    }

    override suspend fun getTransactionAmountForAccountBeforeDate(
        accountId: Long,
        endPeriod: OffsetDateTime,
    ): Double {
        val list =
            transactions.filter { it.account.id == accountId && it.date.isBefore(endPeriod) }
        val expenses = list
            .filter { it.type == TransactionType.EXPENSE }
            .sumOf { it.amount }
        val incomes = list
            .filter { it.type == TransactionType.INCOME }
            .sumOf { it.amount }
        return incomes - expenses
    }

    override fun getIncomeTransactionsForPeriod(
        startPeriod: OffsetDateTime?,
        endPeriod: OffsetDateTime?,
    ): Flow<List<Transaction>> {
        return when {
            startPeriod != null && endPeriod != null -> {
                transactionFlow.map { list ->
                    list.filter {
                        it.date.isAfter(startPeriod) && it.date.isBefore(endPeriod)
                    }
                }
            }
            startPeriod == null && endPeriod != null -> {
                transactionFlow.map { list ->
                    list.filter {
                        it.date.isBefore(endPeriod)
                    }
                }
            }
            startPeriod != null && endPeriod == null -> {
                transactionFlow.map { list ->
                    list.filter {
                        it.date.isAfter(startPeriod)
                    }
                }
            }
            else -> transactionFlow
        }.map { list ->
            list.filter { it.type == TransactionType.INCOME }
        }
    }

    override fun getExpenseTransactionsForPeriod(
        startPeriod: OffsetDateTime?,
        endPeriod: OffsetDateTime?,
    ): Flow<List<Transaction>> {
        return when {
            startPeriod != null && endPeriod != null -> {
                transactionFlow.map { list ->
                    list.filter {
                        it.date.isAfter(startPeriod) && it.date.isBefore(endPeriod)
                    }
                }
            }
            startPeriod == null && endPeriod != null -> {
                transactionFlow.map { list ->
                    list.filter {
                        it.date.isBefore(endPeriod)
                    }
                }
            }
            startPeriod != null && endPeriod == null -> {
                transactionFlow.map { list ->
                    list.filter {
                        it.date.isAfter(startPeriod)
                    }
                }
            }
            else -> transactionFlow
        }.map { list ->
            list.filter { it.type == TransactionType.EXPENSE }
        }
    }

    override fun getTransactionsPaged(
        pageSize: Int,
        startPeriod: OffsetDateTime?,
        endPeriod: OffsetDateTime?,
    ): Flow<PagingData<Transaction>> {
        TODO("Not yet implemented")
    }
}