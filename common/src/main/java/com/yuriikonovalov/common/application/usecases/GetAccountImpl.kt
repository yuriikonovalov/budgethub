package com.yuriikonovalov.common.application.usecases

import com.yuriikonovalov.common.application.Resource
import com.yuriikonovalov.common.application.entities.account.Account
import com.yuriikonovalov.common.application.usecases.datasources.GetAccountSource
import java.io.IOException
import javax.inject.Inject

class GetAccountImpl @Inject constructor(private val source: GetAccountSource) : GetAccount {
    override suspend operator fun invoke(id: Long): Resource<Account> {
        return try {
            val account = source.getAccount(id)
            Resource.successIfNotNull(account)
        } catch (e: IOException) {
            Resource.Failure(e)
        }
    }
}