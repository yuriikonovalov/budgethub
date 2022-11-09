package com.yuriikonovalov.common.data.sources

import com.yuriikonovalov.common.application.entities.Transfer
import com.yuriikonovalov.common.application.usecases.datasources.GetTransfersForPeriodSource
import com.yuriikonovalov.common.data.local.TransfersLocalDataSource
import kotlinx.coroutines.flow.Flow
import java.time.OffsetDateTime
import javax.inject.Inject

class GetTransfersForPeriodSourceImpl @Inject constructor(
    private val transfersSourceLocal: TransfersLocalDataSource
) : GetTransfersForPeriodSource {
    override fun getTransfersForPeriod(
        startPeriod: OffsetDateTime?,
        endPeriod: OffsetDateTime?
    ): Flow<List<Transfer>> {
        return transfersSourceLocal.getTransfersForPeriod(
            startPeriod = startPeriod,
            endPeriod = endPeriod
        )
    }
}