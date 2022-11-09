package com.yuriikonovalov.common.application.usecases

import com.yuriikonovalov.common.application.Resource
import com.yuriikonovalov.common.application.entities.TransactionItem
import kotlinx.coroutines.flow.Flow
import java.time.OffsetDateTime

interface GetTransactionItemsForPeriod {
    operator fun invoke(
        startPeriod: OffsetDateTime,
        endPeriod: OffsetDateTime
    ): Flow<Resource<List<TransactionItem>>>
}