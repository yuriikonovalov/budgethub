package com.yuriikonovalov.shared_test.fakes.usecase.source

import com.yuriikonovalov.common.application.entities.Transfer
import com.yuriikonovalov.common.application.entities.transaction.Transaction
import com.yuriikonovalov.common.application.entities.transaction.TransactionType
import com.yuriikonovalov.common.application.usecases.datasources.GetExpenseForPeriodSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.time.OffsetDateTime

class FakeGetExpenseForPeriodSource(
    private val transactions: List<Transaction>,
    private val transfers: List<Transfer>
) : GetExpenseForPeriodSource {
    override fun getExpenseTransactionsForPeriod(
        startPeriod: OffsetDateTime?,
        endPeriod: OffsetDateTime?
    ): Flow<List<Transaction>> {
        val list = transactions
            .filter { it.type == TransactionType.EXPENSE }
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