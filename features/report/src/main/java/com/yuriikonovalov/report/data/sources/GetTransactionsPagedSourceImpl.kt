package com.yuriikonovalov.report.data.sources

import androidx.paging.PagingData
import com.yuriikonovalov.common.application.entities.transaction.Transaction
import com.yuriikonovalov.common.data.local.TransactionsLocalDataSource
import com.yuriikonovalov.report.application.usecases.sources.GetTransactionsPagedSource
import kotlinx.coroutines.flow.Flow
import java.time.OffsetDateTime
import javax.inject.Inject

class GetTransactionsPagedSourceImpl @Inject constructor(
    private val transactionsLocalDataSource: TransactionsLocalDataSource
) : GetTransactionsPagedSource {
    override fun getTransactionsPaged(
        pageSize: Int,
        startPeriod: OffsetDateTime?,
        endPeriod: OffsetDateTime?
    ): Flow<PagingData<Transaction>> {
        return transactionsLocalDataSource.getTransactionsPaged(pageSize, startPeriod, endPeriod)
    }
}