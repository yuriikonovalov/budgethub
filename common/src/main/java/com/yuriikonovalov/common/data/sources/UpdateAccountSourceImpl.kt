package com.yuriikonovalov.common.data.sources

import com.yuriikonovalov.common.application.entities.account.Account
import com.yuriikonovalov.common.application.usecases.datasources.UpdateAccountSource
import com.yuriikonovalov.common.data.local.AccountsLocalDataSource
import javax.inject.Inject

class UpdateAccountSourceImpl @Inject constructor(
    private val accountsSourceLocal: AccountsLocalDataSource
) : UpdateAccountSource {
    override suspend fun updateAccount(account: Account) {
        accountsSourceLocal.updateAccount(account)
    }

    override suspend fun getAccount(accountId: Long): Account? {
        return accountsSourceLocal.getAccountById(accountId)
    }
}