package com.yuriikonovalov.settings.presentation.createpassword

sealed class CreatePasswordEvent {
    data class ClickPositiveButton(val password: String) : CreatePasswordEvent()
    object PasswordsNotMatch : CreatePasswordEvent()
    object ClearInput : CreatePasswordEvent()
}
