package com.yuriikonovalov.common.application.usecases

import com.yuriikonovalov.common.application.Resource

interface DeleteTransaction {
    suspend operator fun invoke(transactionId: Long): Resource<Unit>
}