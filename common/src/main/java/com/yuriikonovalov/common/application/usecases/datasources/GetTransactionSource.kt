package com.yuriikonovalov.common.application.usecases.datasources

import com.yuriikonovalov.common.application.entities.transaction.Transaction
import kotlinx.coroutines.flow.Flow

interface GetTransactionSource {
    fun getTransactionById(id: Long): Flow<Transaction?>
}