package com.yuriikonovalov.common.data.sources

import com.yuriikonovalov.common.application.entities.Transfer
import com.yuriikonovalov.common.application.entities.transaction.Transaction
import com.yuriikonovalov.common.application.usecases.datasources.GetExpenseForPeriodSource
import com.yuriikonovalov.common.data.local.TransactionsLocalDataSource
import com.yuriikonovalov.common.data.local.TransfersLocalDataSource
import kotlinx.coroutines.flow.Flow
import java.time.OffsetDateTime
import javax.inject.Inject

class GetExpenseForPeriodSourceImpl @Inject constructor(
    private val transactionsLocalDataSource: TransactionsLocalDataSource,
    private val transfersDataSourceLocal: TransfersLocalDataSource
) : GetExpenseForPeriodSource {
    override fun getExpenseTransactionsForPeriod(
        startPeriod: OffsetDateTime?,
        endPeriod: OffsetDateTime?
    ): Flow<List<Transaction>> {
        return transactionsLocalDataSource.getExpenseTransactionsForPeriod(startPeriod, endPeriod)
    }

    override fun getTransfersForPeriod(
        startPeriod: OffsetDateTime?,
        endPeriod: OffsetDateTime?
    ): Flow<List<Transfer>> {
        return transfersDataSourceLocal.getTransfersForPeriod(startPeriod, endPeriod)
    }
}