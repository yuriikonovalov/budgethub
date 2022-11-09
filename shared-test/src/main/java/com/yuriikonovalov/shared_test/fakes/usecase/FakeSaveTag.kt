package com.yuriikonovalov.shared_test.fakes.usecase

import com.yuriikonovalov.common.application.Resource
import com.yuriikonovalov.common.application.entities.Tag
import com.yuriikonovalov.common.application.usecases.SaveTag

class FakeSaveTag(
    private val isSuccess: Boolean = true
) : SaveTag {
    override suspend fun invoke(tag: Tag): Resource<Unit> {
        return if (isSuccess) {
            Resource.unit()
        } else {
            Resource.Failure()
        }
    }
}