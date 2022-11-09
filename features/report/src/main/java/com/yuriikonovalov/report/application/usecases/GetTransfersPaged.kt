package com.yuriikonovalov.report.application.usecases

import androidx.paging.PagingData
import com.yuriikonovalov.common.application.entities.Tag
import com.yuriikonovalov.common.application.entities.Transfer
import com.yuriikonovalov.common.application.entities.account.Account
import com.yuriikonovalov.report.application.entities.TypeFilter
import kotlinx.coroutines.flow.Flow
import java.time.OffsetDateTime

interface GetTransfersPaged {
    operator fun invoke(
        startPeriod: OffsetDateTime?,
        endPeriod: OffsetDateTime?,
        typeFilter: TypeFilter = TypeFilter.ALL,
        accountFilter: List<Account> = emptyList(),
        tagFiler: List<Tag> = emptyList()
    ): Flow<PagingData<Transfer>>
}