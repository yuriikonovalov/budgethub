package com.yuriikonovalov.shared_test.fakes.datasource

import com.yuriikonovalov.common.application.entities.category.Category
import com.yuriikonovalov.common.application.entities.category.CategoryType
import com.yuriikonovalov.common.data.local.CategoriesLocalDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeCategoriesLocalDataSource : CategoriesLocalDataSource {
    private val categories = mutableListOf<Category>()
    private val categoryFlow: MutableStateFlow<List<Category>> = MutableStateFlow(categories)

    suspend fun setData(data: List<Category>) {
        categories.clear()
        categories.addAll(data)
        categoryFlow.emit(categories)
    }

    override suspend fun saveCategory(category: Category): Long {
        if (categories.contains(category)) {
            categories.remove(category)
        }
        categories.add(category)
        categoryFlow.emit(categories)
        return category.id
    }

    override suspend fun deleteCategory(id: Long) {
        categories.removeIf { it.id == id }.also { success ->
            if (success) {
                categoryFlow.emit(categories)
            }
        }
    }

    override suspend fun deleteAllCustom() {
        categories.removeIf { it.isCustom }.also { success ->
            if (success) {
                categoryFlow.emit(categories)
            }
        }
    }

    override fun getCategoriesByType(type: CategoryType): Flow<List<Category>> {
        return categoryFlow.map { list ->
            list.filter { it.type == type }
        }
    }
}