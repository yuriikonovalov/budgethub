package com.yuriikonovalov.common.application.usecases

import com.yuriikonovalov.common.application.Resource
import com.yuriikonovalov.common.application.entities.Tag
import kotlinx.coroutines.flow.Flow

interface GetTags {
    operator fun invoke(): Flow<Resource<List<Tag>>>
}