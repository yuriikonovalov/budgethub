package com.yuriikonovalov.report.data.sources

import com.yuriikonovalov.common.application.entities.Transfer
import com.yuriikonovalov.common.application.entities.transaction.Transaction
import com.yuriikonovalov.common.data.local.TransactionsLocalDataSource
import com.yuriikonovalov.common.data.local.TransfersLocalDataSource
import com.yuriikonovalov.report.application.usecases.sources.GetAggregateExpenseCategoriesFlatByCurrencySource
import kotlinx.coroutines.flow.Flow
import java.time.OffsetDateTime
import javax.inject.Inject

class GetAggregateExpenseCategoriesFlatByCurrencySourceImpl @Inject constructor(
    private val transactionsLocalDataSource: TransactionsLocalDataSource,
    private val transfersLocalDataSource: TransfersLocalDataSource
) : GetAggregateExpenseCategoriesFlatByCurrencySource {

    override fun getTransfersForPeriod(
        startPeriod: OffsetDateTime?,
        endPeriod: OffsetDateTime?
    ): Flow<List<Transfer>> {
        return transfersLocalDataSource.getTransfersForPeriod(
            startPeriod,
            endPeriod
        )
    }

    override fun getExpenseTransactionsForPeriod(
        startPeriod: OffsetDateTime?,
        endPeriod: OffsetDateTime?
    ): Flow<List<Transaction>> {
        return transactionsLocalDataSource.getExpenseTransactionsForPeriod(startPeriod, endPeriod)
    }
}