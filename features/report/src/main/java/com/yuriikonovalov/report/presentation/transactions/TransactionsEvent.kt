package com.yuriikonovalov.report.presentation.transactions

import com.yuriikonovalov.common.application.entities.PeriodFilter
import com.yuriikonovalov.common.application.entities.Tag
import com.yuriikonovalov.common.application.entities.account.Account
import com.yuriikonovalov.report.application.entities.TypeFilter

sealed class TransactionsEvent {
    data class ClickPeriodFilter(val period: PeriodFilter) : TransactionsEvent()
    data class ClickAccountFilter(val accounts: List<Account>, val accountFilter: List<Account>) :
        TransactionsEvent()

    data class ClickCustomPeriod(val startDate: Long?, val endDate: Long?) : TransactionsEvent()
    data class ClickTagFilter(val tags: List<Tag>, val tagFilter: List<Tag>) : TransactionsEvent()
    data class ClickTypeFilter(val type: TypeFilter) : TransactionsEvent()

    data class OpenTransactionDetails(val id: Long) : TransactionsEvent()
    data class OpenTransferDetails(val id: Long) : TransactionsEvent()
}
