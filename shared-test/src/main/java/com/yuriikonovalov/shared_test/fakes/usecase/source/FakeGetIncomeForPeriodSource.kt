package com.yuriikonovalov.shared_test.fakes.usecase.source

import com.yuriikonovalov.common.application.entities.Transfer
import com.yuriikonovalov.common.application.entities.transaction.Transaction
import com.yuriikonovalov.common.application.entities.transaction.TransactionType
import com.yuriikonovalov.common.application.usecases.datasources.GetIncomeForPeriodSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.time.OffsetDateTime

class FakeGetIncomeForPeriodSource(
    private val transactions: List<Transaction>,
    private val transfers: List<Transfer>
) : GetIncomeForPeriodSource {
    override fun getIncomeTransactionsForPeriod(
        startPeriod: OffsetDateTime?,
        endPeriod: OffsetDateTime?
    ): Flow<List<Transaction>> {
        val list = transactions
            .filter { it.type == TransactionType.INCOME }
            .filter { it.date.isAfter(startPeriod) || it.date.isEqual(startPeriod) }
            .filter { it.date.isBefore(endPeriod) || it.date.isEqual(endPeriod) }
        return flowOf(list)
    }

    override fun getTransfersForPeriod(
        startPeriod: OffsetDateTime?,
        endPeriod: OffsetDateTime?
    ): Flow<List<Transfer>> {
        val list = transfers
            .filter { it.date.isAfter(startPeriod) || it.date.isEqual(startPeriod) }
            .filter { it.date.isBefore(endPeriod) || it.date.isEqual(endPeriod) }
        return flowOf(list)
    }
}