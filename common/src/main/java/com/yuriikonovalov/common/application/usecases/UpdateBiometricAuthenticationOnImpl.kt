package com.yuriikonovalov.common.application.usecases

import com.yuriikonovalov.common.application.usecases.datasources.UpdateBiometricAuthenticationOnSource
import javax.inject.Inject

class UpdateBiometricAuthenticationOnImpl @Inject constructor(
    private val source: UpdateBiometricAuthenticationOnSource
) : UpdateBiometricAuthenticationOn {
    override suspend operator fun invoke(on: Boolean) {
        source.updateBiometricAuthenticationOn(on)
    }
}