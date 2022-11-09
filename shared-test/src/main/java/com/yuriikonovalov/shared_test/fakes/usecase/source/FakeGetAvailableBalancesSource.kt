package com.yuriikonovalov.shared_test.fakes.usecase.source

import com.yuriikonovalov.common.application.entities.Transfer
import com.yuriikonovalov.common.application.entities.account.Account
import com.yuriikonovalov.common.application.entities.transaction.Transaction
import com.yuriikonovalov.common.application.entities.transaction.TransactionType
import com.yuriikonovalov.common.application.usecases.datasources.GetAvailableBalancesSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.time.OffsetDateTime

class FakeGetAvailableBalancesSource(
    private val accounts: List<Account>,
    private val transfers: List<Transfer>,
    private val transactions: List<Transaction>
) : GetAvailableBalancesSource {
    override suspend fun getTransfersAmountChange(
        accountId: Long,
        endPeriod: OffsetDateTime
    ): Double {
        val incomes = transfers
            .filter { it.accountTo.id == accountId }
            .filter { it.date.isBefore(endPeriod) }
            .sumOf { it.amountTo }

        val expenses = transfers
            .filter { it.accountFrom.id == accountId }
            .filter { it.date.isBefore(endPeriod) }
            .sumOf { it.amountFrom }
        return incomes - expenses
    }

    override suspend fun getTransactionsAmountChange(
        accountId: Long,
        endPeriod: OffsetDateTime
    ): Double {
        val incomes = transactions
            .filter { it.account.id == accountId }
            .filter { it.type == TransactionType.INCOME }
            .filter { it.date.isBefore(endPeriod) }
            .sumOf { it.amount }

        val expenses = transactions
            .filter { it.account.id == accountId }
            .filter { it.type == TransactionType.EXPENSE }
            .filter { it.date.isBefore(endPeriod) }
            .sumOf { it.amount }
        return incomes - expenses
    }

    override suspend fun getInitialBalance(accountId: Long): Double {
        return accounts.find { it.id == accountId }?.initialBalance ?: 0.0
    }

    override suspend fun getAccounts(): Flow<List<Account>> {
        return flowOf(accounts)
    }
}