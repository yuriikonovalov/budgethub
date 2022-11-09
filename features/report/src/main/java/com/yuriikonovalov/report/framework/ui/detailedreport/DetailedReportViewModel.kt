package com.yuriikonovalov.report.framework.ui.detailedreport

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.yuriikonovalov.common.application.*
import com.yuriikonovalov.common.application.entities.Currency
import com.yuriikonovalov.common.application.entities.PeriodFilter
import com.yuriikonovalov.common.application.entities.account.Account
import com.yuriikonovalov.common.application.usecases.*
import com.yuriikonovalov.common.framework.common.extentions.toStartEndDate
import com.yuriikonovalov.common.framework.utils.date.DateFormat.toCalendarViewDateLong
import com.yuriikonovalov.report.application.usecases.GetAggregateExpenseCategoriesFlatByCurrency
import com.yuriikonovalov.report.application.usecases.GetAggregateIncomeCategoriesFlatByCurrency
import com.yuriikonovalov.report.presentation.detailedreport.DetailedReportEvent
import com.yuriikonovalov.report.presentation.detailedreport.DetailedReportIntent
import com.yuriikonovalov.report.presentation.detailedreport.DetailedReportState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch


class DetailedReportViewModel @AssistedInject constructor(
    @Assisted private val periodFilter: PeriodFilter,
    private val getAllAccounts: GetAllAccounts,
    private val getIncomeForPeriod: GetIncomeForPeriod,
    private val getExpenseForPeriod: GetExpenseForPeriod,
    private val getAggregateIncomeCategoriesFlatByCurrency: GetAggregateIncomeCategoriesFlatByCurrency,
    private val getAggregateExpenseCategoriesFlatByCurrency: GetAggregateExpenseCategoriesFlatByCurrency
) : ViewModel() {
    private val _stateFlow = MutableStateFlow(DetailedReportState())
    private val _eventFlow = MutableStateFlow<DetailedReportEvent?>(null)
    private val currentState get() = stateFlow.value
    val stateFlow = _stateFlow.asStateFlow()
    val eventFlow = _eventFlow.asStateFlow()
    val eventConsumer: () -> Unit = { _eventFlow.value = null }

    private var loadIncomesAndExpensesJob: Job? = null
    private var loadIncomeAggregateCategoriesJob: Job? = null
    private var loadExpenseAggregateCategoriesJob: Job? = null

    init {
        onChangePeriod(periodFilter)
        loadAccounts()
    }

    fun handleIntent(intent: DetailedReportIntent) {
        when (intent) {
            is DetailedReportIntent.ClickCustomPeriod -> onClickCustomPeriod()
            is DetailedReportIntent.ClickPeriodFilter -> onClickPeriodFilter()
            is DetailedReportIntent.ChangePeriodFilter -> onChangePeriod(intent.periodFilter)
            is DetailedReportIntent.ClickAccountFilter -> onClickAccountFilter()
            is DetailedReportIntent.ChangeAccountFilter -> onChangeAccountFilter(intent.accounts)
            is DetailedReportIntent.ChangeIncomeCurrencyFilter -> onChangeIncomeCurrencyFilter(
                intent.currency
            )
            is DetailedReportIntent.ChangeExpenseCurrencyFilter -> onChangeExpenseCurrencyFilter(
                intent.currency
            )
            is DetailedReportIntent.ClickExpenseCurrencyFilter -> onClickExpenseCurrencyFilter()
            is DetailedReportIntent.ClickIncomeCurrencyFilter -> onClickIncomeCurrencyFilter()
            is DetailedReportIntent.ClickIncomeAndExpenseCurrencyFilter -> onClickIncomeAndExpenseCurrencyFilter()
            is DetailedReportIntent.ChangeIncomeAndExpenseCurrencyFilter -> onChangeIncomeAndExpenseCurrencyFilter(
                intent.currency
            )
        }
    }

    private fun onClickPeriodFilter() {
        _eventFlow.value = DetailedReportEvent.ClickPeriodFilter(currentState.periodFilter)
    }

    private fun onClickCustomPeriod() {
        _eventFlow.value = DetailedReportEvent.ClickCustomPeriod(
            startDate = currentState.customPeriod?.first?.toCalendarViewDateLong(),
            endDate = currentState.customPeriod?.second?.toCalendarViewDateLong()
        )
    }

    private fun onChangePeriod(periodFilter: PeriodFilter) {
        _stateFlow.update { it.updatePeriod(periodFilter) }
        loadData()
    }

    private fun onClickAccountFilter() {
        _eventFlow.value = DetailedReportEvent.ClickAccountFilter(
            currentState.accounts,
            currentState.accountFilter
        )
    }

    private fun onChangeAccountFilter(accounts: List<Account>) {
        _stateFlow.update { it.updateAccountFilter(accounts) }
        loadData()
    }

    private fun onChangeIncomeCurrencyFilter(currency: Currency) {
        _stateFlow.update { it.updateIncomeCurrencyFilter(currency) }
    }

    private fun onChangeExpenseCurrencyFilter(currency: Currency) {
        _stateFlow.update { it.updateExpenseCurrencyFilter(currency) }
    }

    private fun onClickIncomeCurrencyFilter() {
        _eventFlow.value =
            DetailedReportEvent.ClickIncomeCurrencyFilter(currentState.incomeAggregateCategoryCurrencies)
    }

    private fun onClickExpenseCurrencyFilter() {
        _eventFlow.value =
            DetailedReportEvent.ClickExpenseCurrencyFilter(currentState.expenseAggregateCategoryCurrencies)
    }


    private fun onChangeIncomeAndExpenseCurrencyFilter(currency: Currency) {
        _stateFlow.update { it.updateIncomeAndExpenseCurrencyFilter(currency) }
    }

    private fun onClickIncomeAndExpenseCurrencyFilter() {
        _eventFlow.value =
            DetailedReportEvent.ClickIncomeAndExpenseCurrencyFilter(currentState.incomeAndExpenseCurrencies)
    }

    private fun loadAccounts() {
        viewModelScope.launch {
            getAllAccounts().collect { resource ->
                resource.onSuccess { list ->
                    _stateFlow.update { it.updateAccounts(list) }
                    // Trigger data loading because the initial loading of accounts updates
                    // the accountFilter from an empty list to all accounts.
                    loadData()
                }
            }
        }
    }

    private fun loadData() {
        loadIncomesAndExpenses()
        loadIncomeAggregateCategories()
        loadExpenseAggregateCategories()
    }


    private fun loadIncomeAggregateCategories() {
        loadIncomeAggregateCategoriesJob?.cancel()
        loadIncomeAggregateCategoriesJob = viewModelScope.launch {
            val resource = getAggregateIncomeCategoriesFlatByCurrency(
                currentState.periodFilter.toStartEndDate().first,
                currentState.periodFilter.toStartEndDate().second,
                currentState.accountFilter
            ).first()

            resource.onSuccess { list ->
                _stateFlow.update { it.updateIncomeAggregateCategories(list) }
            }
            resource.onFailure {
                _stateFlow.update { it.updateIncomeAggregateCategories(emptyList()) }
            }
        }
    }

    private fun loadExpenseAggregateCategories() {
        loadExpenseAggregateCategoriesJob?.cancel()
        loadExpenseAggregateCategoriesJob = viewModelScope.launch {
            val resource = getAggregateExpenseCategoriesFlatByCurrency(
                currentState.periodFilter.toStartEndDate().first,
                currentState.periodFilter.toStartEndDate().second,
                currentState.accountFilter
            ).first()

            resource.onSuccess { list ->
                _stateFlow.update { it.updateExpenseAggregateCategories(list) }
            }
            resource.onFailure {
                _stateFlow.update { it.updateExpenseAggregateCategories(emptyList()) }
            }
        }
    }

    private fun loadIncomesAndExpenses() {
        loadIncomesAndExpensesJob?.cancel()
        loadIncomesAndExpensesJob = viewModelScope.launch {
            _stateFlow.update { it.updateIncomesAndExpensesLoading(true) }

            val incomes = getIncomeForPeriod(
                startPeriod = currentState.periodFilter.toStartEndDate().first,
                endPeriod = currentState.periodFilter.toStartEndDate().second,
                accountFilter = currentState.accountFilter
            ).first()
            val expenses = getExpenseForPeriod(
                startPeriod = currentState.periodFilter.toStartEndDate().first,
                endPeriod = currentState.periodFilter.toStartEndDate().second,
                accountFilter = currentState.accountFilter
            ).first()

            when {
                expenses is Resource.Success && incomes is Resource.Success -> {
                    _stateFlow.update { it.updateIncomesAndExpenses(incomes.data, expenses.data) }
                }
                expenses is Resource.Success -> {
                    _stateFlow.update { it.updateIncomesAndExpenses(emptyList(), expenses.data) }
                }
                incomes is Resource.Success -> {
                    _stateFlow.update { it.updateIncomesAndExpenses(incomes.data, emptyList()) }
                }
                else -> {
                    _stateFlow.update { it.updateIncomesAndExpenses(emptyList(), emptyList()) }
                }
            }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(periodFilter: PeriodFilter): DetailedReportViewModel
    }

    companion object {
        @Suppress("UNCHECKED_CAST")
        fun provideFactory(
            assistedFactory: Factory,
            periodFilter: PeriodFilter
        ) = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(periodFilter) as T
            }
        }
    }
}