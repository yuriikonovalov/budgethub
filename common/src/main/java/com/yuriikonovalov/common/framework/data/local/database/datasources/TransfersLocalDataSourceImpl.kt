package com.yuriikonovalov.common.framework.data.local.database.datasources

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.yuriikonovalov.common.application.entities.Transfer
import com.yuriikonovalov.common.data.local.TransfersLocalDataSource
import com.yuriikonovalov.common.framework.data.local.database.dao.TransferDao
import com.yuriikonovalov.common.framework.data.local.database.mapper.TagMapperDb
import com.yuriikonovalov.common.framework.data.local.database.mapper.TransferMapperDb
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.OffsetDateTime
import javax.inject.Inject

class TransfersLocalDataSourceImpl @Inject constructor(
    private val transferDao: TransferDao,
    private val transferMapperDb: TransferMapperDb,
    private val tagMapperDb: TagMapperDb
) : TransfersLocalDataSource {

    override suspend fun deleteAll() {
        transferDao.deleteAll()
    }

    override suspend fun saveTransfer(transfer: Transfer): Long {
        val transactionDb = transferMapperDb.mapFromDomain(transfer).transfer
        val tagsDb = transfer.tags.map { tagMapperDb.mapFromDomain(it) }
        return transferDao.insertWithTag(transactionDb, tagsDb)
    }


    override suspend fun delete(id: Long) {
        transferDao.deleteById(id)
    }

    override suspend fun updateTransfer(transfer: Transfer) {
        val transactionDb = transferMapperDb.mapFromDomain(transfer).transfer
        val tagsDb = transfer.tags.map { tagMapperDb.mapFromDomain(it) }
        transferDao.updateWithTag(transactionDb, tagsDb)
    }

    override fun getTransferById(id: Long): Flow<Transfer?> {
        return transferDao.get(id).map {
            it?.let { transferMapperDb.mapToDomain(it) }
        }
    }


    override fun getTransfersForPeriod(
        startPeriod: OffsetDateTime?,
        endPeriod: OffsetDateTime?
    ): Flow<List<Transfer>> {
        return transferDao.getTransfersForPeriod(startPeriod, endPeriod)
            .map { list ->
                list.map { transferMapperDb.mapToDomain(it) }
            }
    }


    override suspend fun getTransferAmountForAccountBeforeDate(
        accountId: Long,
        endPeriod: OffsetDateTime
    ): Double {
        val amountFrom = transferDao.getTransferFromAmountForAccountBeforeDate(
            accountId, endPeriod
        ) ?: 0.0
        val amountTo = transferDao.getTransferToAmountForAccountBeforeDate(
            accountId, endPeriod
        ) ?: 0.0
        return amountTo - amountFrom
    }

    override suspend fun getTransferAmountForAccount(accountId: Long): Double {
        val amountFrom = transferDao.getTransferFromAmountForAccount(accountId) ?: 0.0
        val amountTo = transferDao.getTransferToAmountForAccount(accountId) ?: 0.0
        return amountTo - amountFrom
    }

    override fun getTransfersPaged(
        pageSize: Int,
        startPeriod: OffsetDateTime?,
        endPeriod: OffsetDateTime?
    ): Flow<PagingData<Transfer>> {
        return Pager(PagingConfig(pageSize)) {
            transferDao.getTransfersPaged(startPeriod, endPeriod)
        }.flow.map { pagingData ->
            pagingData.map { transferMapperDb.mapToDomain(it) }
        }
    }
}