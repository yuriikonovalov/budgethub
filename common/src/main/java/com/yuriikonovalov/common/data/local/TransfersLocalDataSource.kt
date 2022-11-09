package com.yuriikonovalov.common.data.local

import androidx.paging.PagingData
import com.yuriikonovalov.common.application.entities.Transfer
import kotlinx.coroutines.flow.Flow
import java.time.OffsetDateTime


interface TransfersLocalDataSource {
    suspend fun deleteAll()
    suspend fun saveTransfer(transfer: Transfer): Long
    suspend fun delete(id: Long)
    suspend fun updateTransfer(transfer: Transfer)
    fun getTransferById(id: Long): Flow<Transfer?>

    fun getTransfersForPeriod(
        startPeriod: OffsetDateTime?,
        endPeriod: OffsetDateTime?
    ): Flow<List<Transfer>>

    suspend fun getTransferAmountForAccountBeforeDate(
        accountId: Long,
        endPeriod: OffsetDateTime
    ): Double

    suspend fun getTransferAmountForAccount(accountId: Long): Double

    fun getTransfersPaged(
        pageSize: Int,
        startPeriod: OffsetDateTime?,
        endPeriod: OffsetDateTime?
    ): Flow<PagingData<Transfer>>

}