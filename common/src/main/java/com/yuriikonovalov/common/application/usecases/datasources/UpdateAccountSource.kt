package com.yuriikonovalov.common.application.usecases.datasources

import com.yuriikonovalov.common.application.entities.account.Account

interface UpdateAccountSource {
    suspend fun updateAccount(account: Account)
    suspend fun getAccount(accountId: Long): Account?
}