package com.yuriikonovalov.common.data.sources

import com.yuriikonovalov.common.application.entities.transaction.Transaction
import com.yuriikonovalov.common.application.usecases.datasources.GetTransactionsForPeriodSource
import com.yuriikonovalov.common.data.local.TransactionsLocalDataSource
import kotlinx.coroutines.flow.Flow
import java.time.OffsetDateTime
import javax.inject.Inject

class GetTransactionsForPeriodSourceImpl @Inject constructor(
    private val transactionsSourceLocal: TransactionsLocalDataSource
) : GetTransactionsForPeriodSource {
    override fun getTransactionsForPeriod(
        startPeriod: OffsetDateTime?,
        endPeriod: OffsetDateTime?
    ): Flow<List<Transaction>> {
        return transactionsSourceLocal.getTransactionsForPeriod(
            startPeriod = startPeriod,
            endPeriod = endPeriod
        )
    }
}