package com.yuriikonovalov.common.data.sources

import com.yuriikonovalov.common.application.entities.account.Account
import com.yuriikonovalov.common.application.usecases.datasources.GetAllAccountsSource
import com.yuriikonovalov.common.data.local.AccountsLocalDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllAccountsSourceImpl @Inject constructor(private val accountsSourceLocal: AccountsLocalDataSource) :
    GetAllAccountsSource {
    override fun getAllAccounts(): Flow<List<Account>> {
        return accountsSourceLocal.getAllAccounts()
    }
}