package com.yuriikonovalov.common.application.usecases

import com.yuriikonovalov.common.application.Resource
import com.yuriikonovalov.common.application.entities.account.AccountType
import com.yuriikonovalov.common.application.usecases.datasources.UpdateAccountSource
import java.io.IOException
import javax.inject.Inject

class UpdateAccountImpl @Inject constructor(
    private val source: UpdateAccountSource
) : UpdateAccount {
    override suspend operator fun invoke(
        id: Long,
        name: String,
        type: AccountType,
        color: Int
    ): Resource<Unit> {
        return try {
            val account = source.getAccount(id)!!
            val newAccount = account.copy(name = name, type = type, color = color)
            source.updateAccount(newAccount)
            Resource.unit()
        } catch (e: IOException) {
            Resource.Failure(e)
        }
    }
}