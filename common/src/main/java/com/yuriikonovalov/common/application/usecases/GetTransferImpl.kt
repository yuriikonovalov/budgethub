package com.yuriikonovalov.common.application.usecases

import com.yuriikonovalov.common.application.Resource
import com.yuriikonovalov.common.application.entities.Transfer
import com.yuriikonovalov.common.application.usecases.datasources.GetTransferSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetTransferImpl @Inject constructor(private val source: GetTransferSource) : GetTransfer {
    override operator fun invoke(transferId: Long): Flow<Resource<Transfer>> {
        return source.getTransferById(transferId).map {
            Resource.successIfNotNull(it)
        }
    }
}