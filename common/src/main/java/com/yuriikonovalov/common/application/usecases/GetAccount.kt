package com.yuriikonovalov.common.application.usecases

import com.yuriikonovalov.common.application.Resource
import com.yuriikonovalov.common.application.entities.account.Account

interface GetAccount {
    suspend operator fun invoke(id: Long): Resource<Account>
}