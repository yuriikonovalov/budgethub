package com.yuriikonovalov.common.data.sources

import com.yuriikonovalov.common.application.usecases.datasources.IsBiometricAuthenticationOnSource
import com.yuriikonovalov.common.data.local.AppPreferences
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class IsBiometricAuthenticationOnSourceImpl @Inject constructor(
    private val appPreferences: AppPreferences
) : IsBiometricAuthenticationOnSource {
    override fun isBiometricAuthenticationOn(): Flow<Boolean> {
        return appPreferences.biometricAuthenticationOn()
    }
}