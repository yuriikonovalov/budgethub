package com.yuriikonovalov.shared_test.fakes.usecase.source

import com.yuriikonovalov.common.application.entities.account.Account
import com.yuriikonovalov.common.application.usecases.datasources.GetAccountSource
import java.io.IOException

class FakeGetAccountSource(
    private val account: Account,
    private val exception: Exception? = null
) : GetAccountSource {
    override suspend fun getAccount(id: Long): Account? {
        exception?.let { throw IOException() }
        return if (account.id == id) {
            account
        } else {
            null
        }
    }

}