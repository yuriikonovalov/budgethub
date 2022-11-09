package com.yuriikonovalov.settings.presentation.inputpassword

sealed interface InputPasswordEvent {
    object IncorrectPasswordToast : InputPasswordEvent
    object CorrectPassword : InputPasswordEvent
}
