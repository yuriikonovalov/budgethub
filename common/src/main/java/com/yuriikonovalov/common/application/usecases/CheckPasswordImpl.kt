package com.yuriikonovalov.common.application.usecases

import com.yuriikonovalov.common.application.Resource
import com.yuriikonovalov.common.application.usecases.datasources.CheckPasswordSource
import javax.inject.Inject

class CheckPasswordImpl @Inject constructor(
    private val source: CheckPasswordSource
) : CheckPassword {
    override suspend operator fun invoke(password: String): Resource<Boolean> {
        return try {
            val appPassword = source.getPassword()
            val result = appPassword?.let {
                password == it
            } ?: false
            Resource.Success(result)
        } catch (e: Exception) {
            Resource.Failure(e)
        }
    }
}