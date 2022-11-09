package com.yuriikonovalov.shared_test.fakes.usecase

import android.net.Uri
import com.yuriikonovalov.common.application.Resource
import com.yuriikonovalov.common.application.entities.transaction.Transaction
import com.yuriikonovalov.common.application.usecases.UpdateTransaction

class FakeUpdateTransaction(
    private val isSuccess: Boolean = true
) : UpdateTransaction {
    override suspend fun invoke(newTransaction: Transaction, newImageUri: Uri?): Resource<Unit> {
        return if (isSuccess) {
            Resource.unit()
        } else {
            Resource.Failure()
        }
    }
}