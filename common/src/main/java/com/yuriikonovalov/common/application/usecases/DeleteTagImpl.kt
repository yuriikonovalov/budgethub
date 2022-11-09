package com.yuriikonovalov.common.application.usecases

import com.yuriikonovalov.common.application.entities.Tag
import com.yuriikonovalov.common.application.usecases.datasources.DeleteTagSource
import javax.inject.Inject

class DeleteTagImpl @Inject constructor(private val source: DeleteTagSource) : DeleteTag {
    override suspend operator fun invoke(tag: Tag) {
        source.deleteTag(tag = tag)
    }
}