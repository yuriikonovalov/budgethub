package com.yuriikonovalov.common.application.usecases.datasources

import com.yuriikonovalov.common.application.entities.Tag
import kotlinx.coroutines.flow.Flow

interface GetTagsSource {
     fun getTags(): Flow<List<Tag>>
}