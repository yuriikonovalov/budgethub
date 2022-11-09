package com.yuriikonovalov.accounts.framework.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yuriikonovalov.accounts.presentation.AccountsEvent
import com.yuriikonovalov.accounts.presentation.AccountsIntent
import com.yuriikonovalov.accounts.presentation.AccountsState
import com.yuriikonovalov.common.application.usecases.DeleteAccount
import com.yuriikonovalov.common.application.usecases.GetAllAccounts
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountsViewModel @Inject constructor(
    private val getAllAccounts: GetAllAccounts,
    private val deleteAccount: DeleteAccount
) : ViewModel() {
    private val _stateFlow = MutableStateFlow(AccountsState())
    private val _eventFlow = MutableStateFlow<AccountsEvent?>(null)
    val stateFlow get() = _stateFlow.asStateFlow()
    val eventFlow get() = _eventFlow.asStateFlow()
    val eventConsumer: () -> Unit = { _eventFlow.value = null }

    init {
        loadAccounts()
    }

    fun handleIntent(intent: AccountsIntent) {
        when (intent) {
            is AccountsIntent.CreateAccount -> onCreateAccount()
            is AccountsIntent.AddTransfer -> onAddTransfer()
            is AccountsIntent.DeleteAccount -> onDeleteAccount(intent.id)
            is AccountsIntent.EditAccount -> onEditAccount(intent.id)
        }
    }

    private fun onCreateAccount() {
        _eventFlow.value = AccountsEvent.CreateAccount
    }

    private fun onAddTransfer() {
        _eventFlow.value = AccountsEvent.AddTransfer
    }

    private fun onEditAccount(id: Long) {
        _eventFlow.value = AccountsEvent.EditAccount(id)
    }

    private fun onDeleteAccount(id: Long) {
        viewModelScope.launch {
            deleteAccount(id)
        }
    }

    private fun loadAccounts() {
        viewModelScope.launch {
            getAllAccounts().collect { resource ->
                resource.onSuccess { accounts ->
                    _stateFlow.update { it.updateAccount(accounts) }
                }
                resource.onFailure {
                    _stateFlow.update { it.updateAccount(emptyList()) }
                }
            }
        }
    }
}