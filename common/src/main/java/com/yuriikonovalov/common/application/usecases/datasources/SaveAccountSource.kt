package com.yuriikonovalov.common.application.usecases.datasources

import com.yuriikonovalov.common.application.entities.account.Account

interface SaveAccountSource {
    suspend fun saveAccount(account: Account): Long
}