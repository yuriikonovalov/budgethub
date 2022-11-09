package com.yuriikonovalov.common.data.sources

import com.yuriikonovalov.common.application.entities.Transfer
import com.yuriikonovalov.common.application.usecases.datasources.UpdateTransferSource
import com.yuriikonovalov.common.data.local.TransfersLocalDataSource
import javax.inject.Inject

class UpdateTransferSourceImpl @Inject constructor(
    private val transfersDataSourceLocal: TransfersLocalDataSource
) : UpdateTransferSource {
    override suspend fun updateTransfer(transfer: Transfer) {
        transfersDataSourceLocal.updateTransfer(transfer)
    }
}