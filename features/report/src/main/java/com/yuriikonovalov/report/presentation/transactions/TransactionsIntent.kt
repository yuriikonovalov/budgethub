package com.yuriikonovalov.report.presentation.transactions

import com.yuriikonovalov.common.application.entities.PeriodFilter
import com.yuriikonovalov.common.application.entities.Tag
import com.yuriikonovalov.common.application.entities.account.Account
import com.yuriikonovalov.report.application.entities.TypeFilter

sealed class TransactionsIntent {
    data class ChangePeriodFilter(val periodFilter: PeriodFilter) : TransactionsIntent()
    data class ChangeAccountFilter(val accounts: List<Account>) : TransactionsIntent()
    data class ChangeTagFilter(val tags: List<Tag>) : TransactionsIntent()
    data class ChangeTypeFilter(val type: TypeFilter) : TransactionsIntent()

    data class OpenTransactionDetails(val id: Long) : TransactionsIntent()
    data class OpenTransferDetails(val id: Long) : TransactionsIntent()

    object ClickCustomPeriod : TransactionsIntent()
    object ClickPeriodFilter : TransactionsIntent()
    object ClickAccountFilter : TransactionsIntent()
    object ClickTagFilter : TransactionsIntent()
    object ClickTypeFilter : TransactionsIntent()
}
