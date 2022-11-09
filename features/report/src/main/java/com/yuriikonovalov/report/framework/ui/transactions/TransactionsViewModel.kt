package com.yuriikonovalov.report.framework.ui.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.yuriikonovalov.common.application.entities.PeriodFilter
import com.yuriikonovalov.common.application.entities.Tag
import com.yuriikonovalov.common.application.entities.account.Account
import com.yuriikonovalov.common.application.usecases.GetAllAccounts
import com.yuriikonovalov.common.application.usecases.GetTags
import com.yuriikonovalov.common.framework.common.extentions.toStartEndDate
import com.yuriikonovalov.common.framework.utils.date.DateFormat.toCalendarViewDateLong
import com.yuriikonovalov.report.application.entities.TypeFilter
import com.yuriikonovalov.report.application.usecases.GetTransactionsPaged
import com.yuriikonovalov.report.application.usecases.GetTransfersPaged
import com.yuriikonovalov.report.presentation.transactions.TransactionsEvent
import com.yuriikonovalov.report.presentation.transactions.TransactionsIntent
import com.yuriikonovalov.report.presentation.transactions.TransactionsState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class TransactionsViewModel @AssistedInject constructor(
    @Assisted private val period: PeriodFilter,
    private val getAllAccounts: GetAllAccounts,
    private val getTags: GetTags,
    private val getTransactionsPaged: GetTransactionsPaged,
    private val getTransfersPaged: GetTransfersPaged
) : ViewModel() {
    private val _stateFlow = MutableStateFlow(TransactionsState())
    private val _eventFlow = MutableStateFlow<TransactionsEvent?>(null)
    private val currentState get() = stateFlow.value
    val stateFlow get() = _stateFlow.asStateFlow()
    val eventFlow get() = _eventFlow.asStateFlow()
    val eventConsumer = { _eventFlow.value = null }

    private val search = MutableSharedFlow<Unit>(replay = 1)
    init {
        _stateFlow.update { it.updatePeriod(period) }
        applySearchFilters()
        loadAccounts()
        loadTags()
    }

    fun handleIntent(intent: TransactionsIntent) {
        when (intent) {
            is TransactionsIntent.ChangePeriodFilter -> onChangePeriodFilter(intent.periodFilter)
            is TransactionsIntent.ChangeAccountFilter -> onChangeAccountFilter(intent.accounts)
            is TransactionsIntent.ChangeTagFilter -> onChangeTagFilter(intent.tags)
            is TransactionsIntent.ChangeTypeFilter -> onChangeTypeFilter(intent.type)
            is TransactionsIntent.ClickAccountFilter -> onClickAccountFilter()
            is TransactionsIntent.ClickCustomPeriod -> onClickCustomPeriod()
            is TransactionsIntent.ClickPeriodFilter -> onClickPeriodFilter()
            is TransactionsIntent.ClickTagFilter -> onClickTagFilter()
            is TransactionsIntent.ClickTypeFilter -> onClickTypeFilter()
            is TransactionsIntent.OpenTransactionDetails -> onOpenTransactionDetails(intent.id)
            is TransactionsIntent.OpenTransferDetails -> onOpenTransferDetails(intent.id)
        }
    }

    private fun onChangePeriodFilter(periodFilter: PeriodFilter) {
        _stateFlow.update { it.updatePeriod(periodFilter) }
        applySearchFilters()
    }

    private fun onChangeAccountFilter(selectedAccounts: List<Account>) {
        _stateFlow.update { it.updateAccountFilter(selectedAccounts) }
        applySearchFilters()
    }

    private fun onChangeTagFilter(selectedTags: List<Tag>) {
        _stateFlow.update { it.updateTagFilter(selectedTags) }
        applySearchFilters()
    }

    private fun onChangeTypeFilter(type: TypeFilter) {
        _stateFlow.update { it.updateTypeFilter(type) }
        applySearchFilters()
    }

    private fun onClickAccountFilter() {
        _eventFlow.value =
            TransactionsEvent.ClickAccountFilter(currentState.accounts, currentState.accountFilter)
    }

    private fun onClickCustomPeriod() {
        _eventFlow.value = TransactionsEvent.ClickCustomPeriod(
            currentState.customPeriod?.first?.toCalendarViewDateLong(),
            currentState.customPeriod?.second?.toCalendarViewDateLong()
        )
    }

    private fun onClickPeriodFilter() {
        _eventFlow.value = TransactionsEvent.ClickPeriodFilter(currentState.periodFilter)
    }

    private fun onClickTagFilter() {
        _eventFlow.value =
            TransactionsEvent.ClickTagFilter(currentState.tags, currentState.tagFilter)
    }

    private fun onClickTypeFilter() {
        _eventFlow.value = TransactionsEvent.ClickTypeFilter(currentState.typeFilter)
    }

    private fun onOpenTransactionDetails(id: Long) {
        _eventFlow.value = TransactionsEvent.OpenTransactionDetails(id)
    }

    private fun onOpenTransferDetails(id: Long) {
        _eventFlow.value = TransactionsEvent.OpenTransferDetails(id)
    }

    val transactionPagingData = search.flatMapLatest {
        getTransactionsPaged(
            currentState.periodFilter.toStartEndDate().first,
            currentState.periodFilter.toStartEndDate().second,
            currentState.typeFilter,
            currentState.accountFilter,
            currentState.tagFilter
        )
    }.cachedIn(viewModelScope)

    val transferPagingData = search.flatMapLatest {
        getTransfersPaged(
            currentState.periodFilter.toStartEndDate().first,
            currentState.periodFilter.toStartEndDate().second,
            currentState.typeFilter,
            currentState.accountFilter,
            currentState.tagFilter
        )
    }.cachedIn(viewModelScope)

    private fun applySearchFilters() {
        viewModelScope.launch { search.emit(Unit) }
    }

    private fun loadTags() {
        viewModelScope.launch {
            getTags().collect { resource ->
                resource.onSuccess { list -> _stateFlow.update { it.updateTags(list) } }
                resource.onFailure { _stateFlow.update { it.updateTags(emptyList()) } }
            }
        }
    }

    private fun loadAccounts() {
        viewModelScope.launch {
            getAllAccounts().collect { resource ->
                resource.onSuccess { list -> _stateFlow.update { it.updateAccounts(list) } }
                resource.onFailure { _stateFlow.update { it.updateAccounts(emptyList()) } }
            }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(period: PeriodFilter): TransactionsViewModel
    }

    companion object {
        @Suppress("UNCHECKED_CAST")
        fun provideFactory(
            assistedFactory: Factory,
            period: PeriodFilter
        ) = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(period) as T
            }
        }
    }
}