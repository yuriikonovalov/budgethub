package com.yuriikonovalov.common.application.usecases.datasources

interface DeleteCategorySource {
    suspend fun deleteCategory(id: Long)
}