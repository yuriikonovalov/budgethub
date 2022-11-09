package com.yuriikonovalov.login.presentation

sealed class LoginIntent {
    data class ChangePassword(val password: String) : LoginIntent()
    object ClickUseBiometricButton : LoginIntent()
    object ClickContinueButton : LoginIntent()
}
