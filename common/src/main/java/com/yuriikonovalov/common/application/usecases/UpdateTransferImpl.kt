package com.yuriikonovalov.common.application.usecases

import com.yuriikonovalov.common.application.Resource
import com.yuriikonovalov.common.application.entities.Transfer
import com.yuriikonovalov.common.application.usecases.datasources.UpdateTransferSource
import java.io.IOException
import javax.inject.Inject

class UpdateTransferImpl @Inject constructor(
    private val source: UpdateTransferSource,
    private val refreshAccountBalance: RefreshAccountBalance
) : UpdateTransfer {
    override suspend operator fun invoke(newTransfer: Transfer): Resource<Unit> {
        return try {
            source.updateTransfer(newTransfer)
            // update the balance column in the 'accounts' table.
            refreshAccountBalance(newTransfer.accountFrom.id)
            refreshAccountBalance(newTransfer.accountTo.id)
            Resource.unit()
        } catch (e: IOException) {
            Resource.Failure(e)
        }
    }
}
