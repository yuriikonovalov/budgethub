package com.yuriikonovalov.common.presentation.addeditaccount

sealed class AddEditAccountEvent {
    object NavigateUp : AddEditAccountEvent()
    object CurrencyButtonClick : AddEditAccountEvent()
    object ColorButtonClick : AddEditAccountEvent()
    data class InputName(val name: String) : AddEditAccountEvent()
}