package com.yuriikonovalov.common.presentation.addeditaccount

import com.yuriikonovalov.common.application.entities.Currency
import com.yuriikonovalov.common.application.entities.account.AccountType

sealed class AddEditAccountIntent {
    data class ChangeName(val input: String) : AddEditAccountIntent()
    data class ChangeCurrency(val currency: Currency) : AddEditAccountIntent()
    data class ChangeBalance(val input: String) : AddEditAccountIntent()
    data class ChangeType(val type: AccountType) : AddEditAccountIntent()
    data class ChangeColor(val color: Int) : AddEditAccountIntent()
    data class AddColor(val color: Int) : AddEditAccountIntent()

    object ClickCurrencyButton : AddEditAccountIntent()
    object ClickColorButton : AddEditAccountIntent()
    object ClickSaveButton : AddEditAccountIntent()
}




