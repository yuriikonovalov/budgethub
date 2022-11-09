package com.yuriikonovalov.report.framework.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yuriikonovalov.common.application.entities.PeriodFilter
import com.yuriikonovalov.common.application.usecases.GetExpenseForPeriod
import com.yuriikonovalov.common.application.usecases.GetIncomeForPeriod
import com.yuriikonovalov.common.framework.common.extentions.toStartEndDate
import com.yuriikonovalov.common.framework.utils.date.DateFormat.toCalendarViewDateLong
import com.yuriikonovalov.report.application.usecases.GetCategoriesWithNumberOfTransactions
import com.yuriikonovalov.report.presentation.ReportEvent
import com.yuriikonovalov.report.presentation.ReportIntent
import com.yuriikonovalov.report.presentation.ReportState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class ReportViewModel @Inject constructor(
    private val getCategoriesWithNumberOfTransactions: GetCategoriesWithNumberOfTransactions,
    private val getIncomeForPeriod: GetIncomeForPeriod,
    private val getExpenseForPeriod: GetExpenseForPeriod
) : ViewModel() {
    private val _stateFlow = MutableStateFlow(ReportState())
    private val _eventFlow = MutableStateFlow<ReportEvent?>(null)
    private val currentState get() = stateFlow.value
    val stateFlow = _stateFlow.asStateFlow()
    val eventFlow = _eventFlow.asStateFlow()
    val eventConsumer: () -> Unit = { _eventFlow.value = null }

    private var loadCategoriesJob: Job? = null
    private var loadExpensesJob: Job? = null
    private var loadIncomesJob: Job? = null

    init {
        checkInitialPeriod()
    }

    fun handleIntent(intent: ReportIntent) {
        when (intent) {
            is ReportIntent.ChangePeriod -> onChangePeriod(intent.periodFilter)
            is ReportIntent.ClickReportButton -> onClickReportButton()
            is ReportIntent.ClickSeeAllButton -> onClickSeeAllButton()
            is ReportIntent.ClickCustomPeriod -> onClickCustomPeriod()
        }
    }

    private fun checkInitialPeriod() {
        _eventFlow.value = ReportEvent.CheckInitialPeriod(currentState.periodFilter)
        // Trigger data loading because loading for the custom filter is triggered
        // after selection a date range in a calendar dialog.
        if (currentState.periodFilter is PeriodFilter.Custom) {
            loadData()
        }
    }

    private fun onClickCustomPeriod() {
        _eventFlow.value = ReportEvent.ClickCustomPeriod(
            startDate = currentState.customPeriod?.first?.toCalendarViewDateLong(),
            endDate = currentState.customPeriod?.second?.toCalendarViewDateLong()
        )
    }

    private fun onChangePeriod(periodFilter: PeriodFilter) {
        _stateFlow.update { it.updatePeriod(periodFilter) }
        loadData()
    }

    private fun onClickSeeAllButton() {
        _eventFlow.value = ReportEvent.ClickSeeAllButton(currentState.periodFilter)
    }

    private fun onClickReportButton() {
        _eventFlow.value = ReportEvent.ClickReportButton(currentState.periodFilter)
    }

    private fun loadData() {
        loadCategories()
        loadIncomes()
        loadExpenses()
    }

    private fun loadIncomes() {
        _stateFlow.update { it.updateIncomesLoading(true) }
        loadIncomesJob?.cancel()
        loadIncomesJob = viewModelScope.launch {
            getIncomeForPeriod(
                startPeriod = currentState.periodFilter.toStartEndDate().first,
                endPeriod = currentState.periodFilter.toStartEndDate().second
            ).collect { resource ->
                resource.onSuccess { incomes ->
                    _stateFlow.update { it.updateIncomes(incomes) }
                }
                resource.onFailure {
                    _stateFlow.update { it.updateIncomes(emptyList()) }
                }
            }
        }
    }


    private fun loadExpenses() {
        _stateFlow.update { it.updateExpensesLoading(true) }
        loadExpensesJob?.cancel()
        loadExpensesJob = viewModelScope.launch {
            getExpenseForPeriod(
                startPeriod = currentState.periodFilter.toStartEndDate().first,
                endPeriod = currentState.periodFilter.toStartEndDate().second
            ).collect { resource ->
                resource.onSuccess { expenses ->
                    _stateFlow.update { it.updateExpenses(expenses) }
                }
                resource.onFailure {
                    _stateFlow.update { it.updateExpenses(emptyList()) }
                }
            }
        }
    }

    private fun loadCategories() {
        _stateFlow.update { it.updateCategoriesLoading(true) }
        loadCategoriesJob?.cancel()
        loadCategoriesJob = viewModelScope.launch {
            getCategoriesWithNumberOfTransactions(
                startPeriod = currentState.periodFilter.toStartEndDate().first,
                endPeriod = currentState.periodFilter.toStartEndDate().second
            ).collect { resource ->
                resource.onSuccess { list ->
                    _stateFlow.update { it.updateCategories(list) }
                }
                resource.onFailure {
                    _stateFlow.update { it.updateCategories(emptyList()) }
                }
            }
        }
    }
}