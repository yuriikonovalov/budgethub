package com.yuriikonovalov.common.framework.data.local.database.dao

import androidx.room.*
import com.yuriikonovalov.common.application.entities.category.CategoryType

import com.yuriikonovalov.common.framework.data.local.database.model.CategoryDb
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Insert
    suspend fun insert(category: CategoryDb): Long

    @Query("SELECT * FROM categories WHERE type = :type")
    fun getCategoriesByType(type: CategoryType): Flow<List<CategoryDb>>

    @Query("DELETE FROM categories WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM categories WHERE is_custom = 1")
    suspend fun deleteAllCustom()
}