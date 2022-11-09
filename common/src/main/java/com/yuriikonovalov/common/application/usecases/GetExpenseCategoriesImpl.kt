package com.yuriikonovalov.common.application.usecases

import com.yuriikonovalov.common.application.Resource
import com.yuriikonovalov.common.application.entities.category.Category
import com.yuriikonovalov.common.application.usecases.datasources.GetExpenseCategoriesSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetExpenseCategoriesImpl @Inject constructor(
    private val source: GetExpenseCategoriesSource
) : GetExpenseCategories {
    override operator fun invoke(): Flow<Resource<List<Category>>> {
        return source.getCategories().map {
            Resource.successIfNotEmpty(it)
        }
    }
}