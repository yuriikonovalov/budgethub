package com.yuriikonovalov.report.application.usecases

import com.yuriikonovalov.common.application.Resource
import com.yuriikonovalov.report.application.entities.CategoryWithNumberOfTransactions
import kotlinx.coroutines.flow.Flow
import java.time.OffsetDateTime

interface GetCategoriesWithNumberOfTransactions {
    operator fun invoke(
        startPeriod: OffsetDateTime?,
        endPeriod: OffsetDateTime?
    ): Flow<Resource<List<CategoryWithNumberOfTransactions>>>
}