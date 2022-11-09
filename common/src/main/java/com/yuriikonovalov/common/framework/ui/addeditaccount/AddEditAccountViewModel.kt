package com.yuriikonovalov.common.framework.ui.addeditaccount

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.yuriikonovalov.common.application.entities.Currency
import com.yuriikonovalov.common.application.entities.account.Account
import com.yuriikonovalov.common.application.entities.account.AccountType
import com.yuriikonovalov.common.application.usecases.*
import com.yuriikonovalov.common.framework.utils.money.MoneyFormat
import com.yuriikonovalov.common.presentation.addeditaccount.AddEditAccountEvent
import com.yuriikonovalov.common.presentation.addeditaccount.AddEditAccountIntent
import com.yuriikonovalov.common.presentation.addeditaccount.AddEditAccountState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.OffsetDateTime


class AddEditAccountViewModel @AssistedInject constructor(
    @Assisted private val id: Long,
    private val saveAccount: SaveAccount,
    private val updateAccount: UpdateAccount,
    private val getAccount: GetAccount,
    getAllCurrencies: GetAllCurrencies,
    getColors: GetColors
) : ViewModel() {
    private val _stateFlow = MutableStateFlow(AddEditAccountState())
    private val _eventFlow = MutableStateFlow<AddEditAccountEvent?>(null)
    private val currentState get() = stateFlow.value
    val stateFlow = _stateFlow.asStateFlow()
    val eventFlow = _eventFlow.asStateFlow()
    val eventConsumer: () -> Unit = { _eventFlow.value = null }

    init {
        if (id != AddEditAccountFragment.ARGUMENT_NEW_ACCOUNT || id != AddEditAccountFragment.ARGUMENT_ONBOARDING) {
            viewModelScope.launch {
                getAccount(id).onSuccess { account ->
                    _stateFlow.update { it.updateAll(account) }
                    _eventFlow.value = AddEditAccountEvent.InputName(account.name)
                }
            }
        }

        _stateFlow.update { it.updateColors(getColors()) }

        getAllCurrencies().onSuccess { currencies ->
            _stateFlow.update { state -> state.updateCurrency(currencies.find { it.code == "USD" }) }
        }
    }

    fun handleIntent(intent: AddEditAccountIntent) {
        when (intent) {
            is AddEditAccountIntent.ChangeName -> onChangeName(intent.input)
            is AddEditAccountIntent.ChangeBalance -> onChangeBalance(intent.input)
            is AddEditAccountIntent.ChangeType -> onChangeType(intent.type)
            is AddEditAccountIntent.ChangeCurrency -> onChangeCurrency(intent.currency)
            is AddEditAccountIntent.ClickSaveButton -> onClickSaveButton()
            is AddEditAccountIntent.ChangeColor -> onChangeColor(intent.color)
            is AddEditAccountIntent.ClickCurrencyButton -> onClickCurrencyButton()
            is AddEditAccountIntent.ClickColorButton -> onClickColorButton()
            is AddEditAccountIntent.AddColor -> onAddColor(intent.color)
        }
    }

    private fun onAddColor(color: Int) {
        _stateFlow.update { it.addColor(color) }
    }

    private fun onChangeName(name: String) {
        _stateFlow.value = currentState.updateName(name)
    }

    private fun onChangeBalance(input: String) {
        val balance = MoneyFormat.getDoubleValue(input) ?: 0.0
        _stateFlow.value = currentState.updateBalance(balance)
    }

    private fun onChangeType(type: AccountType) {
        _stateFlow.value = currentState.updateType(type)
    }

    private fun onChangeCurrency(currency: Currency) {
        _stateFlow.value = currentState.updateCurrency(currency)
    }

    private fun onChangeColor(color: Int) {
        _stateFlow.update { it.updateColor(color) }
    }

    private fun onClickCurrencyButton() {
        _eventFlow.value = AddEditAccountEvent.CurrencyButtonClick
    }

    private fun onClickColorButton() {
        _eventFlow.value = AddEditAccountEvent.ColorButtonClick
    }

    private fun onClickSaveButton() {
        when (currentState.mode) {
            AddEditAccountState.Mode.ADD -> addAccount()
            AddEditAccountState.Mode.EDIT -> updateAccount()
        }
    }

    private fun updateAccount() {
        viewModelScope.launch {
            val resource = updateAccount(
                currentState.id!!,
                currentState.name.trim(),
                currentState.type,
                currentState.color
            )
            resource.onSuccess {
                _eventFlow.value = AddEditAccountEvent.NavigateUp
            }
        }
    }

    private fun addAccount() {
        viewModelScope.launch {
            val resource = saveAccount(currentState.toAccount())
            resource.onSuccess {
                _eventFlow.value = AddEditAccountEvent.NavigateUp
            }
        }
    }


    private fun AddEditAccountState.toAccount(): Account {
        return Account(
            id = id ?: 0,
            name = name.trim(),
            initialBalance = balance,
            balance = balance,
            currency = currency!!,
            type = type,
            dateOfCreation = OffsetDateTime.now(),
            color = color
        )
    }

    @AssistedFactory
    interface Factory {
        fun create(id: Long): AddEditAccountViewModel
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