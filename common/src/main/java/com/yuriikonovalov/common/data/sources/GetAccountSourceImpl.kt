package com.yuriikonovalov.common.data.sources

import com.yuriikonovalov.common.application.entities.account.Account
import com.yuriikonovalov.common.application.usecases.datasources.GetAccountSource
import com.yuriikonovalov.common.data.local.AccountsLocalDataSource
import javax.inject.Inject

class GetAccountSourceImpl @Inject constructor(
    private val accountsSourceLocal: AccountsLocalDataSource
) : GetAccountSource {
    override suspend fun getAccount(id: Long): Account? {
        return accountsSourceLocal.getAccountById(id)
    }
}