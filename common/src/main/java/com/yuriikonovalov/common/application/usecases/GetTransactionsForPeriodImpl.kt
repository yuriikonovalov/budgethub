package com.yuriikonovalov.common.application.usecases

import com.yuriikonovalov.common.application.entities.transaction.Transaction
import com.yuriikonovalov.common.application.usecases.datasources.GetTransactionsForPeriodSource
import kotlinx.coroutines.flow.Flow
import java.time.OffsetDateTime
import javax.inject.Inject

class GetTransactionsForPeriodImpl @Inject constructor(
    private val source: GetTransactionsForPeriodSource
) : GetTransactionsForPeriod {
    override operator fun invoke(
        startPeriod: OffsetDateTime?,
        endPeriod: OffsetDateTime?
    ): Flow<List<Transaction>> {
        return source.getTransactionsForPeriod(startPeriod = startPeriod, endPeriod = endPeriod)
    }
}


