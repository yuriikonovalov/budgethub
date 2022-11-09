package com.yuriikonovalov.shared_test.fakes.usecase

import com.yuriikonovalov.common.application.Resource
import com.yuriikonovalov.common.application.entities.transaction.Transaction
import com.yuriikonovalov.common.application.usecases.GetTransaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeGetTransaction(
    private val transaction: Transaction,
    private val isSuccess: Boolean = true
) : GetTransaction {
    override fun invoke(transactionId: Long): Flow<Resource<Transaction>> {
        return if (isSuccess) {
            flowOf(Resource.Success(transaction))
        } else {
            flowOf(Resource.Failure())
        }
    }
}