package com.yuriikonovalov.common.application.usecases.datasources

import com.yuriikonovalov.common.application.entities.account.Account

interface GetAccountSource {
    suspend fun getAccount(id: Long): Account?
}