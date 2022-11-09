package com.yuriikonovalov.common.framework.data.local.database.dao

import androidx.paging.PagingSource
import androidx.room.*
import com.yuriikonovalov.common.application.entities.transaction.TransactionType
import com.yuriikonovalov.common.framework.data.local.database.model.TagDb
import com.yuriikonovalov.common.framework.data.local.database.model.TransactionDb
import com.yuriikonovalov.common.framework.data.local.database.model.TransactionTagCrossRefDb
import com.yuriikonovalov.common.framework.data.local.database.model.relations.TransactionAggregateDb
import kotlinx.coroutines.flow.Flow
import java.time.OffsetDateTime

@Dao
interface TransactionDao {
    @Insert
    suspend fun insert(transaction: TransactionDb): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(transaction: TransactionDb)

    @Query("DELETE FROM transactions")
    suspend fun deleteAll()

    @Query("DELETE FROM transactions WHERE id =:transactionId")
    suspend fun deleteById(transactionId: Long)

    @Transaction
    @Query("SELECT * FROM transactions WHERE id=:id")
    fun get(id: Long): Flow<TransactionAggregateDb?>


    @Transaction
    suspend fun updateWithTag(transaction: TransactionDb, tags: List<TagDb>) {
        // update a transaction
        update(transaction)
        // delete old crossRefs
        deleteTagCrossRefs(transaction.id)
        // insert new crossRefs
        tags.forEach { tagId ->
            insertTagCrossRef(TransactionTagCrossRefDb(transaction.id, tagId.id))
        }
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTagCrossRef(crossRef: TransactionTagCrossRefDb)


    @Query("DELETE FROM transaction_tag_cross_refs WHERE id = :transactionId")
    suspend fun deleteTagCrossRefs(transactionId: Long)

    @Transaction
    suspend fun insertWithTag(transaction: TransactionDb, tags: List<TagDb>): Long {
        // insert a transaction
        val transactionId = insert(transaction)
        // insert new crossRefs
        tags.forEach { tagId ->
            insertTagCrossRef(TransactionTagCrossRefDb(transactionId, tagId.id))
        }
        return transactionId
    }


    @Transaction
    @Query("SELECT * FROM transactions ORDER BY datetime(date) DESC")
    fun getAll(): Flow<List<TransactionAggregateDb>>

    @Transaction
    @Query(
        "SELECT * FROM transactions " +
                "WHERE type = :type " +
                "ORDER BY datetime(date) DESC"
    )
    fun getAllByType(type: TransactionType): Flow<List<TransactionAggregateDb>>

    @Transaction
    @Query(
        "SELECT * FROM transactions " +
                "WHERE datetime(date) >= datetime(:dateFrom) " +
                "ORDER BY datetime(date) DESC"
    )
    fun getTransactionsFromDate(dateFrom: OffsetDateTime): Flow<List<TransactionAggregateDb>>

    @Transaction
    @Query(
        "SELECT * FROM transactions " +
                "WHERE datetime(date) BETWEEN datetime(:startPeriod) AND datetime(:endPeriod) " +
                "ORDER BY datetime(date) DESC"
    )
    fun getTransactionsBetween(
        startPeriod: OffsetDateTime,
        endPeriod: OffsetDateTime
    ): Flow<List<TransactionAggregateDb>>

    fun getTransactionsForPeriod(
        startPeriod: OffsetDateTime?,
        endPeriod: OffsetDateTime?
    ): Flow<List<TransactionAggregateDb>> {
        return when {
            startPeriod != null && endPeriod != null -> {
                getTransactionsBetween(startPeriod, endPeriod)
            }
            startPeriod != null && endPeriod == null -> {
                getTransactionsFromDate(startPeriod)
            }
            startPeriod == null && endPeriod == null -> {
                getAll()
            }
            else -> {
                throw IllegalArgumentException("No fun to get transactions before 'endDate'")
            }
        }
    }

    @Query(
        "SELECT SUM(amount) FROM transactions " +
                "WHERE account_id = :accountId AND type = :type " +
                "GROUP BY type"
    )
    suspend fun getTransactionAmountForAccount(accountId: Long, type: TransactionType): Double?

    @Query(
        "SELECT SUM(amount) FROM transactions " +
                "WHERE account_id = :accountId AND datetime(date)<= datetime(:endPeriod) AND type = :type " +
                "GROUP BY account_id"
    )
    suspend fun getTransactionAmountForAccountBeforeDate(
        accountId: Long,
        endPeriod: OffsetDateTime,
        type: TransactionType
    ): Double?

    @Transaction
    @Query(
        "SELECT * FROM transactions " +
                "WHERE datetime(date) BETWEEN datetime(:startPeriod) AND datetime(:endPeriod) AND type = :type"
    )
    fun getTransactionsBetweenByType(
        startPeriod: OffsetDateTime,
        endPeriod: OffsetDateTime,
        type: TransactionType
    ): Flow<List<TransactionAggregateDb>>

    fun getTransactionsForPeriodByType(
        startPeriod: OffsetDateTime?,
        endPeriod: OffsetDateTime?,
        type: TransactionType
    ): Flow<List<TransactionAggregateDb>> {
        return when {
            startPeriod != null && endPeriod != null -> {
                getTransactionsBetweenByType(startPeriod, endPeriod, type)
            }
            startPeriod != null && endPeriod == null -> {
                getTransactionsFromDateByType(startPeriod, type)
            }
            startPeriod == null && endPeriod == null -> {
                getAllByType(type)
            }
            else -> {
                throw IllegalArgumentException("No fun to get transactions before 'endDate'")
            }
        }
    }

    fun getExpenseTransactionsForPeriod(
        startPeriod: OffsetDateTime?,
        endPeriod: OffsetDateTime?
    ) = getTransactionsForPeriodByType(startPeriod, endPeriod, TransactionType.EXPENSE)


    @Transaction
    @Query(
        "SELECT * FROM transactions " +
                "WHERE datetime(date) >= datetime(:dateFrom) AND type = :type"
    )
    fun getTransactionsFromDateByType(
        dateFrom: OffsetDateTime,
        type: TransactionType
    ): Flow<List<TransactionAggregateDb>>


    fun getTransactionsPaged(
        startPeriod: OffsetDateTime? = null,
        endPeriod: OffsetDateTime? = null
    ): PagingSource<Int, TransactionAggregateDb> = when {
        startPeriod != null && endPeriod != null -> {
            getTransactionsBetweenDatesPaged(startPeriod, endPeriod)
        }
        startPeriod != null && endPeriod == null -> {
            getTransactionsFromDatePaged(startPeriod)
        }
        startPeriod == null && endPeriod == null -> {
            getAllPaged()
        }
        else -> {
            throw IllegalArgumentException("No fun to get transactions before 'endDate'")
        }
    }

    @Transaction
    @Query("SELECT * FROM transactions ORDER BY datetime(date) DESC")
    fun getAllPaged(): PagingSource<Int, TransactionAggregateDb>

    @Transaction
    @Query(
        "SELECT * FROM transactions " +
                "WHERE datetime(date) >= datetime(:dateFrom) " +
                "ORDER BY datetime(date) DESC"
    )
    fun getTransactionsFromDatePaged(dateFrom: OffsetDateTime): PagingSource<Int, TransactionAggregateDb>

    @Transaction
    @Query(
        "SELECT * FROM transactions " +
                "WHERE datetime(date) BETWEEN datetime(:startPeriod) AND datetime(:endPeriod) " +
                "ORDER BY datetime(date) DESC"
    )
    fun getTransactionsBetweenDatesPaged(
        startPeriod: OffsetDateTime,
        endPeriod: OffsetDateTime
    ): PagingSource<Int, TransactionAggregateDb>
}