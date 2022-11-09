package com.yuriikonovalov.common.application.usecases

import com.yuriikonovalov.common.application.entities.Transfer
import kotlinx.coroutines.flow.Flow
import java.time.OffsetDateTime

interface GetTransfersForPeriod {
    operator fun invoke(
        startPeriod: OffsetDateTime?,
        endPeriod: OffsetDateTime?
    ): Flow<List<Transfer>>
}