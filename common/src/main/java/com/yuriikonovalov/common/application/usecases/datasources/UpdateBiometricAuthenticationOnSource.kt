package com.yuriikonovalov.common.application.usecases.datasources

interface UpdateBiometricAuthenticationOnSource {
    suspend fun updateBiometricAuthenticationOn(on: Boolean)
}