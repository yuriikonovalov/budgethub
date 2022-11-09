package com.yuriikonovalov.common.framework.ui.transferdetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.yuriikonovalov.common.application.usecases.DeleteTransfer
import com.yuriikonovalov.common.application.usecases.GetTransfer
import com.yuriikonovalov.common.presentation.transferdetails.TransferDetailsEvent
import com.yuriikonovalov.common.presentation.transferdetails.TransferDetailsIntent
import com.yuriikonovalov.common.presentation.transferdetails.TransferDetailsState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class TransferDetailsViewModel @AssistedInject constructor(
    @Assisted private val id: Long,
    private val getTransfer: GetTransfer,
    private val deleteTransfer: DeleteTransfer
) : ViewModel() {
    private val _stateFlow = MutableStateFlow(TransferDetailsState())
    private val _eventFlow = MutableStateFlow<TransferDetailsEvent?>(null)
    private val currentState get() = stateFlow.value
    val stateFlow get() = _stateFlow.asStateFlow()
    val eventFlow get() = _eventFlow.asStateFlow()
    val eventConsumer: () -> Unit = { _eventFlow.value = null }

    init {
        viewModelScope.launch {
            getTransfer(id).collect { resource ->
                resource.onSuccess { transfer ->
                    _stateFlow.update { it.updateTransfer(transfer) }
                }
            }
        }
    }

    fun handleIntent(intent: TransferDetailsIntent) {
        when (intent) {
            is TransferDetailsIntent.EditTransfer -> onEditTransfer()
            is TransferDetailsIntent.DeleteTransfer -> onDeleteTransfer()
        }
    }

    private fun onDeleteTransfer() {
        viewModelScope.launch {
            deleteTransfer(currentState.transfer?.id!!)
            _eventFlow.value = TransferDetailsEvent.NavigateUp
        }
    }

    private fun onEditTransfer() {
        _eventFlow.value = TransferDetailsEvent.EditTransfer(currentState.transfer?.id!!)
    }

    @AssistedFactory
    interface Factory {
        fun create(id: Long): TransferDetailsViewModel
    }

    companion object {
        @Suppress("UNCHECKED_CAST")
        fun provideFactory(
            assistedFactory: Factory,
            id: Long
        ) = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(id) as T
            }
        }
    }
}