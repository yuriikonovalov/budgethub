package com.yuriikonovalov.login.presentation

data class LoginState(
    val useBiometricButtonVisible: Boolean = true,
    val password: String = ""
) {
    fun updateUseBiometricButtonVisible(visible: Boolean): LoginState {
        return copy(useBiometricButtonVisible = visible)
    }

    fun updatePassword(password: String): LoginState {
        return copy(password = password)
    }

}