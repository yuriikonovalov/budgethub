package com.yuriikonovalov.common.application.usecases.datasources

import kotlinx.coroutines.flow.Flow

interface IsBiometricAuthenticationOnSource {
    fun isBiometricAuthenticationOn(): Flow<Boolean>
}