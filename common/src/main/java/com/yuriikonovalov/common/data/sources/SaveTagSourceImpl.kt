package com.yuriikonovalov.common.data.sources

import com.yuriikonovalov.common.application.entities.Tag
import com.yuriikonovalov.common.application.usecases.datasources.SaveTagSource
import com.yuriikonovalov.common.data.local.TagsLocalDataSource
import javax.inject.Inject

class SaveTagSourceImpl @Inject constructor(
    private val tagsSourceLocal: TagsLocalDataSource
) : SaveTagSource {
    override suspend fun saveTag(tag: Tag) {
        tagsSourceLocal.saveTag(tag)
    }
}