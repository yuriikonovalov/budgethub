package com.yuriikonovalov.report.presentation.detailedreport

import com.github.mikephil.charting.data.*
import com.yuriikonovalov.common.application.entities.Currency
import com.yuriikonovalov.common.application.entities.TransactionAmount
import com.yuriikonovalov.common.application.entities.PeriodFilter
import com.yuriikonovalov.common.application.entities.account.Account
import com.yuriikonovalov.report.application.entities.AggregateCategoryFlatByCurrency
import com.yuriikonovalov.report.presentation.model.PieChartAggregateCurrencyUi
import java.time.OffsetDateTime

data class DetailedReportState(
    val periodFilter: PeriodFilter = PeriodFilter.Week,
    val customPeriod: Pair<OffsetDateTime, OffsetDateTime>? = null,
    val accounts: List<Account> = emptyList(),
    val accountFilter: List<Account> = emptyList(),
    val incomes: List<TransactionAmount> = emptyList(),
    val expenses: List<TransactionAmount> = emptyList(),
    val incomesLoading: Boolean = true,
    val expensesLoading: Boolean = true,

    val incomeAndExpenseCurrencyFilter: Currency? = null,
    val incomeAndExpenseCurrencies: List<Currency> = emptyList(),
    val incomesFiltered: List<TransactionAmount> = emptyList(),
    val expensesFiltered: List<TransactionAmount> = emptyList(),

    val incomeAggregateCategories: List<AggregateCategoryFlatByCurrency> = emptyList(),
    val incomeAggregateCategoryCurrencies: List<Currency> = emptyList(),
    val incomeCurrencyFilter: Currency? = null,
    val incomeAggregateCategoriesFiltered: List<AggregateCategoryFlatByCurrency> = emptyList(),

    val expenseAggregateCategories: List<AggregateCategoryFlatByCurrency> = emptyList(),
    val expenseAggregateCategoryCurrencies: List<Currency> = emptyList(),
    val expenseCurrencyFilter: Currency? = null,
    val expenseAggregateCategoriesFiltered: List<AggregateCategoryFlatByCurrency> = emptyList()
) {
    val incomeAndExpenseBarCardVisible: Boolean get() = updateIncomeAndExpenseBarCardVisible()
    val incomePieCardVisible: Boolean get() = incomeAggregateCategoriesFiltered.isNotEmpty()
    val expensePieCardVisible: Boolean get() = expenseAggregateCategoriesFiltered.isNotEmpty()
    val incomePieEntries: List<PieEntry> get() = incomeAggregateCategoriesFiltered.toPieEntries()
    val expensePieEntries: List<PieEntry> get() = expenseAggregateCategoriesFiltered.toPieEntries()
    val incomeAndExpenseBarEntries: List<BarEntry> get() = formBarEntries()
    val placeholderVisible: Boolean get() = updatePlaceholderVisible()

    private fun updatePlaceholderVisible(): Boolean {
        return incomes.isEmpty() && incomesLoading.not()
                && expenses.isEmpty() && expensesLoading.not()
                && incomeAggregateCategories.isEmpty() && expenseAggregateCategories.isEmpty()
    }

    private fun updateIncomeAndExpenseBarCardVisible(): Boolean {
        return incomes.isNotEmpty() || expenses.isNotEmpty()
    }

    private fun formBarEntries(): List<BarEntry> {
        return listOf(
            BarEntry(1f, (incomesFiltered.firstOrNull()?.amount ?: 0).toFloat()),
            BarEntry(2f, (expensesFiltered.firstOrNull()?.amount ?: 0).toFloat()),
        )
    }

    private fun List<AggregateCategoryFlatByCurrency>.toPieEntries(): List<PieEntry> {
        val ui = PieChartAggregateCurrencyUi.fromList(this)
        return ui.map {
            PieEntry(it.percent.toFloat(), it.color)
        }
    }

    fun updatePeriod(periodFilter: PeriodFilter): DetailedReportState {
        return if (periodFilter is PeriodFilter.Custom) {
            copy(
                periodFilter = periodFilter,
                customPeriod = Pair(periodFilter.startPeriod, periodFilter.endPeriod)
            )
        } else {
            copy(periodFilter = periodFilter, customPeriod = null)
        }
    }


    fun updateIncomesAndExpenses(
        incomes: List<TransactionAmount>, expenses: List<TransactionAmount>
    ): DetailedReportState {
        val incomeCurrencies =
            incomes.map { it.currency }.toSet().toList().sortedBy { it.code }
        val expenseCurrencies =
            expenses.map { it.currency }.toSet().toList().sortedBy { it.code }
        val incomeAndExpenseCurrencies = incomeCurrencies.union(expenseCurrencies).toList()

        val filter = incomeAndExpenseCurrencies.firstOrNull()

        val incomesFiltered = incomes.filter { it.currency == filter }
        val expensesFiltered = expenses.filter { it.currency == filter }

        return copy(
            expenses = expenses,
            expensesLoading = false,
            incomes = incomes,
            incomesLoading = false,
            incomeAndExpenseCurrencyFilter = filter,
            incomeAndExpenseCurrencies = incomeAndExpenseCurrencies,
            incomesFiltered = incomesFiltered,
            expensesFiltered = expensesFiltered
        )
    }

    fun updateIncomeAndExpenseCurrencyFilter(filter: Currency): DetailedReportState {
        val incomesFiltered = incomes.filter { it.currency == filter }
        val expensesFiltered = expenses.filter { it.currency == filter }

        return copy(
            incomeAndExpenseCurrencyFilter = filter,
            incomesFiltered = incomesFiltered,
            expensesFiltered = expensesFiltered
        )
    }

    fun updateIncomesAndExpensesLoading(isLoading: Boolean): DetailedReportState {
        return copy(incomesLoading = isLoading, expensesLoading = true)
    }


    fun updateAccounts(accounts: List<Account>): DetailedReportState {
        return copy(accounts = accounts)
    }

    fun updateAccountFilter(accounts: List<Account>): DetailedReportState {
        return copy(accountFilter = accounts)
    }


    fun updateIncomeAggregateCategories(aggregateCategories: List<AggregateCategoryFlatByCurrency>): DetailedReportState {
        val currencyFilters =
            aggregateCategories.map { it.currency }.toSet().toList().sortedBy { it.code }
        val filter = currencyFilters.firstOrNull()
        val aggregateCategoriesFiltered = aggregateCategories.filter { it.currency == filter }
        return copy(
            incomeAggregateCategories = aggregateCategories,
            incomeCurrencyFilter = filter,
            incomeAggregateCategoryCurrencies = currencyFilters,
            incomeAggregateCategoriesFiltered = aggregateCategoriesFiltered
        )
    }


    fun updateExpenseAggregateCategories(aggregateCategories: List<AggregateCategoryFlatByCurrency>): DetailedReportState {
        val currencyFilters =
            aggregateCategories.map { it.currency }.toSet().toList().sortedBy { it.code }
        val filter = currencyFilters.firstOrNull()
        val aggregateCategoriesFiltered = aggregateCategories.filter { it.currency == filter }
        return copy(
            expenseAggregateCategories = aggregateCategories,
            expenseCurrencyFilter = filter,
            expenseAggregateCategoryCurrencies = currencyFilters,
            expenseAggregateCategoriesFiltered = aggregateCategoriesFiltered
        )
    }

    fun updateIncomeCurrencyFilter(currency: Currency): DetailedReportState {
        val incomeTransactionsFiltered =
            incomeAggregateCategories.filter { it.currency == currency }
        return copy(
            incomeCurrencyFilter = currency,
            incomeAggregateCategoriesFiltered = incomeTransactionsFiltered
        )
    }

    fun updateExpenseCurrencyFilter(currency: Currency): DetailedReportState {
        val expenseTransactionsFiltered =
            expenseAggregateCategories.filter { it.currency == currency }
        return copy(
            expenseCurrencyFilter = currency,
            expenseAggregateCategoriesFiltered = expenseTransactionsFiltered
        )
    }
}
