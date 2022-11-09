package com.yuriikonovalov.report.application.usecases.sources

import androidx.paging.PagingData
import com.yuriikonovalov.common.application.entities.Transfer
import kotlinx.coroutines.flow.Flow
import java.time.OffsetDateTime

interface GetTransfersPagedSource {
    fun getTransfersPaged(
        pageSize: Int,
        startPeriod: OffsetDateTime?,
        endPeriod: OffsetDateTime?
    ): Flow<PagingData<Transfer>>
}