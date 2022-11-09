package com.yuriikonovalov.common.data.sources

import com.yuriikonovalov.common.application.entities.account.Account
import com.yuriikonovalov.common.application.usecases.datasources.SaveAccountSource
import com.yuriikonovalov.common.data.local.AccountsLocalDataSource
import javax.inject.Inject

class SaveAccountSourceImpl @Inject constructor(
    private val accountsSourceLocal: AccountsLocalDataSource
) : SaveAccountSource {
    override suspend fun saveAccount(account: Account): Long {
        return accountsSourceLocal.saveAccount(account)
    }
}