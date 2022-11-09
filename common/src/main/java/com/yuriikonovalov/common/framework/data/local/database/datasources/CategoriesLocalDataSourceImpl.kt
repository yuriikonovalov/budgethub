package com.yuriikonovalov.common.framework.data.local.database.datasources

import com.yuriikonovalov.common.application.entities.category.Category
import com.yuriikonovalov.common.application.entities.category.CategoryType
import com.yuriikonovalov.common.data.local.CategoriesLocalDataSource
import com.yuriikonovalov.common.framework.data.local.database.dao.CategoryDao
import com.yuriikonovalov.common.framework.data.local.database.mapper.CategoryMapperDb
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CategoriesLocalDataSourceImpl @Inject constructor(
    private val categoryDao: CategoryDao,
    private val categoryMapperDb: CategoryMapperDb
) : CategoriesLocalDataSource {

    override suspend fun saveCategory(category: Category): Long {
        val categoryDb = categoryMapperDb.mapFromDomain(category)
        return categoryDao.insert(categoryDb)
    }

    override suspend fun deleteCategory(id: Long) {
        categoryDao.deleteById(id)
    }

    override suspend fun deleteAllCustom() {
        categoryDao.deleteAllCustom()
    }

    override fun getCategoriesByType(type: CategoryType): Flow<List<Category>> {
        return categoryDao.getCategoriesByType(type).map { list ->
            list.map { categoryAggregateDb ->
                categoryMapperDb.mapToDomain(categoryAggregateDb)
            }
        }
    }
}