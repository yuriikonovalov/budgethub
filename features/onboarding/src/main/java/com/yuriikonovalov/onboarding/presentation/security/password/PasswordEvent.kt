package com.yuriikonovalov.onboarding.presentation.security.password

sealed class PasswordEvent {
    object Skip : PasswordEvent()
    object PasswordsNotMatch : PasswordEvent()
    object ClearInput : PasswordEvent()
    data class Continue(val destination: Destination) : PasswordEvent()
    object SavePasswordFailure : PasswordEvent()

    sealed class Destination {
        object BiometricAuthentication : Destination()
        object AddAccount : Destination()
    }
}
