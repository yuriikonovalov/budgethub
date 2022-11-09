package com.yuriikonovalov.common.data.sources

import com.yuriikonovalov.common.application.entities.Transfer
import com.yuriikonovalov.common.application.usecases.datasources.GetTransferSource
import com.yuriikonovalov.common.data.local.TransfersLocalDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTransferSourceImpl @Inject constructor(
    private val transfersDataSourceLocal: TransfersLocalDataSource
) : GetTransferSource {
    override fun getTransferById(id: Long): Flow<Transfer?> {
        return transfersDataSourceLocal.getTransferById(id)
    }
}