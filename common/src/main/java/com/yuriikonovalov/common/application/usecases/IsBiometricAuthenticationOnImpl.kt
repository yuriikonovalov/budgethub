package com.yuriikonovalov.common.application.usecases

import com.yuriikonovalov.common.application.usecases.datasources.IsBiometricAuthenticationOnSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class IsBiometricAuthenticationOnImpl @Inject constructor(
    private val source: IsBiometricAuthenticationOnSource
) : IsBiometricAuthenticationOn {
    override operator fun invoke(): Flow<Boolean> {
        return source.isBiometricAuthenticationOn()
    }
}