package com.yuriikonovalov.common.application.usecases

import com.yuriikonovalov.common.application.Resource
import com.yuriikonovalov.common.application.entities.Transfer

interface UpdateTransfer {
    suspend operator fun invoke(newTransfer: Transfer): Resource<Unit>
}