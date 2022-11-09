package com.yuriikonovalov.common.data.sources

import com.yuriikonovalov.common.application.entities.Transfer
import com.yuriikonovalov.common.application.entities.transaction.Transaction
import com.yuriikonovalov.common.application.usecases.datasources.GetIncomeForPeriodSource
import com.yuriikonovalov.common.data.local.TransactionsLocalDataSource
import com.yuriikonovalov.common.data.local.TransfersLocalDataSource
import kotlinx.coroutines.flow.Flow
import java.time.OffsetDateTime
import javax.inject.Inject

class GetIncomeForPeriodSourceImpl @Inject constructor(
    private val transactionsLocalDataSource: TransactionsLocalDataSource,
    private val transfersDataSourceLocal: TransfersLocalDataSource
) : GetIncomeForPeriodSource {
    override fun getIncomeTransactionsForPeriod(
        startPeriod: OffsetDateTime?,
        endPeriod: OffsetDateTime?
    ): Flow<List<Transaction>> {
        return transactionsLocalDataSource.getIncomeTransactionsForPeriod(startPeriod, endPeriod)
    }

    override fun getTransfersForPeriod(
        startPeriod: OffsetDateTime?,
        endPeriod: OffsetDateTime?
    ): Flow<List<Transfer>> {
        return transfersDataSourceLocal.getTransfersForPeriod(startPeriod, endPeriod)
    }
}