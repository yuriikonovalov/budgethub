package com.yuriikonovalov.common.application.usecases.datasources

import com.yuriikonovalov.common.application.entities.Tag

interface DeleteTagSource {
    suspend fun deleteTag(tag: Tag)
}