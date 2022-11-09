package com.yuriikonovalov.common.application.usecases

import com.yuriikonovalov.common.application.Resource
import com.yuriikonovalov.common.application.entities.Tag
import com.yuriikonovalov.common.application.usecases.datasources.SaveTagSource
import javax.inject.Inject

class SaveTagImpl @Inject constructor(private val source: SaveTagSource) : SaveTag {
    override suspend operator fun invoke(tag: Tag): Resource<Unit> {
        return try {
            source.saveTag(tag)
            Resource.unit()
        } catch (e: Exception) {
            Resource.Failure(e)
        }
    }
}