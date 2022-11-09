package com.yuriikonovalov.common.application.usecases

import kotlinx.coroutines.flow.Flow

interface IsBiometricAuthenticationOn {
    operator fun invoke(): Flow<Boolean>
}