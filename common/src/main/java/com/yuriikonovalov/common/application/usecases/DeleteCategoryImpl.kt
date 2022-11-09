package com.yuriikonovalov.common.application.usecases

import com.yuriikonovalov.common.application.usecases.datasources.DeleteCategorySource
import javax.inject.Inject

class DeleteCategoryImpl @Inject constructor(private val source: DeleteCategorySource) :
    DeleteCategory {
    override suspend operator fun invoke(id: Long) {
        source.deleteCategory(id)
    }
}