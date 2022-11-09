package com.yuriikonovalov.shared_test.fakes.usecase

import com.yuriikonovalov.common.application.Resource
import com.yuriikonovalov.common.application.usecases.DeleteTransaction

class FakeDeleteTransaction : DeleteTransaction {
    override suspend fun invoke(transactionId: Long): Resource<Unit> {
        return Resource.unit()
    }
}