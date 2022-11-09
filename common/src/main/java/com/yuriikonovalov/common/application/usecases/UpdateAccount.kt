package com.yuriikonovalov.common.application.usecases

import com.yuriikonovalov.common.application.Resource
import com.yuriikonovalov.common.application.entities.account.AccountType

interface UpdateAccount {
    suspend operator fun invoke(
        id: Long,
        name: String,
        type: AccountType,
        color: Int
    ): Resource<Unit>
}