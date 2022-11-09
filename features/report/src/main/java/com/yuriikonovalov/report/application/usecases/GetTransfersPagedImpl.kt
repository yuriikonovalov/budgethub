package com.yuriikonovalov.report.application.usecases

import androidx.paging.PagingData
import androidx.paging.filter
import com.yuriikonovalov.common.application.entities.Tag
import com.yuriikonovalov.common.application.entities.Transfer
import com.yuriikonovalov.common.application.entities.account.Account
import com.yuriikonovalov.report.application.entities.TypeFilter
import com.yuriikonovalov.report.application.usecases.sources.GetTransfersPagedSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.OffsetDateTime
import javax.inject.Inject

class GetTransfersPagedImpl @Inject constructor(
    private val source: GetTransfersPagedSource
) : GetTransfersPaged {
    override operator fun invoke(
        startPeriod: OffsetDateTime?,
        endPeriod: OffsetDateTime?,
        typeFilter: TypeFilter,
        accountFilter: List<Account>,
        tagFiler: List<Tag>
    ): Flow<PagingData<Transfer>> {
        val pageSize = 20
        val transferFlow = source.getTransfersPaged(pageSize, startPeriod, endPeriod)

        return transferFlow.map { pagingData ->
            pagingData
                .filterByAccountsAndType(accountFilter, typeFilter)
                .filterByTags(tagFiler)
        }
    }

    private fun PagingData<Transfer>.filterByTags(tagFilter: List<Tag>): PagingData<Transfer> {
        return if (tagFilter.isEmpty()) {
            this
        } else {
            this.filter { it.tags.containsAtLeastOne(tagFilter) }
        }
    }

    private fun List<Tag>.containsAtLeastOne(filter: List<Tag>): Boolean {
        return this.intersect(filter.toSet()).isNotEmpty()
    }

    private fun PagingData<Transfer>.filterByAccountsAndType(
        accountFilter: List<Account>,
        type: TypeFilter
    ): PagingData<Transfer> {
        return if (accountFilter.isEmpty()) {
            this
        } else {
            when (type) {
                TypeFilter.ALL -> this.filter { (it.accountFrom in accountFilter) || (it.accountTo in accountFilter) }
                TypeFilter.EXPENSE -> this.filter { (it.accountFrom in accountFilter) }
                TypeFilter.INCOME -> this.filter { (it.accountTo in accountFilter) }
            }
        }
    }
}