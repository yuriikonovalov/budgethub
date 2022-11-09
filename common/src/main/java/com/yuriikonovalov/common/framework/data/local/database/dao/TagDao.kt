package com.yuriikonovalov.common.framework.data.local.database.dao

import androidx.room.*
import com.yuriikonovalov.common.framework.data.local.database.model.TagDb
import kotlinx.coroutines.flow.Flow

@Dao
interface TagDao {
    @Insert
    suspend fun insert(tag: TagDb): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(tag: TagDb)

    @Delete
    suspend fun delete(tag: TagDb)

    @Query("DELETE FROM tags")
    suspend fun deleteAll()

    @Query("SELECT * FROM tags")
    fun getAll(): Flow<List<TagDb>>
}