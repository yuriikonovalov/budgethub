package com.yuriikonovalov.common.application.usecases.datasources

import com.yuriikonovalov.common.application.entities.Transfer
import com.yuriikonovalov.common.application.entities.account.Account

interface SaveTransferSource {
    suspend fun saveTransferTransaction(transaction: Transfer): Long
    suspend fun updateAccount(account: Account)
}