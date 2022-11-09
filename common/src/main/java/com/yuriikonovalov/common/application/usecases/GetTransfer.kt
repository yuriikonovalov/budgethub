package com.yuriikonovalov.common.application.usecases

import com.yuriikonovalov.common.application.Resource
import com.yuriikonovalov.common.application.entities.Transfer
import kotlinx.coroutines.flow.Flow

interface GetTransfer {
    operator fun invoke(transferId: Long): Flow<Resource<Transfer>>
}