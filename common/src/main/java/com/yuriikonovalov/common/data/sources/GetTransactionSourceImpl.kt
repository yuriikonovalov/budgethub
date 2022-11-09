package com.yuriikonovalov.common.data.sources

import com.yuriikonovalov.common.application.entities.transaction.Transaction
import com.yuriikonovalov.common.application.usecases.datasources.GetTransactionSource
import com.yuriikonovalov.common.data.local.TransactionsLocalDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTransactionSourceImpl @Inject constructor(
    private val transactionsSourceLocal: TransactionsLocalDataSource
) : GetTransactionSource {
    override fun getTransactionById(id: Long): Flow<Transaction?> {
        return transactionsSourceLocal.getTransactionById(id)
    }
}