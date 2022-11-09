package com.yuriikonovalov.common.framework.data.local.database.dao

import androidx.paging.PagingSource
import androidx.room.*
import com.yuriikonovalov.common.framework.data.local.database.model.TagDb
import com.yuriikonovalov.common.framework.data.local.database.model.TransferTagCrossRefDb
import com.yuriikonovalov.common.framework.data.local.database.model.TransferDb
import com.yuriikonovalov.common.framework.data.local.database.model.relations.TransferAggregateDb
import kotlinx.coroutines.flow.Flow
import java.time.OffsetDateTime

@Dao
interface TransferDao {
    @Insert
    suspend fun insert(transfer: TransferDb): Long

    @Query("DELETE FROM transfers")
    suspend fun deleteAll()

    @Query("DELETE FROM transfer_tag_cross_refs WHERE id = :transferId")
    suspend fun deleteTagCrossRefs(transferId: Long)

    @Transaction
    suspend fun insertWithTag(transfer: TransferDb, tags: List<TagDb>): Long {
        // insert a transfer
        val transferId = insert(transfer)
        // insert new crossRefs
        tags.forEach { tagId ->
            insertTagCrossRef(TransferTagCrossRefDb(transferId, tagId.id))
        }
        return transferId
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTagCrossRef(crossRef: TransferTagCrossRefDb)

    @Transaction
    suspend fun updateWithTag(transfer: TransferDb, tags: List<TagDb>) {
        // update a transfer
        update(transfer)
        // delete old crossRefs
        deleteTagCrossRefs(transfer.id)
        // insert new crossRefs
        tags.forEach { tagId ->
            insertTagCrossRef(TransferTagCrossRefDb(transfer.id, tagId.id))
        }
    }

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(transaction: TransferDb)

    @Transaction
    @Query("SELECT * FROM transfers WHERE id=:id")
    fun get(id: Long): Flow<TransferAggregateDb?>

    @Transaction
    @Query("SELECT * FROM transfers")
    fun getAll(): Flow<List<TransferAggregateDb>>

    @Query("DELETE FROM transfers WHERE id = :transferId")
    suspend fun deleteById(transferId: Long)

    @Transaction
    @Query(
        "SELECT * FROM transfers " +
                "WHERE datetime(date) >= datetime(:dateFrom) " +
                "ORDER BY datetime(date) DESC"
    )
    fun getTransfersFromDate(dateFrom: OffsetDateTime): Flow<List<TransferAggregateDb>>

    @Transaction
    @Query(
        "SELECT * FROM transfers " +
                "WHERE datetime(date) BETWEEN datetime(:startPeriod) AND datetime(:endPeriod) " +
                "ORDER BY datetime(date) DESC"
    )
    fun getTransfersBetween(
        startPeriod: OffsetDateTime,
        endPeriod: OffsetDateTime
    ): Flow<List<TransferAggregateDb>>


    fun getTransfersForPeriod(
        startPeriod: OffsetDateTime?,
        endPeriod: OffsetDateTime?
    ): Flow<List<TransferAggregateDb>> {
        return when {
            startPeriod != null && endPeriod != null -> {
                getTransfersBetween(startPeriod, endPeriod)
            }
            startPeriod != null && endPeriod == null -> {
                getTransfersFromDate(startPeriod)
            }
            startPeriod == null && endPeriod == null -> {
                getAll()
            }
            else -> {
                throw IllegalArgumentException("No fun to get transfers before 'endDate'")
            }
        }
    }

    @Query(
        "SELECT SUM(amountFrom) FROM transfers " +
                "WHERE sender_id = :accountId AND datetime(date)<= datetime(:endPeriod) " +
                "GROUP BY sender_id"
    )
    suspend fun getTransferFromAmountForAccountBeforeDate(
        accountId: Long,
        endPeriod: OffsetDateTime
    ): Double?


    @Query(
        "SELECT SUM(amountTo) FROM transfers " +
                "WHERE recipient_id = :accountId AND datetime(date)<= datetime(:endPeriod) " +
                "GROUP BY recipient_id"
    )
    suspend fun getTransferToAmountForAccountBeforeDate(
        accountId: Long,
        endPeriod: OffsetDateTime
    ): Double?


    @Query(
        "SELECT SUM(amountFrom) FROM transfers " +
                "WHERE sender_id = :accountId " +
                "GROUP BY sender_id"
    )
    suspend fun getTransferFromAmountForAccount(accountId: Long): Double?


    @Query(
        "SELECT SUM(amountTo) FROM transfers " +
                "WHERE recipient_id = :accountId " +
                "GROUP BY recipient_id"
    )
    suspend fun getTransferToAmountForAccount(accountId: Long): Double?


    fun getTransfersPaged(
        startPeriod: OffsetDateTime?,
        endPeriod: OffsetDateTime?
    ): PagingSource<Int, TransferAggregateDb> {
        return when {
            startPeriod != null && endPeriod != null -> {
                getTransfersPagedBetween(startPeriod, endPeriod)
            }
            startPeriod != null && endPeriod == null -> {
                getTransfersPagedFromDate(startPeriod)
            }
            startPeriod == null && endPeriod == null -> {
                getAllPaged()
            }
            else -> {
                throw IllegalArgumentException("No fun to get transfers before 'endDate'")
            }
        }
    }


    @Transaction
    @Query(
        "SELECT * FROM transfers " +
                "WHERE datetime(date) >= datetime(:dateFrom) " +
                "ORDER BY datetime(date) DESC"
    )
    fun getTransfersPagedFromDate(dateFrom: OffsetDateTime): PagingSource<Int, TransferAggregateDb>

    @Transaction
    @Query(
        "SELECT * FROM transfers " +
                "WHERE datetime(date) BETWEEN datetime(:startPeriod) AND datetime(:endPeriod) " +
                "ORDER BY datetime(date) DESC"
    )
    fun getTransfersPagedBetween(
        startPeriod: OffsetDateTime,
        endPeriod: OffsetDateTime
    ): PagingSource<Int, TransferAggregateDb>


    @Transaction
    @Query("SELECT * FROM transfers")
    fun getAllPaged(): PagingSource<Int, TransferAggregateDb>
}