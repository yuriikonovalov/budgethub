package com.yuriikonovalov.common.data.sources

import com.yuriikonovalov.common.application.entities.Tag
import com.yuriikonovalov.common.application.usecases.datasources.DeleteTagSource
import com.yuriikonovalov.common.data.local.TagsLocalDataSource
import javax.inject.Inject

class DeleteTagSourceImpl @Inject constructor(
    private val tagsSourceLocal: TagsLocalDataSource
) : DeleteTagSource {
    override suspend fun deleteTag(tag: Tag) {
        tagsSourceLocal.deleteTag(tag)
    }
}