package com.yuriikonovalov.onboarding.presentation.security.biometricauthentication

sealed interface BiometricAuthenticationEvent {
    object ClickSkipButton : BiometricAuthenticationEvent
    object EnableBiometricAuthentication : BiometricAuthenticationEvent
}
