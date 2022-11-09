package com.yuriikonovalov.common.framework.ui.transactiondetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.yuriikonovalov.common.application.usecases.DeleteTransaction
import com.yuriikonovalov.common.application.usecases.GetTransaction
import com.yuriikonovalov.common.framework.utils.EspressoIdlingResource
import com.yuriikonovalov.common.presentation.transactiondetails.TransactionDetailsEvent
import com.yuriikonovalov.common.presentation.transactiondetails.TransactionDetailsIntent
import com.yuriikonovalov.common.presentation.transactiondetails.TransactionDetailsState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TransactionDetailsViewModel @AssistedInject constructor(
    @Assisted private val id: Long,
    private val getTransaction: GetTransaction,
    private val deleteTransaction: DeleteTransaction,
    private val idlingResource: EspressoIdlingResource
) : ViewModel() {
    private val _stateFlow = MutableStateFlow(TransactionDetailsState())
    private val _eventFlow = MutableStateFlow<TransactionDetailsEvent?>(null)
    private val currentState get() = stateFlow.value
    val stateFlow get() = _stateFlow.asStateFlow()
    val eventFlow get() = _eventFlow.asStateFlow()
    val eventConsumer: () -> Unit = { _eventFlow.value = null }

    init {
        viewModelScope.launch {
            idlingResource.increment()
            getTransaction(id).collect { resource ->
                resource.onSuccess { transaction ->
                    _stateFlow.update { it.updateTransaction(transaction) }
                }
                idlingResource.decrement()
            }
        }
    }

    fun handleIntent(intent: TransactionDetailsIntent) {
        when (intent) {
            is TransactionDetailsIntent.EditTransaction -> onEditTransaction()
            is TransactionDetailsIntent.DeleteTransaction -> onDeleteTransaction()
        }
    }

    private fun onDeleteTransaction() {
        viewModelScope.launch {
            idlingResource.increment()
            deleteTransaction(currentState.transaction?.id!!)
            _eventFlow.value = TransactionDetailsEvent.NavigateUp
            idlingResource.decrement()
        }
    }

    private fun onEditTransaction() {
        _eventFlow.value = TransactionDetailsEvent.EditTransaction(currentState.transaction?.id!!)
    }

    @AssistedFactory
    interface Factory {
        fun create(id: Long): TransactionDetailsViewModel
    }

    @Suppress("UNCHECKED_CAST")
    companion object {
        fun provideFactory(
            assistedFactory: Factory,
            id: Long
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(id) as T
            }
        }
    }
}