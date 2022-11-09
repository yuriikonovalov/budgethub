package com.yuriikonovalov.common.application.usecases.datasources

import com.yuriikonovalov.common.application.entities.Transfer
import kotlinx.coroutines.flow.Flow

interface GetTransferSource {
    fun getTransferById(id: Long): Flow<Transfer?>
}