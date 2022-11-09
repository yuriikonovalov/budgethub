package com.yuriikonovalov.common.framework.ui.addedittransaction

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.yuriikonovalov.common.application.entities.Tag
import com.yuriikonovalov.common.application.entities.account.Account
import com.yuriikonovalov.common.application.entities.category.Category
import com.yuriikonovalov.common.application.entities.category.CategoryType
import com.yuriikonovalov.common.application.entities.transaction.Transaction
import com.yuriikonovalov.common.application.entities.transaction.TransactionType
import com.yuriikonovalov.common.application.usecases.*
import com.yuriikonovalov.common.framework.utils.EspressoIdlingResource
import com.yuriikonovalov.common.presentation.addedittransaction.AddEditTransactionEvent
import com.yuriikonovalov.common.presentation.addedittransaction.AddEditTransactionIntent
import com.yuriikonovalov.common.presentation.addedittransaction.AddEditTransactionState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.OffsetDateTime


class AddEditTransactionViewModel @AssistedInject constructor(
    @Assisted private val id: Long,
    private val getAllAccounts: GetAllAccounts,
    private val saveTransaction: SaveTransaction,
    private val getTransaction: GetTransaction,
    private val updateTransaction: UpdateTransaction,
    getIncomeCategories: GetIncomeCategories,
    getExpenseCategories: GetExpenseCategories,
    private val idlingResource: EspressoIdlingResource
) : ViewModel() {
    private val _stateFlow = MutableStateFlow(AddEditTransactionState())
    private val currentState get() = stateFlow.value
    private val _eventFlow = MutableStateFlow<AddEditTransactionEvent?>(null)
    val stateFlow get() = _stateFlow.asStateFlow()
    val eventFlow get() = _eventFlow.asStateFlow()
    val eventConsumer: () -> Unit = { _eventFlow.value = null }

    init {
        loadAccounts()
        loadEditTransaction()

        viewModelScope.launch {
            idlingResource.increment()
            getIncomeCategories().collect { resource ->
                resource.onSuccess { list -> _stateFlow.update { it.updateIncomeCategories(list) } }
                resource.onFailure { _stateFlow.update { it.updateIncomeCategories(emptyList()) } }
                idlingResource.decrement()
            }
        }

        viewModelScope.launch {
            idlingResource.increment()
            getExpenseCategories().collect { resource ->
                resource.onSuccess { list -> _stateFlow.update { it.updateExpenseCategories(list) } }
                resource.onFailure { _stateFlow.update { it.updateExpenseCategories(emptyList()) } }
                idlingResource.decrement()
            }
        }
    }

    private fun loadEditTransaction() {
        if (id != AddEditTransactionFragment.ARGUMENT_NEW_TRANSACTION) {
            viewModelScope.launch {
                idlingResource.increment()
                getTransaction(id).first().onSuccess { transaction ->
                    _stateFlow.update { it.updateAll(transaction) }
                    _eventFlow.value = AddEditTransactionEvent.EditModeInput(
                        type = currentState.type,
                        amount = currentState.amount!!
                    )
                    idlingResource.decrement()
                }
            }
        }
    }

    fun handleIntent(intent: AddEditTransactionIntent) {
        when (intent) {
            is AddEditTransactionIntent.ChangeType -> onChangeType(intent.type)
            is AddEditTransactionIntent.ChangeAccount -> onChangeAccount(intent.account)
            is AddEditTransactionIntent.ChangeAmount -> onChangeAmount(intent.amount)
            is AddEditTransactionIntent.ChangeCategory -> onChangeCategory(intent.category)
            is AddEditTransactionIntent.ChangeNote -> onChangeNote(intent.note)
            is AddEditTransactionIntent.ChangeDate -> onChangeDate(intent.date)
            is AddEditTransactionIntent.ChangeImage -> onChangeImage(intent.imageUri)
            is AddEditTransactionIntent.ChangeTags -> onChangeTags(intent.tags)
            is AddEditTransactionIntent.ClickCategory -> onClickCategoryButton()
            is AddEditTransactionIntent.ClickNoteButton -> onClickNoteButton()
            is AddEditTransactionIntent.ClickDateButton -> onClickDateButton()
            is AddEditTransactionIntent.ClickPhotoButton -> onClickPhotoButton()
            is AddEditTransactionIntent.ClickTagButton -> onClickTagButton()
            is AddEditTransactionIntent.ClickSaveButton -> onClickSaveButton()
            is AddEditTransactionIntent.ClickAccount -> onClickAccount()
        }
    }

    private fun onClickAccount() {
        if (currentState.account == null) {
            _eventFlow.value = AddEditTransactionEvent.CreateAccount
        } else {
            _eventFlow.value = AddEditTransactionEvent.ShowAccounts(currentState.accounts)
        }
    }

    private fun onChangeType(type: TransactionType) {
        _stateFlow.update { it.updateType(type) }
    }

    private fun onChangeAccount(account: Account) {
        _stateFlow.update { it.updateAccount(account) }
    }

    private fun onChangeAmount(amount: Double?) {
        _stateFlow.update { it.updateAmount(amount) }
    }

    private fun onChangeCategory(category: Category) {
        _stateFlow.update { it.updateCategory(category) }
    }

    private fun onChangeNote(note: String?) {
        _stateFlow.update { it.updateNote(note) }
    }

    private fun onChangeDate(date: OffsetDateTime) {
        _stateFlow.update { it.updateDate(date) }
    }

    private fun onChangeImage(imageUri: Uri?) {
        _stateFlow.update { it.updateImageUri(imageUri = imageUri) }
    }

    private fun onChangeTags(tags: List<Tag>) {
        _stateFlow.update { it.updateTags(tags) }
    }


    private fun onClickCategoryButton() {
        val categoryType = when (currentState.type) {
            TransactionType.INCOME -> CategoryType.INCOME
            TransactionType.EXPENSE -> CategoryType.EXPENSE
        }
        _eventFlow.value = AddEditTransactionEvent.ShowCategories(type = categoryType)
    }

    private fun onClickNoteButton() {
        _eventFlow.value = AddEditTransactionEvent.NoteButtonClick(note = currentState.note)
    }

    private fun onClickDateButton() {
        _eventFlow.value = AddEditTransactionEvent.DateButtonClick(
            minDate = currentState.minDate, date = currentState.date
        )
    }

    private fun onClickPhotoButton() {
        _eventFlow.value =
            AddEditTransactionEvent.PhotoButtonClick(imageUri = currentState.imageUri)
    }

    private fun onClickTagButton() {
        _eventFlow.value = AddEditTransactionEvent.TagButtonClick(currentState.tags)
    }

    private fun onClickSaveButton() {
        when (currentState.mode) {
            AddEditTransactionState.Mode.ADD -> addTransaction()
            AddEditTransactionState.Mode.EDIT -> updateTransaction()
        }
    }

    private fun updateTransaction() {
        viewModelScope.launch {
            idlingResource.increment()
            updateTransaction(
                currentState.toTransaction(), currentState.imageUri
            ).onSuccess {
                _eventFlow.value = AddEditTransactionEvent.NavigateBack
                idlingResource.decrement()
            }
        }
    }

    private fun addTransaction() {
        viewModelScope.launch {
            idlingResource.increment()
            saveTransaction(currentState.toTransaction(), currentState.imageUri).onSuccess {
                _eventFlow.value = AddEditTransactionEvent.NavigateBack
            }
            idlingResource.decrement()
        }
    }


    private fun loadAccounts() {
        viewModelScope.launch {
            idlingResource.increment()
            getAllAccounts().collect { resource ->
                resource.onSuccess { accounts -> _stateFlow.update { it.updateAccounts(accounts) } }
                resource.onFailure { _stateFlow.update { it.updateAccounts(emptyList()) } }
                idlingResource.decrement()
            }
        }
    }


    private fun AddEditTransactionState.toTransaction(): Transaction {
        return Transaction(
            id = id,
            account = account!!,
            type = type,
            category = category!!,
            tags = tags,
            note = note,
            date = date,
            amount = amount!!,
            imagePath = null
        )
    }

    @AssistedFactory
    interface Factory {
        fun create(id: Long): AddEditTransactionViewModel
    }

    @Suppress("UNCHECKED_CAST")
    companion object {
        fun provideFactory(
            assistedFactory: Factory, id: Long
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(id) as T
            }
        }
    }
}