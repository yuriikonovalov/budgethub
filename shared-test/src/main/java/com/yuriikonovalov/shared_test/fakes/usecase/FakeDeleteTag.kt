package com.yuriikonovalov.shared_test.fakes.usecase

import com.yuriikonovalov.common.application.entities.Tag
import com.yuriikonovalov.common.application.usecases.DeleteTag

class FakeDeleteTag : DeleteTag {
    override suspend fun invoke(tag: Tag) {
        /* no-op */
    }
}