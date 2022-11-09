package com.yuriikonovalov.report.application.usecases

import com.yuriikonovalov.common.application.Resource
import com.yuriikonovalov.common.application.usecases.GetTransactionsForPeriod
import com.yuriikonovalov.common.application.usecases.GetTransfersForPeriod
import com.yuriikonovalov.report.application.entities.CategoryWithNumberOfTransactions
import com.yuriikonovalov.report.application.util.mapToCategoryWithNumberOfTransactions
import com.yuriikonovalov.report.application.util.mapToCategoryWithNumberOfTransactionsList
import com.yuriikonovalov.report.application.util.swapNullCategoryToUncategorized
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.time.OffsetDateTime
import javax.inject.Inject

class GetCategoriesWithNumberOfTransactionsImpl @Inject constructor(
    private val getTransactionsForPeriod: GetTransactionsForPeriod,
    private val getTransfersForPeriod: GetTransfersForPeriod
) : GetCategoriesWithNumberOfTransactions {
    override operator fun invoke(
        startPeriod: OffsetDateTime?,
        endPeriod: OffsetDateTime?
    ): Flow<Resource<List<CategoryWithNumberOfTransactions>>> {
        val transactionFlow = getTransactionsForPeriod(startPeriod, endPeriod)
        val transferFlow = getTransfersForPeriod(startPeriod, endPeriod)
        return combine(transactionFlow, transferFlow) { transactions, transfers ->
            val totalNumberOfTransactions = transactions.size + transfers.size

            val transactionItems = transactions
                .swapNullCategoryToUncategorized()
                .groupBy { it.category!! }
                .mapToCategoryWithNumberOfTransactionsList(totalNumberOfTransactions)


            val transferItem = transfers
                .mapToCategoryWithNumberOfTransactions(totalNumberOfTransactions)

            if (transferItem != null) {
                transactionItems + transferItem
            } else {
                transactionItems
            }
        }.map {
            Resource.successIfNotEmpty(it)
        }
    }
}
