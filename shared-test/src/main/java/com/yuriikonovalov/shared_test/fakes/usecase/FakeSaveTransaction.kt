package com.yuriikonovalov.shared_test.fakes.usecase

import android.net.Uri
import com.yuriikonovalov.common.application.Resource
import com.yuriikonovalov.common.application.entities.transaction.Transaction
import com.yuriikonovalov.common.application.usecases.SaveTransaction

class FakeSaveTransaction(
    private val isSuccess: Boolean = true
) : SaveTransaction {
    override suspend fun invoke(transaction: Transaction, imageUri: Uri?): Resource<Unit> {
        return if (isSuccess) {
            Resource.unit()
        } else {
            Resource.Failure()
        }
    }
}