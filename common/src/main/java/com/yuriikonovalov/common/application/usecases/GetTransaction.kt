package com.yuriikonovalov.common.application.usecases

import com.yuriikonovalov.common.application.Resource
import com.yuriikonovalov.common.application.entities.transaction.Transaction
import kotlinx.coroutines.flow.Flow

interface GetTransaction {
    operator fun invoke(transactionId: Long): Flow<Resource<Transaction>>
}