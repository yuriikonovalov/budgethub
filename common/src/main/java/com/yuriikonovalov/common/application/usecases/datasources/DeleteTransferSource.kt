package com.yuriikonovalov.common.application.usecases.datasources

import com.yuriikonovalov.common.application.entities.Transfer
import kotlinx.coroutines.flow.Flow

interface DeleteTransferSource {
    suspend fun deleteTransfer(transferId: Long)
    fun getTransfer(transferId: Long): Flow<Transfer?>
}