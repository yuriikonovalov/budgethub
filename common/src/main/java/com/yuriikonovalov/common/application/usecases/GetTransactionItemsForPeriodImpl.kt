package com.yuriikonovalov.common.application.usecases

import com.yuriikonovalov.common.application.Resource
import com.yuriikonovalov.common.application.entities.TransactionItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.time.OffsetDateTime
import javax.inject.Inject

class GetTransactionItemsForPeriodImpl @Inject constructor(
    private val getTransfersForPeriod: GetTransfersForPeriod,
    private val getTransactionsForPeriod: GetTransactionsForPeriod,
) : GetTransactionItemsForPeriod {
    override operator fun invoke(
        startPeriod: OffsetDateTime,
        endPeriod: OffsetDateTime
    ): Flow<Resource<List<TransactionItem>>> {
        val expenseIncomeTransactions = getTransactionsForPeriod(startPeriod, endPeriod)
        val transferTransactions = getTransfersForPeriod(startPeriod, endPeriod)
        return combine(
            expenseIncomeTransactions,
            transferTransactions
        ) { expensesAndIncomes, transfers ->
            val transactionItemList = mutableListOf<TransactionItem>()
            transactionItemList.addAll(expensesAndIncomes.map(TransactionItem::ExpenseIncome))
            transactionItemList.addAll(transfers.map(TransactionItem::Transfer))
            transactionItemList.sortedByDescending { it.date }
        }.map {
            Resource.successIfNotEmpty(it)
        }
    }
}