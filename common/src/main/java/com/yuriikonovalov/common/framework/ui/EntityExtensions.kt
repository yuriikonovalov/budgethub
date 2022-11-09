package com.yuriikonovalov.common.framework.ui

import com.yuriikonovalov.common.R
import com.yuriikonovalov.common.application.entities.account.AccountType

fun AccountType.toStringResource(): Int {
    return when (this) {
        AccountType.CASH -> R.string.cash
        AccountType.CARD -> R.string.card
        AccountType.DEPOSIT -> R.string.deposit
    }
}