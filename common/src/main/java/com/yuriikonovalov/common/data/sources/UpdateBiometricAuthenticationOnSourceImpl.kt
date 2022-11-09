package com.yuriikonovalov.common.data.sources

import com.yuriikonovalov.common.application.usecases.datasources.UpdateBiometricAuthenticationOnSource
import com.yuriikonovalov.common.data.local.AppPreferences
import javax.inject.Inject

class UpdateBiometricAuthenticationOnSourceImpl @Inject constructor(
    private val appPreferences: AppPreferences
) : UpdateBiometricAuthenticationOnSource {
    override suspend fun updateBiometricAuthenticationOn(on: Boolean) {
        appPreferences.updateBiometricAuthenticationOn(on)
    }
}