package com.yuriikonovalov.common.application.usecases

import com.yuriikonovalov.common.application.Resource

interface RefreshAccountBalance {
    suspend operator fun invoke(accountId: Long): Resource<Unit>
}