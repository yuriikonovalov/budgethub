package com.yuriikonovalov.onboarding.presentation.security.password

sealed class PasswordIntent {
    object ClickSkipButton : PasswordIntent()
    data class ChangePassword(val password: String) : PasswordIntent()
    object ClickPositiveButton : PasswordIntent()
}
