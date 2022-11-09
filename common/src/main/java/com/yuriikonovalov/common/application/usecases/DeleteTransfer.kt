package com.yuriikonovalov.common.application.usecases

import com.yuriikonovalov.common.application.Resource

interface DeleteTransfer {
    suspend operator fun invoke(transferId: Long): Resource<Unit>
}