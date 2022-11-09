package com.yuriikonovalov.common.application.usecases

import com.yuriikonovalov.common.application.Resource
import com.yuriikonovalov.common.application.entities.transaction.Transaction
import com.yuriikonovalov.common.application.usecases.datasources.GetTransactionSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetTransactionImpl @Inject constructor(
    private val source: GetTransactionSource
) : GetTransaction {
    override operator fun invoke(transactionId: Long): Flow<Resource<Transaction>> {
        return source.getTransactionById(transactionId)
            .map { Resource.successIfNotNull(it) }
    }
}