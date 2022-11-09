package com.yuriikonovalov.report.application.usecases

import com.yuriikonovalov.common.application.Resource
import com.yuriikonovalov.common.application.entities.account.Account
import com.yuriikonovalov.common.application.util.filterByAccounts
import com.yuriikonovalov.common.application.util.filterByAccountsTo
import com.yuriikonovalov.report.application.entities.AggregateCategoryFlatByCurrency
import com.yuriikonovalov.report.application.usecases.sources.GetAggregateIncomeCategoriesFlatByCurrencySource
import com.yuriikonovalov.report.application.util.mapToAggregateCategoryFlatByCurrency
import com.yuriikonovalov.report.application.util.mapToAggregateCategoryList
import com.yuriikonovalov.report.application.util.mapToIncomeTransaction
import com.yuriikonovalov.report.application.util.swapNullCategoryToUncategorized
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.time.OffsetDateTime
import javax.inject.Inject

 class GetAggregateIncomeCategoriesFlatByCurrencyImpl @Inject constructor(
    private val source: GetAggregateIncomeCategoriesFlatByCurrencySource
) : GetAggregateIncomeCategoriesFlatByCurrency {
    override operator fun invoke(
        startPeriod: OffsetDateTime?,
        endPeriod: OffsetDateTime?,
        accountFilter: List<Account>,
    ): Flow<Resource<List<AggregateCategoryFlatByCurrency>>> {
        val incomeTransactionFlow = source.getIncomeTransactionsForPeriod(startPeriod, endPeriod)
        val transferFlow = source.getTransfersForPeriod(startPeriod, endPeriod)

        return combine(incomeTransactionFlow, transferFlow) { incomeTransactions, transfers ->
            val aggregateIncomeCategoryFlatByCurrency = incomeTransactions
                .filterByAccounts(accountFilter)
                .swapNullCategoryToUncategorized()
                .groupBy { it.category!! }
                .mapToAggregateCategoryList()
                .mapToAggregateCategoryFlatByCurrency()

            val aggregateTransferCategoryFlatByCurrency = transfers
                .filterByAccountsTo(accountFilter)
                .mapToIncomeTransaction()
                .swapNullCategoryToUncategorized()
                .groupBy { it.category!! }
                .mapToAggregateCategoryList()
                .mapToAggregateCategoryFlatByCurrency()

            (aggregateIncomeCategoryFlatByCurrency + aggregateTransferCategoryFlatByCurrency)
        }.map {
            Resource.successIfNotEmpty(it)
        }
    }
}