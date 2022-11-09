package com.yuriikonovalov.shared_test.fakes.usecase

import com.yuriikonovalov.common.application.Resource
import com.yuriikonovalov.common.application.entities.category.Category
import com.yuriikonovalov.common.application.entities.category.CategoryType
import com.yuriikonovalov.common.application.usecases.GetExpenseCategories
import com.yuriikonovalov.shared_test.model.categories
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeGetExpenseCategories(
    private val categories: List<Category> = categories(number = 5, type = CategoryType.EXPENSE)
) : GetExpenseCategories {
    override fun invoke(): Flow<Resource<List<Category>>> {
        return flowOf(Resource.successIfNotEmpty(categories))
    }
}