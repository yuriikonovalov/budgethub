package com.yuriikonovalov.common.data.sources

import com.yuriikonovalov.common.application.entities.Tag
import com.yuriikonovalov.common.application.usecases.datasources.GetTagsSource
import com.yuriikonovalov.common.data.local.TagsLocalDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTagsSourceImpl @Inject constructor(
    private val tagsSourceLocal: TagsLocalDataSource
) : GetTagsSource {
    override  fun getTags(): Flow<List<Tag>> {
        return tagsSourceLocal.getTags()
    }
}