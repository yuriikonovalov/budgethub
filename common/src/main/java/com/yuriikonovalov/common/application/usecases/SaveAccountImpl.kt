package com.yuriikonovalov.common.application.usecases

import com.yuriikonovalov.common.application.Resource
import com.yuriikonovalov.common.application.entities.account.Account
import com.yuriikonovalov.common.application.usecases.datasources.SaveAccountSource
import java.io.IOException
import javax.inject.Inject

class SaveAccountImpl @Inject constructor(private val source: SaveAccountSource) : SaveAccount {
    override suspend operator fun invoke(account: Account): Resource<Unit> {
        return try {
            source.saveAccount(account)
            Resource.unit()
        } catch (e: IOException) {
            Resource.Failure(e)
        }
    }
}