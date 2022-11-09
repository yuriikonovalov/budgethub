package com.yuriikonovalov.common.application.usecases

import com.yuriikonovalov.common.application.Resource
import com.yuriikonovalov.common.application.entities.Transfer

interface SaveTransfer {
    suspend operator fun invoke(transfer: Transfer): Resource<Unit>
}