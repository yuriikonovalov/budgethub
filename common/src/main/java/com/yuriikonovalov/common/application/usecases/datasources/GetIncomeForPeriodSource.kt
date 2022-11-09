package com.yuriikonovalov.common.application.usecases.datasources

import com.yuriikonovalov.common.application.entities.Transfer
import com.yuriikonovalov.common.application.entities.transaction.Transaction
import kotlinx.coroutines.flow.Flow
import java.time.OffsetDateTime

interface GetIncomeForPeriodSource {
    fun getIncomeTransactionsForPeriod(
        startPeriod: OffsetDateTime?,
        endPeriod: OffsetDateTime?
    ): Flow<List<Transaction>>

    fun getTransfersForPeriod(
        startPeriod: OffsetDateTime?,
        endPeriod: OffsetDateTime?
    ): Flow<List<Transfer>>

}