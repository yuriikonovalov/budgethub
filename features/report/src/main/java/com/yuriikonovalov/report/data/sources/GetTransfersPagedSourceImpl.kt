package com.yuriikonovalov.report.data.sources

import androidx.paging.PagingData
import com.yuriikonovalov.common.application.entities.Transfer
import com.yuriikonovalov.common.data.local.TransfersLocalDataSource
import com.yuriikonovalov.report.application.usecases.sources.GetTransfersPagedSource
import kotlinx.coroutines.flow.Flow
import java.time.OffsetDateTime
import javax.inject.Inject

class GetTransfersPagedSourceImpl @Inject constructor(
    private val transfersDataSourceLocal: TransfersLocalDataSource
) : GetTransfersPagedSource {
    override fun getTransfersPaged(
        pageSize: Int,
        startPeriod: OffsetDateTime?,
        endPeriod: OffsetDateTime?
    ): Flow<PagingData<Transfer>> {
        return transfersDataSourceLocal.getTransfersPaged(pageSize, startPeriod, endPeriod)
    }
}