package com.yuriikonovalov.common.data.sources

import com.yuriikonovalov.common.application.usecases.datasources.DeleteAccountSource
import com.yuriikonovalov.common.data.local.AccountsLocalDataSource
import javax.inject.Inject

class DeleteAccountSourceImpl @Inject constructor(
    private val accountsLocalDataSource: AccountsLocalDataSource
) : DeleteAccountSource {
    override suspend fun deleteAccount(id: Long) {
        accountsLocalDataSource.deleteAccount(id)
    }
}