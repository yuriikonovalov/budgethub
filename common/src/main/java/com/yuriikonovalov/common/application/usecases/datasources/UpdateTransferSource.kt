package com.yuriikonovalov.common.application.usecases.datasources

import com.yuriikonovalov.common.application.entities.Transfer

interface UpdateTransferSource {
    suspend fun updateTransfer(transfer: Transfer)
}