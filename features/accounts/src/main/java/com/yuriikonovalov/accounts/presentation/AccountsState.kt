package com.yuriikonovalov.accounts.presentation

import com.yuriikonovalov.common.application.entities.account.Account

data class AccountsState(
    val accounts: List<Account> = emptyList()
) {
    // At least two accounts required for transferring.
    val createButtonVisible: Boolean get() = accounts.isNotEmpty()
    val transferButtonVisible: Boolean get() = accounts.size > 1
    val placeholderVisible: Boolean get() = accounts.isEmpty()

    fun updateAccount(accounts: List<Account>): AccountsState {
        return copy(accounts = accounts)
    }
}
