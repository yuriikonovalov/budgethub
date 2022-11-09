package com.yuriikonovalov.shared_test.fakes.usecase

import com.yuriikonovalov.common.application.Resource
import com.yuriikonovalov.common.application.entities.category.Category
import com.yuriikonovalov.common.application.usecases.SaveCategory

class FakeSaveCategory(
    private val isSuccess: Boolean = true
) : SaveCategory {
    override suspend fun invoke(category: Category): Resource<Unit> {
        return if (isSuccess) {
            Resource.unit()
        } else {
            Resource.Failure()
        }
    }
}