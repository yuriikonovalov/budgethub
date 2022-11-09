package com.yuriikonovalov.common.application.usecases

import com.yuriikonovalov.common.application.Resource
import com.yuriikonovalov.common.application.entities.TransactionAmount
import com.yuriikonovalov.common.application.entities.account.Account
import kotlinx.coroutines.flow.Flow
import java.time.OffsetDateTime

interface GetIncomeForPeriod {
    operator fun invoke(
        startPeriod: OffsetDateTime?,
        endPeriod: OffsetDateTime?,
        accountFilter: List<Account> = emptyList()
    ): Flow<Resource<List<TransactionAmount>>>
}