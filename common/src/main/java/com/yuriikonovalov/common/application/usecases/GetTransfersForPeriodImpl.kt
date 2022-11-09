package com.yuriikonovalov.common.application.usecases

import com.yuriikonovalov.common.application.entities.Transfer
import com.yuriikonovalov.common.application.usecases.datasources.GetTransfersForPeriodSource
import kotlinx.coroutines.flow.Flow
import java.time.OffsetDateTime
import javax.inject.Inject

class GetTransfersForPeriodImpl @Inject constructor(
    private val source: GetTransfersForPeriodSource
) : GetTransfersForPeriod {
    override operator fun invoke(
        startPeriod: OffsetDateTime?,
        endPeriod: OffsetDateTime?
    ): Flow<List<Transfer>> {
        return source.getTransfersForPeriod(startPeriod = startPeriod, endPeriod = endPeriod)
    }
}