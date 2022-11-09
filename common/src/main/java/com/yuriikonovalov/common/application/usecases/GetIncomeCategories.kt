package com.yuriikonovalov.common.application.usecases

import com.yuriikonovalov.common.application.Resource
import com.yuriikonovalov.common.application.entities.category.Category
import kotlinx.coroutines.flow.Flow

interface GetIncomeCategories {
    operator fun invoke(): Flow<Resource<List<Category>>>
}