package com.yuriikonovalov.login.presentation

sealed interface LoginEvent {
    object LoginSuccessful : LoginEvent
    object IncorrectPassword : LoginEvent
    object ClickUseBiometricAuthentication : LoginEvent
}
