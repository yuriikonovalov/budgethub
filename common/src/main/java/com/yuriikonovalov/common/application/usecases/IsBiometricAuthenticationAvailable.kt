package com.yuriikonovalov.common.application.usecases

interface IsBiometricAuthenticationAvailable {
    operator fun invoke(): Boolean
}