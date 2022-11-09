package com.yuriikonovalov.report.presentation.model

import com.yuriikonovalov.common.application.entities.account.Account

data class AccountItem(
    val account: Account,
    val checked: Boolean = false
)
