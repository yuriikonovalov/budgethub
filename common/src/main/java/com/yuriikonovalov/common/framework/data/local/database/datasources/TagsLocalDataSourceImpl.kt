package com.yuriikonovalov.common.framework.data.local.database.datasources

import com.yuriikonovalov.common.application.entities.Tag
import com.yuriikonovalov.common.data.local.TagsLocalDataSource
import com.yuriikonovalov.common.framework.data.local.database.dao.TagDao
import com.yuriikonovalov.common.framework.data.local.database.mapper.TagMapperDb
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TagsLocalDataSourceImpl @Inject constructor(
    private val tagDao: TagDao,
    private val tagMapperDb: TagMapperDb
) : TagsLocalDataSource {
    override suspend fun saveTag(tag: Tag):Long {
        val tagDb = tagMapperDb.mapFromDomain(tag)
        return tagDao.insert(tagDb)
    }

    override suspend fun deleteTag(tag: Tag) {
        val tagDb = tagMapperDb.mapFromDomain(tag)
        tagDao.delete(tagDb)
    }

    override suspend fun deleteAll() {
        tagDao.deleteAll()
    }

    override suspend fun updateTag(tag: Tag) {
        val tagDb = tagMapperDb.mapFromDomain(tag)
        tagDao.update(tagDb)
    }

    override fun getTags(): Flow<List<Tag>> {
        return tagDao.getAll().map { list ->
            list.map {
                tagMapperDb.mapToDomain(it)
            }
        }
    }
}