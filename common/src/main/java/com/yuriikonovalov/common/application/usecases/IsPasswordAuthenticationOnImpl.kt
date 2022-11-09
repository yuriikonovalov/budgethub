package com.yuriikonovalov.common.application.usecases

import com.yuriikonovalov.common.application.usecases.datasources.IsPasswordAuthenticationOnSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class IsPasswordAuthenticationOnImpl @Inject constructor(
    private val source: IsPasswordAuthenticationOnSource
) : IsPasswordAuthenticationOn {
    override operator fun invoke(): Flow<Boolean> {
        return source.isPasswordAuthenticationOn()
    }
}