package com.yuriikonovalov.report.application.usecases

import com.yuriikonovalov.common.application.Resource
import com.yuriikonovalov.common.application.entities.account.Account
import com.yuriikonovalov.report.application.entities.AggregateCategoryFlatByCurrency
import kotlinx.coroutines.flow.Flow
import java.time.OffsetDateTime

interface GetAggregateIncomeCategoriesFlatByCurrency {
    operator fun invoke(
        startPeriod: OffsetDateTime?,
        endPeriod: OffsetDateTime?,
        accountFilter: List<Account> = emptyList(),
    ): Flow<Resource<List<AggregateCategoryFlatByCurrency>>>
}