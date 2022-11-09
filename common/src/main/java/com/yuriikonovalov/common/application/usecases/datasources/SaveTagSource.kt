package com.yuriikonovalov.common.application.usecases.datasources

import com.yuriikonovalov.common.application.entities.Tag

interface SaveTagSource {
    suspend fun saveTag(tag: Tag)
}