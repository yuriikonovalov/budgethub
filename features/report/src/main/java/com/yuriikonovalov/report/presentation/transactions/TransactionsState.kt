package com.yuriikonovalov.report.presentation.transactions

import com.yuriikonovalov.common.application.entities.PeriodFilter
import com.yuriikonovalov.common.application.entities.Tag
import com.yuriikonovalov.common.application.entities.account.Account
import com.yuriikonovalov.report.application.entities.TypeFilter
import java.time.OffsetDateTime

data class TransactionsState(
    val periodFilter: PeriodFilter = PeriodFilter.Week,
    val customPeriod: Pair<OffsetDateTime, OffsetDateTime>? = null,
    val accounts: List<Account> = emptyList(),
    val accountFilter: List<Account> = emptyList(),
    val tags: List<Tag> = emptyList(),
    val tagFilter: List<Tag> = emptyList(),
    val typeFilter: TypeFilter = TypeFilter.ALL
) {

    fun updatePeriod(periodFilter: PeriodFilter): TransactionsState {
        return if (periodFilter is PeriodFilter.Custom) {
            copy(
                periodFilter = periodFilter,
                customPeriod = Pair(periodFilter.startPeriod, periodFilter.endPeriod)
            )
        } else {
            copy(periodFilter = periodFilter, customPeriod = null)
        }
    }

    fun updateAccounts(accounts: List<Account>): TransactionsState {
        return copy(accounts = accounts)
    }

    fun updateAccountFilter(accounts: List<Account>): TransactionsState {
        return copy(accountFilter = accounts)
    }

    fun updateTags(tags: List<Tag>): TransactionsState {
        return copy(tags = tags)
    }

    fun updateTagFilter(tags: List<Tag>): TransactionsState {
        return copy(tagFilter = tags)
    }

    fun updateTypeFilter(typeFilter: TypeFilter): TransactionsState {
        return copy(typeFilter = typeFilter)
    }
}
