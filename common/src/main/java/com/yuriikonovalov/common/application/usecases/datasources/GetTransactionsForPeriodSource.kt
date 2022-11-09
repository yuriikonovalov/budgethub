package com.yuriikonovalov.common.application.usecases.datasources

import com.yuriikonovalov.common.application.entities.transaction.Transaction
import kotlinx.coroutines.flow.Flow
import java.time.OffsetDateTime

interface GetTransactionsForPeriodSource {
    fun getTransactionsForPeriod(
        startPeriod: OffsetDateTime?,
        endPeriod: OffsetDateTime?
    ): Flow<List<Transaction>>
}