package com.yuriikonovalov.report.presentation

import com.yuriikonovalov.common.application.entities.TransactionAmount
import com.yuriikonovalov.report.application.entities.CategoryWithNumberOfTransactions
import com.yuriikonovalov.common.application.entities.PeriodFilter
import java.time.OffsetDateTime

data class ReportState(
    val periodFilter: PeriodFilter = PeriodFilter.Week,
    val customPeriod: Pair<OffsetDateTime, OffsetDateTime>? = null,
    val incomes: List<TransactionAmount> = emptyList(),
    val expenses: List<TransactionAmount> = emptyList(),
    val incomesLoading: Boolean = true,
    val expensesLoading: Boolean = true,
    val categoriesLoading: Boolean = true,
    val categories: List<CategoryWithNumberOfTransactions> = emptyList()
) {
    val incomePlaceholderVisible: Boolean get() = updateIncomePlaceholderVisible()
    val expensePlaceholderVisible: Boolean get() = updateExpensePlaceholderVisible()
    val placeholderVisible: Boolean get() = updatePlaceholderVisible()

    private fun updatePlaceholderVisible(): Boolean {
        return (categories.isEmpty() && !categoriesLoading) || categories.isEmpty()
    }

    private fun updateIncomePlaceholderVisible(): Boolean {
        return (incomes.isEmpty() && !incomesLoading) || incomes.isEmpty()
    }

    private fun updateExpensePlaceholderVisible(): Boolean {
        return (expenses.isEmpty() && !expensesLoading) || expenses.isEmpty()
    }

    fun updatePeriod(periodFilter: PeriodFilter): ReportState {
        return if (periodFilter is PeriodFilter.Custom) {
            copy(
                periodFilter = periodFilter,
                customPeriod = Pair(periodFilter.startPeriod, periodFilter.endPeriod)
            )
        } else {
            copy(periodFilter = periodFilter, customPeriod = null)
        }
    }

    fun updateIncomes(incomes: List<TransactionAmount>): ReportState {
        return copy(incomes = incomes, incomesLoading = false)
    }

    fun updateExpenses(expenses: List<TransactionAmount>): ReportState {
        return copy(expenses = expenses, expensesLoading = false)
    }

    fun updateCategories(categories: List<CategoryWithNumberOfTransactions>): ReportState {
        return copy(categories = categories, categoriesLoading = false)
    }

    fun updateIncomesLoading(isLoading: Boolean): ReportState {
        return copy(incomesLoading = isLoading)
    }

    fun updateExpensesLoading(isLoading: Boolean): ReportState {
        return copy(expensesLoading = isLoading)
    }

    fun updateCategoriesLoading(isLoading: Boolean): ReportState {
        return copy(categoriesLoading = isLoading)
    }
}
