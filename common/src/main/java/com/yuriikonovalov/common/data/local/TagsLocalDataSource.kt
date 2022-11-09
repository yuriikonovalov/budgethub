package com.yuriikonovalov.common.data.local

import com.yuriikonovalov.common.application.entities.Tag
import kotlinx.coroutines.flow.Flow


interface TagsLocalDataSource {
    suspend fun saveTag(tag: Tag): Long
    suspend fun deleteTag(tag: Tag)
    suspend fun deleteAll()
    suspend fun updateTag(tag: Tag)
    fun getTags(): Flow<List<Tag>>
}