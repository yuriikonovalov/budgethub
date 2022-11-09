package com.yuriikonovalov.shared_test.fakes.datasource

import com.yuriikonovalov.common.application.entities.Tag
import com.yuriikonovalov.common.data.local.TagsLocalDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeTagsLocalDataSource : TagsLocalDataSource {
    private val tags = mutableListOf<Tag>()
    private val tagFlow: MutableStateFlow<List<Tag>> = MutableStateFlow(tags)

    suspend fun setData(data: List<Tag>) {
        tags.clear()
        tags.addAll(data)
        tagFlow.emit(tags)
    }

    override suspend fun saveTag(tag: Tag): Long {
        if (tags.contains(tag)) {
            tags.remove(tag)
        }
        tags.add(tag)
        tagFlow.emit(tags)
        return tag.id
    }

    override suspend fun deleteTag(tag: Tag) {
        tags.remove(tag)
        tagFlow.emit(tags)
    }

    override suspend fun deleteAll() {
        tags.clear()
        tagFlow.emit(tags)
    }

    override suspend fun updateTag(tag: Tag) {
        val removed = tags.remove(tag)
        if (removed) {
            tags.add(tag)
            tagFlow.emit(tags)
        }
    }

    override fun getTags(): Flow<List<Tag>> {
        return tagFlow
    }
}