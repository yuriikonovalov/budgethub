package com.yuriikonovalov.accounts.presentation

sealed class AccountsEvent {
    object CreateAccount : AccountsEvent()
    object AddTransfer : AccountsEvent()
    data class EditAccount(val id: Long) : AccountsEvent()
}
