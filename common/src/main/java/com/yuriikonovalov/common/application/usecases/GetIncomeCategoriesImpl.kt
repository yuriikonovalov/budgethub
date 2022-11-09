package com.yuriikonovalov.common.application.usecases

import com.yuriikonovalov.common.application.Resource
import com.yuriikonovalov.common.application.entities.category.Category
import com.yuriikonovalov.common.application.usecases.datasources.GetIncomeCategoriesSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetIncomeCategoriesImpl @Inject constructor(
    private val source: GetIncomeCategoriesSource
) : GetIncomeCategories {
    override operator fun invoke(): Flow<Resource<List<Category>>> {
        return source.getCategories().map {
            Resource.successIfNotEmpty(it)
        }
    }
}