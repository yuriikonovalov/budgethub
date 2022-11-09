package com.yuriikonovalov.report.application.usecases.sources

import androidx.paging.PagingData
import com.yuriikonovalov.common.application.entities.transaction.Transaction
import kotlinx.coroutines.flow.Flow
import java.time.OffsetDateTime

interface GetTransactionsPagedSource {
    fun getTransactionsPaged(
        pageSize: Int,
        startPeriod: OffsetDateTime?,
        endPeriod: OffsetDateTime?
    ): Flow<PagingData<Transaction>>
}