package com.yuriikonovalov.common.framework.ui.addedittransaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.yuriikonovalov.common.application.entities.Tag
import com.yuriikonovalov.common.application.entities.Transfer
import com.yuriikonovalov.common.application.entities.account.Account
import com.yuriikonovalov.common.application.usecases.*
import com.yuriikonovalov.common.presentation.addedittransaction.AddEditTransferEvent
import com.yuriikonovalov.common.presentation.addedittransaction.AddEditTransferIntent
import com.yuriikonovalov.common.presentation.addedittransaction.AddEditTransferState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.OffsetDateTime

class AddEditTransferViewModel @AssistedInject constructor(
    @Assisted private val id: Long,
    private val getAllAccounts: GetAllAccounts,
    private val saveTransfer: SaveTransfer,
    private val getTransfer: GetTransfer,
    private val updateTransfer: UpdateTransfer
) : ViewModel() {
    private val _stateFlow = MutableStateFlow(AddEditTransferState())
    private val currentState get() = stateFlow.value
    private val _eventFlow = MutableStateFlow<AddEditTransferEvent?>(null)
    val eventFlow get() = _eventFlow.asStateFlow()
    val stateFlow get() = _stateFlow.asStateFlow()
    val eventConsumer: () -> Unit = { _eventFlow.value = null }

    init {
        loadAccounts()
        loadEditTransfer()
    }

    fun handleIntent(intent: AddEditTransferIntent) {
        when (intent) {
            is AddEditTransferIntent.ChangeAccountFrom -> onChangeAccountFrom(intent.accountFrom)
            is AddEditTransferIntent.ChangeAccountTo -> onChangeAccountTo(intent.accountTo)
            is AddEditTransferIntent.ChangeAmountFrom -> onChangeAmountFrom(intent.amountFrom)
            is AddEditTransferIntent.ChangeAmountTo -> onChangeAmountTo(intent.amountTo)
            is AddEditTransferIntent.ChangeNote -> onChangeNote(intent.note)
            is AddEditTransferIntent.ChangeDate -> onChangeDate(intent.date)
            is AddEditTransferIntent.ChangeTags -> onChangeTags(intent.tags)
            is AddEditTransferIntent.ClickAccountFrom -> onClickAccountFrom()
            is AddEditTransferIntent.ClickAccountTo -> onClickAccountTo()
            is AddEditTransferIntent.ClickNoteButton -> onClickNoteButton()
            is AddEditTransferIntent.ClickDateButton -> onClickDateButton()
            is AddEditTransferIntent.ClickTagButton -> onClickTagButton()
            is AddEditTransferIntent.ClickSaveButton -> onClickSaveButton()
        }
    }

    private fun onChangeAccountFrom(account: Account) {
        _stateFlow.update { it.updateAccountFrom(account) }
    }

    private fun onChangeAccountTo(account: Account) {
        _stateFlow.update { it.updateAccountTo(account) }
    }

    private fun onChangeAmountFrom(amount: Double?) {
        _stateFlow.update { it.updateAmountFrom(amount) }
    }

    private fun onChangeAmountTo(amount: Double?) {
        _stateFlow.update { it.updateAmountTo(amount) }
    }

    private fun onChangeNote(note: String?) {
        _stateFlow.update { it.updateNote(note) }
    }

    private fun onChangeDate(date: OffsetDateTime) {
        _stateFlow.update { it.updateDate(date) }
    }

    private fun onChangeTags(tags: List<Tag>) {
        _stateFlow.update { it.updateTags(tags) }
    }

    private fun onClickAccountFrom() {
        _eventFlow.value = AddEditTransferEvent.AccountFromClick(currentState.accounts)
    }

    private fun onClickAccountTo() {
        val leftAccounts = currentState.accounts.filter { it != currentState.accountFrom }
        _eventFlow.value = AddEditTransferEvent.AccountToClick(leftAccounts)
    }

    private fun onClickNoteButton() {
        _eventFlow.value = AddEditTransferEvent.NoteButtonClick(note = currentState.note)
    }

    private fun onClickDateButton() {
        _eventFlow.value = AddEditTransferEvent.DateButtonClick(
            minDate = currentState.minDate,
            date = currentState.date
        )
    }

    private fun onClickTagButton() {
        _eventFlow.value = AddEditTransferEvent.TagButtonClick(currentState.tags)
    }

    private fun onClickSaveButton() {
        when (currentState.mode) {
            AddEditTransferState.Mode.ADD -> addTransfer()
            AddEditTransferState.Mode.EDIT -> updateTransfer()
        }
    }

    private fun updateTransfer() {
        viewModelScope.launch {
            val resource = updateTransfer(currentState.toTransferTransaction())
            resource.onSuccess {
                _eventFlow.value = AddEditTransferEvent.NavigateBack
            }
        }
    }

    private fun addTransfer() {
        viewModelScope.launch {
            val resource = saveTransfer(currentState.toTransferTransaction())
            resource.onSuccess {
                _eventFlow.value = AddEditTransferEvent.NavigateBack
            }
        }
    }

    private fun loadAccounts() {
        viewModelScope.launch {
            getAllAccounts().collect { resource ->
                resource.onSuccess { accounts ->
                    _stateFlow.update { it.updateAccounts(accounts) }
                }
            }
        }
    }

    private fun loadEditTransfer() {
        if (id != AddEditTransferFragment.ARGUMENT_NEW_TRANSFER) {
            viewModelScope.launch {
                getTransfer(id).first().onSuccess { transfer ->
                    _stateFlow.update { it.updateAll(transfer) }
                    _eventFlow.value = AddEditTransferEvent.EditModeInput(
                        amountFrom = currentState.amountFrom!!,
                        amountTo = currentState.amountTo!!
                    )
                }
            }
        }
    }

    private fun AddEditTransferState.toTransferTransaction(): Transfer {
        val amountTo = if (currentState.differentCurrencies) {
            amountTo!!
        } else {
            amountFrom!!
        }
        return Transfer(
            id = id ?: 0,
            accountFrom = accountFrom!!,
            accountTo = accountTo!!,
            amountFrom = amountFrom!!,
            amountTo = amountTo,
            date = date,
            note = note,
            tags = tags
        )
    }

    @AssistedFactory
    interface Factory {
        fun create(id: Long): AddEditTransferViewModel
    }

    @Suppress("UNCHECKED_CAST")
    companion object {
        fun provideFactory(assistedFactory: Factory, id: Long): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return assistedFactory.create(id) as T
                }
            }
    }
}