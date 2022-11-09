package com.yuriikonovalov.common.application.usecases

import com.yuriikonovalov.common.application.Resource
import com.yuriikonovalov.common.application.entities.account.Account

interface SaveAccount {
    suspend operator fun invoke(account: Account): Resource<Unit>
}