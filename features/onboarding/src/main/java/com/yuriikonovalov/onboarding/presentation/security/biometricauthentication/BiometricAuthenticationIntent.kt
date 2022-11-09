package com.yuriikonovalov.onboarding.presentation.security.biometricauthentication

sealed interface BiometricAuthenticationIntent {
    object ClickSkipButton : BiometricAuthenticationIntent
    object ClickEnableButton : BiometricAuthenticationIntent
}
