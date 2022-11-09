package com.yuriikonovalov.common.data.sources

import com.yuriikonovalov.common.application.entities.Transfer
import com.yuriikonovalov.common.application.usecases.datasources.DeleteTransferSource
import com.yuriikonovalov.common.data.local.TransfersLocalDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DeleteTransferSourceImpl @Inject constructor(
    private val transfersDataSourceLocal: TransfersLocalDataSource
) : DeleteTransferSource {
    override suspend fun deleteTransfer(transferId: Long) {
        transfersDataSourceLocal.delete(transferId)
    }

    override fun getTransfer(transferId: Long): Flow<Transfer?> {
        return transfersDataSourceLocal.getTransferById(transferId)
    }
}