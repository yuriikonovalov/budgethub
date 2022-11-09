package com.yuriikonovalov.common.application.usecases

import androidx.core.net.toUri
import com.yuriikonovalov.common.application.Resource
import com.yuriikonovalov.common.application.usecases.datasources.DeleteTransactionSource
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class DeleteTransactionImpl @Inject constructor(
    private val source: DeleteTransactionSource,
    private val refreshAccountBalance: RefreshAccountBalance
) : DeleteTransaction {
    override suspend operator fun invoke(transactionId: Long): Resource<Unit> {
        return try {
            val transaction = source.getTransaction(transactionId).first()!!
            // Delete a transaction by the given id.
            source.deleteTransaction(transactionId)
            // Delete an image if there's one.
            transaction.imagePath?.let { path ->
                source.deleteTransactionImage(path.toUri())
            }
            // Update the relevant account.
            refreshAccountBalance(transaction.account.id)
            Resource.unit()
        } catch (e: Exception) {
            Resource.Failure(e)
        }
    }
}