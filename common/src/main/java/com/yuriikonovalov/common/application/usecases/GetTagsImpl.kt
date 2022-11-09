package com.yuriikonovalov.common.application.usecases

import com.yuriikonovalov.common.application.Resource
import com.yuriikonovalov.common.application.entities.Tag
import com.yuriikonovalov.common.application.usecases.datasources.GetTagsSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetTagsImpl @Inject constructor(private val source: GetTagsSource) : GetTags {
    override operator fun invoke(): Flow<Resource<List<Tag>>> {
        return source.getTags().map { Resource.successIfNotEmpty(it) }
    }
}