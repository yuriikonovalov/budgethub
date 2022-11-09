package com.yuriikonovalov.common.presentation.addedittransaction

import com.yuriikonovalov.common.application.entities.Tag
import com.yuriikonovalov.common.application.entities.account.Account
import java.time.OffsetDateTime

sealed class AddEditTransferEvent {
    object NavigateBack : AddEditTransferEvent()
    data class EditModeInput(val amountFrom: Double, val amountTo: Double) : AddEditTransferEvent()
    data class AccountFromClick(val accounts: List<Account>) : AddEditTransferEvent()
    data class AccountToClick(val accounts: List<Account>) : AddEditTransferEvent()
    data class NoteButtonClick(val note: String? = null) : AddEditTransferEvent()
    data class TagButtonClick(val tags: List<Tag>) : AddEditTransferEvent()
    data class DateButtonClick(val minDate: OffsetDateTime?, val date: OffsetDateTime?) :
        AddEditTransferEvent()
}