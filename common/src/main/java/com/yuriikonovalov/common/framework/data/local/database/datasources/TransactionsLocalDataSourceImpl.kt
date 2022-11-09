package com.yuriikonovalov.common.framework.data.local.database.datasources

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.yuriikonovalov.common.application.entities.transaction.Transaction
import com.yuriikonovalov.common.application.entities.transaction.TransactionType
import com.yuriikonovalov.common.data.local.TransactionsLocalDataSource
import com.yuriikonovalov.common.framework.data.local.database.dao.TransactionDao
import com.yuriikonovalov.common.framework.data.local.database.mapper.TagMapperDb
import com.yuriikonovalov.common.framework.data.local.database.mapper.TransactionAggregateMapperDb
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.OffsetDateTime
import javax.inject.Inject

class TransactionsLocalDataSourceImpl @Inject constructor(
    private val transactionDao: TransactionDao,
    private val transactionAggregateMapperDb: TransactionAggregateMapperDb,
    private val tagMapperDb: TagMapperDb
) : TransactionsLocalDataSource {

    override suspend fun saveTransaction(transaction: Transaction): Long {
        val transactionDb = transactionAggregateMapperDb.mapFromDomain(transaction).transactionDb
        val tagsDb = transaction.tags.map { tagMapperDb.mapFromDomain(it) }
        return transactionDao.insertWithTag(transactionDb, tagsDb)
    }

    override suspend fun deleteAll() {
        transactionDao.deleteAll()
    }

    override suspend fun deleteTransactionById(transactionId: Long) {
        transactionDao.deleteById(transactionId)
    }

    override suspend fun updateTransaction(transaction: Transaction) {
        val transactionDb = transactionAggregateMapperDb.mapFromDomain(transaction).transactionDb
        val tagsDb = transaction.tags.map { tagMapperDb.mapFromDomain(it) }
        transactionDao.updateWithTag(transactionDb, tagsDb)
    }

    override fun getTransactionById(id: Long): Flow<Transaction?> {
        return transactionDao.get(id).map {
            it?.let { transactionAggregateMapperDb.mapToDomain(it) }
        }
    }

    override fun getTransactionsForPeriod(
        startPeriod: OffsetDateTime?,
        endPeriod: OffsetDateTime?
    ): Flow<List<Transaction>> {
        return transactionDao.getTransactionsForPeriod(startPeriod, endPeriod).map { list ->
            list.map { transactionAggregateDb ->
                transactionAggregateMapperDb.mapToDomain(transactionAggregateDb)
            }
        }
    }


    override suspend fun getTransactionAmountForAccount(accountId: Long): Double {
        val expenseAmount = transactionDao.getTransactionAmountForAccount(
            accountId, TransactionType.EXPENSE
        ) ?: 0.0
        val incomeAmount = transactionDao.getTransactionAmountForAccount(
            accountId, TransactionType.INCOME
        ) ?: 0.0
        return incomeAmount - expenseAmount
    }

    override suspend fun getTransactionAmountForAccountBeforeDate(
        accountId: Long,
        endPeriod: OffsetDateTime
    ): Double {
        val expenseAmount = transactionDao.getTransactionAmountForAccountBeforeDate(
            accountId, endPeriod, TransactionType.EXPENSE
        ) ?: 0.0
        val incomeAmount = transactionDao.getTransactionAmountForAccountBeforeDate(
            accountId, endPeriod, TransactionType.INCOME
        ) ?: 0.0
        return incomeAmount - expenseAmount
    }

    override fun getIncomeTransactionsForPeriod(
        startPeriod: OffsetDateTime?,
        endPeriod: OffsetDateTime?
    ): Flow<List<Transaction>> {
        return transactionDao.getTransactionsForPeriodByType(
            startPeriod,
            endPeriod,
            TransactionType.INCOME
        ).map { list ->
            list.map {
                transactionAggregateMapperDb.mapToDomain(it)
            }
        }
    }

    override fun getExpenseTransactionsForPeriod(
        startPeriod: OffsetDateTime?,
        endPeriod: OffsetDateTime?
    ): Flow<List<Transaction>> {
        return transactionDao.getTransactionsForPeriodByType(
            startPeriod,
            endPeriod,
            TransactionType.EXPENSE
        ).map { list ->
            list.map {
                transactionAggregateMapperDb.mapToDomain(it)
            }
        }
    }

    override fun getTransactionsPaged(
        pageSize: Int,
        startPeriod: OffsetDateTime?,
        endPeriod: OffsetDateTime?
    ): Flow<PagingData<Transaction>> {
        return Pager(
            PagingConfig(pageSize)
        ) {
            transactionDao.getTransactionsPaged(startPeriod, endPeriod)
        }.flow.map { pagingData ->
            pagingData.map { db ->
                transactionAggregateMapperDb.mapToDomain(db)
            }
        }
    }
}