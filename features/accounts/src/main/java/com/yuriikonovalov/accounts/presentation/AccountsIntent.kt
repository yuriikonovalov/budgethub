package com.yuriikonovalov.accounts.presentation

sealed class AccountsIntent {
    object CreateAccount : AccountsIntent()
    object AddTransfer : AccountsIntent()
    data class DeleteAccount(val id: Long) : AccountsIntent()
    data class EditAccount(val id: Long) : AccountsIntent()
}
