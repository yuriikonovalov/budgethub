package com.yuriikonovalov.common.application.util

import com.yuriikonovalov.common.application.entities.Transfer
import com.yuriikonovalov.common.application.entities.account.Account
import com.yuriikonovalov.common.application.entities.transaction.Transaction

fun List<Transaction>.filterByAccounts(filterList: List<Account>): List<Transaction> {
    return if (filterList.isEmpty()) {
        this
    } else {
        this.filter { it.account in filterList }
    }
}

fun List<Transfer>.filterByAccountsFrom(filterList: List<Account>): List<Transfer> {
    return if (filterList.isEmpty()) {
        this
    } else {
        this.filter { it.accountFrom in filterList }
    }
}

fun List<Transfer>.filterByAccountsTo(accountFilter: List<Account>): List<Transfer> {
    return if (accountFilter.isEmpty()) {
        this
    } else {
        this.filter { it.accountTo in accountFilter }
    }
}