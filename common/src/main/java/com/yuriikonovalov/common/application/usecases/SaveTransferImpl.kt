package com.yuriikonovalov.common.application.usecases

import com.yuriikonovalov.common.application.Resource
import com.yuriikonovalov.common.application.entities.Transfer
import com.yuriikonovalov.common.application.usecases.datasources.SaveTransferSource
import java.io.IOException
import javax.inject.Inject

class SaveTransferImpl @Inject constructor(
    private val source: SaveTransferSource,
    private val refreshAccountBalance: RefreshAccountBalance
) : SaveTransfer {
    override suspend operator fun invoke(transfer: Transfer): Resource<Unit> {
        return try {
            source.saveTransferTransaction(transfer)
            refreshAccountBalance(transfer.accountFrom.id)
            refreshAccountBalance(transfer.accountTo.id)
            Resource.unit()
        } catch (e: IOException) {
            Resource.Failure(e)
        }
    }
}