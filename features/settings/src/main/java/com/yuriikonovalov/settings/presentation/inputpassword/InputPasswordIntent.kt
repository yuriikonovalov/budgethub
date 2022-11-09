package com.yuriikonovalov.settings.presentation.inputpassword

sealed class InputPasswordIntent {
    data class CheckPassword(val password: String) : InputPasswordIntent()
}
