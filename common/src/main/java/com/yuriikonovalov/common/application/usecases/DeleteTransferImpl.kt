package com.yuriikonovalov.common.application.usecases

import com.yuriikonovalov.common.application.Resource
import com.yuriikonovalov.common.application.usecases.datasources.DeleteTransferSource
import kotlinx.coroutines.flow.first
import java.lang.Exception
import javax.inject.Inject

class DeleteTransferImpl @Inject constructor(
    private val source: DeleteTransferSource,
    private val refreshAccountBalance: RefreshAccountBalance
) : DeleteTransfer {
    override suspend operator fun invoke(transferId: Long): Resource<Unit> {
        return try {
            // Affirm that the default is not null
            val transfer = source.getTransfer(transferId).first()!!
            // Delete a transaction by the given id.
            source.deleteTransfer(transferId)

            // Update the relevant accounts.
            refreshAccountBalance(transfer.accountFrom.id)
            refreshAccountBalance(transfer.accountTo.id)
            Resource.unit()
        } catch (e: Exception) {
            Resource.Failure(e)
        }
    }
}