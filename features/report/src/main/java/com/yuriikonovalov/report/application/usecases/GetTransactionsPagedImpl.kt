package com.yuriikonovalov.report.application.usecases

import androidx.paging.PagingData
import androidx.paging.filter
import com.yuriikonovalov.common.application.entities.Tag
import com.yuriikonovalov.common.application.entities.account.Account
import com.yuriikonovalov.common.application.entities.transaction.Transaction
import com.yuriikonovalov.common.application.entities.transaction.TransactionType
import com.yuriikonovalov.report.application.entities.TypeFilter
import com.yuriikonovalov.report.application.usecases.sources.GetTransactionsPagedSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.OffsetDateTime
import javax.inject.Inject

class GetTransactionsPagedImpl @Inject constructor(
    private val source: GetTransactionsPagedSource
) : GetTransactionsPaged {
    override operator fun invoke(
        startPeriod: OffsetDateTime?,
        endPeriod: OffsetDateTime?,
        typeFilter: TypeFilter,
        accountFilter: List<Account>,
        tagFiler: List<Tag>
    ): Flow<PagingData<Transaction>> {
        val pageSize = 20
        val transactionFlow = source.getTransactionsPaged(pageSize, startPeriod, endPeriod)

        return transactionFlow.map { pagingData ->
            pagingData
                .filterByAccounts(accountFilter)
                .filterByType(typeFilter)
                .filterByTags(tagFiler)
        }
    }

    private fun PagingData<Transaction>.filterByType(type: TypeFilter): PagingData<Transaction> {
        return when (type) {
            TypeFilter.ALL -> this
            TypeFilter.EXPENSE -> this.filter { it.type == TransactionType.EXPENSE }
            TypeFilter.INCOME -> this.filter { it.type == TransactionType.INCOME }
        }
    }


    private fun PagingData<Transaction>.filterByTags(tagFilter: List<Tag>): PagingData<Transaction> {
        return if (tagFilter.isEmpty()) {
            this
        } else {
            this.filter { it.tags.containsAtLeastOne(tagFilter) }
        }
    }

    private fun List<Tag>.containsAtLeastOne(filter: List<Tag>): Boolean {
        return this.intersect(filter.toSet()).isNotEmpty()
    }

    private fun PagingData<Transaction>.filterByAccounts(accountFilter: List<Account>): PagingData<Transaction> {
        return if (accountFilter.isEmpty()) {
            this
        } else {
            this.filter { it.account in accountFilter }
        }
    }
}