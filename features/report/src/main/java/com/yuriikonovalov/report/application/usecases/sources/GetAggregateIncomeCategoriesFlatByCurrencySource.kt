package com.yuriikonovalov.report.application.usecases.sources

import com.yuriikonovalov.common.application.entities.Transfer
import com.yuriikonovalov.common.application.entities.transaction.Transaction
import kotlinx.coroutines.flow.Flow
import java.time.OffsetDateTime

interface GetAggregateIncomeCategoriesFlatByCurrencySource {
    fun getIncomeTransactionsForPeriod(
        startPeriod: OffsetDateTime?,
        endPeriod: OffsetDateTime?
    ): Flow<List<Transaction>>

    fun getTransfersForPeriod(
        startPeriod: OffsetDateTime?,
        endPeriod: OffsetDateTime?
    ): Flow<List<Transfer>>
}