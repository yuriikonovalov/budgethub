package com.yuriikonovalov.common.application.usecases.datasources

import com.yuriikonovalov.common.application.entities.Transfer
import kotlinx.coroutines.flow.Flow
import java.time.OffsetDateTime

interface GetTransfersForPeriodSource {
    fun getTransfersForPeriod(
        startPeriod: OffsetDateTime?,
        endPeriod: OffsetDateTime?
    ): Flow<List<Transfer>>
}