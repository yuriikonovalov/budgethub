package com.yuriikonovalov.home.framework.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yuriikonovalov.common.application.usecases.GetAvailableBalances
import com.yuriikonovalov.common.application.usecases.GetExpenseForPeriod
import com.yuriikonovalov.common.application.usecases.GetIncomeForPeriod
import com.yuriikonovalov.common.application.usecases.GetTransactionItemsForPeriod
import com.yuriikonovalov.common.application.util.beginningOfMonth
import com.yuriikonovalov.common.application.util.endOfMonth
import com.yuriikonovalov.home.presentation.home.HomeEvent
import com.yuriikonovalov.home.presentation.home.HomeIntent
import com.yuriikonovalov.home.presentation.home.HomeState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.OffsetDateTime
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getTransactionItemsForPeriod: GetTransactionItemsForPeriod,
    private val getAvailableBalances: GetAvailableBalances,
    private val getIncomeForPeriod: GetIncomeForPeriod,
    private val getExpenseForPeriod: GetExpenseForPeriod
) : ViewModel() {
    private val _stateFlow = MutableStateFlow(HomeState())
    private val _eventFlow = MutableStateFlow<HomeEvent?>(null)
    val stateFlow get() = _stateFlow.asStateFlow()
    val eventFlow get() = _eventFlow.asStateFlow()
    val eventConsumer: () -> Unit = { _eventFlow.value = null }

    private val startPeriod = OffsetDateTime.now().beginningOfMonth()
    private val endPeriod = OffsetDateTime.now().endOfMonth()

    init {
        loadAvailableBalances()
        loadIncomes()
        loadExpenses()
        loadTransactions()
    }

    fun handleIntent(intent: HomeIntent) {
        when (intent) {
            is HomeIntent.OpenTransactionDetails -> onOpenTransactionDetails(intent.id)
            is HomeIntent.OpenTransferDetails -> onOpenTransferDetails(intent.id)
            is HomeIntent.ClickAvailableBalancePercent -> onClickAvailableBalancePercent()
        }
    }

    private fun onOpenTransferDetails(id: Long) {
        _eventFlow.value = HomeEvent.OpenTransferDetails(id)
    }

    private fun onOpenTransactionDetails(id: Long) {
        _eventFlow.value = HomeEvent.OpenTransactionDetails(id)
    }

    private fun onClickAvailableBalancePercent() {
        _eventFlow.value = HomeEvent.ClickAvailableBalancePercent
    }

    private fun loadTransactions() {
        _stateFlow.update { it.updateTransactionsLoading(true) }
        viewModelScope.launch {
            getTransactionItemsForPeriod(
                startPeriod = startPeriod,
                endPeriod = endPeriod
            ).collect { resource ->
                resource.onSuccess { list -> _stateFlow.update { it.updateTransactions(list) } }
                resource.onFailure { _stateFlow.update { it.updateTransactions(emptyList()) } }
            }
        }
    }


    private fun loadAvailableBalances() {
        _stateFlow.update { it.updateAvailableBalancesLoading(true) }
        viewModelScope.launch {
            getAvailableBalances(
                startPeriod = startPeriod,
                endPeriod = endPeriod
            ).collect { resource ->
                resource.onSuccess { list -> _stateFlow.update { it.updateAvailableBalances(list) } }
                resource.onFailure { _stateFlow.update { it.updateAvailableBalances(emptyList()) } }
            }
        }
    }

    private fun loadExpenses() {
        _stateFlow.update { it.updateExpensesLoading(true) }
        viewModelScope.launch {
            getExpenseForPeriod(startPeriod, endPeriod).collect { resource ->
                resource.onSuccess { expenses ->
                    _stateFlow.update { it.updateExpenses(expenses) }
                }
                resource.onFailure {
                    _stateFlow.update { it.updateExpenses(emptyList()) }
                }
            }
        }
    }

    private fun loadIncomes() {
        _stateFlow.update { it.updateIncomesLoading(true) }
        viewModelScope.launch {
            getIncomeForPeriod(startPeriod, endPeriod).collect { resource ->
                resource.onSuccess { incomes ->
                    _stateFlow.update { it.updateIncomes(incomes) }
                }
                resource.onFailure {
                    _stateFlow.update { it.updateIncomes(emptyList()) }
                }
            }
        }
    }
}