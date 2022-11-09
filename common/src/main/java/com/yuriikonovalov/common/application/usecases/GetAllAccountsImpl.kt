package com.yuriikonovalov.common.application.usecases

import com.yuriikonovalov.common.application.Resource
import com.yuriikonovalov.common.application.entities.account.Account
import com.yuriikonovalov.common.application.usecases.datasources.GetAllAccountsSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetAllAccountsImpl @Inject constructor(
    private val source: GetAllAccountsSource
) : GetAllAccounts {
    override operator fun invoke(): Flow<Resource<List<Account>>> {
        return source.getAllAccounts().map {
            Resource.successIfNotEmpty(it)
        }
    }
}
