package com.yuriikonovalov.common.application.usecases

import com.yuriikonovalov.common.application.Resource
import com.yuriikonovalov.common.application.usecases.datasources.SetPasswordSource
import javax.inject.Inject

class SetPasswordImpl @Inject constructor(private val source: SetPasswordSource) : SetPassword {
    override suspend operator fun invoke(password: String): Resource<Boolean> {
        val saved = source.savePassword(password)
        return Resource.Success(saved)
    }
}