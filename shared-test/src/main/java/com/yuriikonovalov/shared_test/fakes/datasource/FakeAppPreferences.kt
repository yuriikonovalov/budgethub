package com.yuriikonovalov.shared_test.fakes.datasource

import com.yuriikonovalov.common.data.local.AppPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

class FakeAppPreferences : AppPreferences {
    private val onboardingCompleted = MutableSharedFlow<Boolean>(1)
    private val passwordAuthenticationOn = MutableSharedFlow<Boolean>(1)
    private val biometricAuthenticationOn = MutableSharedFlow<Boolean>(1)
    private var password: String? = null

    override fun onboardingCompleted(): Flow<Boolean> {
        return onboardingCompleted
    }

    override fun biometricAuthenticationOn(): Flow<Boolean> {
        return biometricAuthenticationOn
    }

    override fun passwordAuthenticationOn(): Flow<Boolean> {
        return passwordAuthenticationOn
    }

    override suspend fun updateOnboardingCompleted(onboardingCompleted: Boolean) {
        this.onboardingCompleted.emit(onboardingCompleted)
    }

    override suspend fun setPassword(password: String): Boolean {
        this.password = password
        return true
    }

    override suspend fun getPassword(): String? {
        return password
    }

    override suspend fun updateBiometricAuthenticationOn(on: Boolean) {
        biometricAuthenticationOn.emit(on)
    }

    override suspend fun updatePasswordAuthenticationOn(on: Boolean) {
        passwordAuthenticationOn.emit(on)
    }
}