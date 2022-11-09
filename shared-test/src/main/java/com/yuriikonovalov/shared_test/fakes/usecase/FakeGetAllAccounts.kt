package com.yuriikonovalov.shared_test.fakes.usecase

import com.yuriikonovalov.common.application.Resource
import com.yuriikonovalov.common.application.entities.account.Account
import com.yuriikonovalov.common.application.usecases.GetAllAccounts
import com.yuriikonovalov.shared_test.model.accounts
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeGetAllAccounts(
    private val accounts: List<Account> = accounts(2)
) : GetAllAccounts {
    override fun invoke(): Flow<Resource<List<Account>>> {
        return flowOf(Resource.successIfNotEmpty(accounts))
    }
}