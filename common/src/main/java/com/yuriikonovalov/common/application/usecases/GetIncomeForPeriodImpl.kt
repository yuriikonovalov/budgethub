package com.yuriikonovalov.common.application.usecases

import com.yuriikonovalov.common.application.Resource
import com.yuriikonovalov.common.application.entities.Currency
import com.yuriikonovalov.common.application.entities.TransactionAmount
import com.yuriikonovalov.common.application.entities.Transfer
import com.yuriikonovalov.common.application.entities.account.Account
import com.yuriikonovalov.common.application.entities.transaction.Transaction
import com.yuriikonovalov.common.application.usecases.datasources.GetIncomeForPeriodSource
import com.yuriikonovalov.common.application.util.filterByAccounts
import com.yuriikonovalov.common.application.util.filterByAccountsTo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.time.OffsetDateTime
import javax.inject.Inject

class GetIncomeForPeriodImpl @Inject constructor(
    private val source: GetIncomeForPeriodSource
) : GetIncomeForPeriod {
    override operator fun invoke(
        startPeriod: OffsetDateTime?,
        endPeriod: OffsetDateTime?,
        accountFilter: List<Account>
    ): Flow<Resource<List<TransactionAmount>>> {
        val transactionsFlow = source.getIncomeTransactionsForPeriod(startPeriod, endPeriod)
        val transfersFlow = source.getTransfersForPeriod(startPeriod, endPeriod)
        return combine(transactionsFlow, transfersFlow) { transactions, transfers ->
            val transactionIncomes = transactions
                .filterByAccounts(accountFilter)
                .toTransactionAmountList()
            val transferIncomes = transfers
                .filterByAccountsTo(accountFilter)
                .toTransactionAmountList()

            (transferIncomes + transactionIncomes)
                .groupBy { it.currency }
                .sumAmountPerCurrency()
                .sortedBy { it.currency.code }
        }.map {
            Resource.successIfNotEmpty(it)
        }
    }


    private fun Map<Currency, List<TransactionAmount>>.sumAmountPerCurrency(): List<TransactionAmount> {
        return this.map { entry ->
            TransactionAmount(
                currency = entry.key,
                amount = entry.value.sumOf { it.amount },
                type = TransactionAmount.Type.INCOME
            )
        }
    }

    private fun List<Transaction>.toTransactionAmountList(): List<TransactionAmount> {
        return this.map { transaction ->
            TransactionAmount(
                amount = transaction.amount,
                currency = transaction.account.currency,
                type = TransactionAmount.Type.INCOME
            )
        }
    }

    @JvmName("toTransfersToTransactionAmountList")
    private fun List<Transfer>.toTransactionAmountList(): List<TransactionAmount> {
        return this.map { transfer ->
            TransactionAmount(
                amount = transfer.amountTo,
                currency = transfer.accountTo.currency,
                type = TransactionAmount.Type.INCOME
            )
        }
    }
}