package com.yuriikonovalov.common.application.usecases

import com.yuriikonovalov.common.application.Resource
import com.yuriikonovalov.common.application.entities.AvailableBalance
import com.yuriikonovalov.common.application.usecases.datasources.GetAvailableBalancesSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.OffsetDateTime
import javax.inject.Inject


class GetAvailableBalancesImpl @Inject constructor(
    private val source: GetAvailableBalancesSource
) : GetAvailableBalances {
    override suspend operator fun invoke(
        startPeriod: OffsetDateTime,
        endPeriod: OffsetDateTime
    ): Flow<Resource<List<AvailableBalance>>> {
        return source.getAccounts().map { accounts ->
            accounts.map { account ->
                val id = account.id
                val initialBalance = source.getInitialBalance(id)

                // Start balance
                val transfersAmountStartPeriod =
                    source.getTransfersAmountChange(id, startPeriod)
                val transactionsAmountStartPeriod =
                    source.getTransactionsAmountChange(id, startPeriod)
                val startBalance =
                    initialBalance + transactionsAmountStartPeriod + transfersAmountStartPeriod

                // End balance
                val transfersAmountEndPeriod = source.getTransfersAmountChange(id, endPeriod)
                val transactionsAmountEndPeriod =
                    source.getTransactionsAmountChange(id, endPeriod)
                val endBalance =
                    initialBalance + transactionsAmountEndPeriod + transfersAmountEndPeriod

                AvailableBalance(id, account.currency, startBalance, endBalance)
            }
        }.map { list ->
            Resource.successIfNotEmpty(list)
        }
    }
}