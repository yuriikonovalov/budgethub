package com.yuriikonovalov.common.application.usecases.datasources

import com.yuriikonovalov.common.application.entities.account.Account
import kotlinx.coroutines.flow.Flow

interface GetAllAccountsSource {
    fun getAllAccounts(): Flow<List<Account>>
}