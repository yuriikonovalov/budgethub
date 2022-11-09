package com.yuriikonovalov.report.application.usecases

import com.yuriikonovalov.common.application.Resource
import com.yuriikonovalov.common.application.entities.account.Account
import com.yuriikonovalov.common.application.util.filterByAccounts
import com.yuriikonovalov.common.application.util.filterByAccountsFrom
import com.yuriikonovalov.report.application.entities.AggregateCategoryFlatByCurrency
import com.yuriikonovalov.report.application.usecases.sources.GetAggregateExpenseCategoriesFlatByCurrencySource
import com.yuriikonovalov.report.application.util.mapToAggregateCategoryFlatByCurrency
import com.yuriikonovalov.report.application.util.mapToAggregateCategoryList
import com.yuriikonovalov.report.application.util.mapToExpenseTransaction
import com.yuriikonovalov.report.application.util.swapNullCategoryToUncategorized
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.time.OffsetDateTime
import javax.inject.Inject

class GetAggregateExpenseCategoriesFlatByCurrencyImpl @Inject constructor(
    private val source: GetAggregateExpenseCategoriesFlatByCurrencySource
) : GetAggregateExpenseCategoriesFlatByCurrency {
    override operator fun invoke(
        startPeriod: OffsetDateTime?,
        endPeriod: OffsetDateTime?,
        accountFilter: List<Account>
    ): Flow<Resource<List<AggregateCategoryFlatByCurrency>>> {
        val expenseTransactionFlow = source.getExpenseTransactionsForPeriod(startPeriod, endPeriod)
        val transferFlow = source.getTransfersForPeriod(startPeriod, endPeriod)

        return combine(expenseTransactionFlow, transferFlow) { expenseTransactions, transfers ->
            val aggregateExpenseCategoriesFlatByCurrency = expenseTransactions
                .filterByAccounts(accountFilter)
                .swapNullCategoryToUncategorized()
                .groupBy { it.category!! }
                .mapToAggregateCategoryList()
                .mapToAggregateCategoryFlatByCurrency()

            val aggregateTransferCategoryFlatByCurrency = transfers
                .filterByAccountsFrom(accountFilter)
                .mapToExpenseTransaction()
                .swapNullCategoryToUncategorized()
                .groupBy { it.category!! }
                .mapToAggregateCategoryList()
                .mapToAggregateCategoryFlatByCurrency()

            (aggregateExpenseCategoriesFlatByCurrency + aggregateTransferCategoryFlatByCurrency)
        }.map {
            Resource.successIfNotEmpty(it)
        }
    }
}