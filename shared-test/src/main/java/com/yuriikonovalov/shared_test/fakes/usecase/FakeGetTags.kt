package com.yuriikonovalov.shared_test.fakes.usecase

import com.yuriikonovalov.common.application.Resource
import com.yuriikonovalov.common.application.entities.Tag
import com.yuriikonovalov.common.application.usecases.GetTags
import com.yuriikonovalov.shared_test.model.tags
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeGetTags(
    private val tags: List<Tag> = tags(5),
    private val isSuccess: Boolean = true
) : GetTags {
    override fun invoke(): Flow<Resource<List<Tag>>> {
        return if (isSuccess) {
            flowOf(Resource.Success(tags))
        } else {
            flowOf(Resource.Failure())
        }
    }
}