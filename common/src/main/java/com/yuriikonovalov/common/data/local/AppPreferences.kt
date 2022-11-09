package com.yuriikonovalov.common.data.local

import kotlinx.coroutines.flow.Flow

interface AppPreferences {
    fun onboardingCompleted(): Flow<Boolean>
    fun biometricAuthenticationOn(): Flow<Boolean>
    fun passwordAuthenticationOn(): Flow<Boolean>
    suspend fun updateOnboardingCompleted(onboardingCompleted: Boolean)
    suspend fun setPassword(password: String): Boolean
    suspend fun getPassword(): String?
    suspend fun updateBiometricAuthenticationOn(on: Boolean)
    suspend fun updatePasswordAuthenticationOn(on: Boolean)
}