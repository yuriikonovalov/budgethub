package com.yuriikonovalov.common.application.usecases

import com.yuriikonovalov.common.application.Resource
import com.yuriikonovalov.common.application.entities.account.Account
import kotlinx.coroutines.flow.Flow

interface GetAllAccounts {
    operator fun invoke(): Flow<Resource<List<Account>>>
}