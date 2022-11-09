package com.yuriikonovalov.settings.presentation.createpassword

sealed class CreatePasswordIntent {
    data class ChangePassword(val password: String) : CreatePasswordIntent()
    object ClickContinueButton : CreatePasswordIntent()
    object ClickOkButton : CreatePasswordIntent()
}
