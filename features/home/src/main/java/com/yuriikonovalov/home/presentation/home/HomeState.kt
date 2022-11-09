package com.yuriikonovalov.home.presentation.home

import com.yuriikonovalov.common.application.entities.AvailableBalance
import com.yuriikonovalov.common.application.entities.TransactionAmount
import com.yuriikonovalov.common.application.entities.TransactionItem
import java.time.OffsetDateTime

data class HomeState(
    val availableBalances: List<AvailableBalance> = emptyList(),
    val transactions: List<TransactionItem> = emptyList(),
    val incomes: List<TransactionAmount> = emptyList(),
    val expenses: List<TransactionAmount> = emptyList(),
    val incomesLoading: Boolean = false,
    val expensesLoading: Boolean = false,
    val transactionsLoading: Boolean = false,
    val availableBalancesLoading: Boolean = false,
) {
    val date: OffsetDateTime get() = OffsetDateTime.now()

    val emptyPlaceholderVisible: Boolean get() = availableBalances.isEmpty() && !availableBalancesLoading
    val transactionPlaceholderVisible: Boolean get() = transactions.isEmpty() && !transactionsLoading && !emptyPlaceholderVisible
    val incomePlaceholderVisible: Boolean get() = updateIncomePlaceholderVisible()
    val expensePlaceholderVisible: Boolean get() = updateExpensePlaceholderVisible()
    val transactionsVisible: Boolean get() = transactions.isNotEmpty()
    val incomeExpenseVisible: Boolean get() = transactions.isNotEmpty()

    private fun updateIncomePlaceholderVisible(): Boolean {
        return (incomes.isEmpty() && incomesLoading) || incomes.isEmpty()
    }

    private fun updateExpensePlaceholderVisible(): Boolean {
        return (expenses.isEmpty() && expensesLoading) || expenses.isEmpty()
    }

    fun updateAvailableBalances(availableBalances: List<AvailableBalance>): HomeState {
        return copy(availableBalances = availableBalances, availableBalancesLoading = false)
    }

    fun updateTransactions(transactions: List<TransactionItem>): HomeState {
        return copy(transactions = transactions, transactionsLoading = false)
    }

    fun updateAvailableBalancesLoading(isLoading: Boolean): HomeState {
        return copy(availableBalancesLoading = isLoading)
    }

    fun updateTransactionsLoading(isLoading: Boolean): HomeState {
        return copy(transactionsLoading = isLoading)
    }

    fun updateIncomesLoading(isLoading: Boolean): HomeState {
        return copy(incomesLoading = isLoading)
    }

    fun updateExpensesLoading(isLoading: Boolean): HomeState {
        return copy(expensesLoading = isLoading)
    }

    fun updateIncomes(incomes: List<TransactionAmount>): HomeState {
        return copy(incomes = incomes, incomesLoading = false)
    }

    fun updateExpenses(expenses: List<TransactionAmount>): HomeState {
        return copy(expenses = expenses, expensesLoading = false)
    }
}