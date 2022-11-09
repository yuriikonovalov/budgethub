package com.yuriikonovalov.common.application.usecases

interface UpdateBiometricAuthenticationOn {
    suspend operator fun invoke(on: Boolean)
}