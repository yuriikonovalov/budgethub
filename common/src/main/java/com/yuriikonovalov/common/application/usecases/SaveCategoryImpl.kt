package com.yuriikonovalov.common.application.usecases

import com.yuriikonovalov.common.application.Resource
import com.yuriikonovalov.common.application.entities.category.Category
import com.yuriikonovalov.common.application.usecases.datasources.SaveCategorySource
import java.io.IOException
import javax.inject.Inject

class SaveCategoryImpl @Inject constructor(private val source: SaveCategorySource) : SaveCategory {
    override suspend operator fun invoke(category: Category): Resource<Unit> {
        return try {
            source.saveCategory(category = category)
            Resource.unit()
        } catch (e: IOException) {
            Resource.Failure(e)
        }
    }
}