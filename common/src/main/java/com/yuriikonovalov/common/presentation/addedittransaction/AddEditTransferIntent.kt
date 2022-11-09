package com.yuriikonovalov.common.presentation.addedittransaction

import com.yuriikonovalov.common.application.entities.Tag
import com.yuriikonovalov.common.application.entities.account.Account
import java.time.OffsetDateTime

sealed class AddEditTransferIntent {
    data class ChangeAccountFrom(val accountFrom: Account) : AddEditTransferIntent()
    data class ChangeAccountTo(val accountTo: Account) : AddEditTransferIntent()
    data class ChangeAmountFrom(val amountFrom: Double?) : AddEditTransferIntent()
    data class ChangeAmountTo(val amountTo: Double?) : AddEditTransferIntent()
    data class ChangeNote(val note: String?) : AddEditTransferIntent()
    data class ChangeDate(val date: OffsetDateTime) : AddEditTransferIntent()
    data class ChangeTags(val tags: List<Tag>) : AddEditTransferIntent()

    object ClickSaveButton : AddEditTransferIntent()
    object ClickAccountFrom : AddEditTransferIntent()
    object ClickAccountTo : AddEditTransferIntent()
    object ClickNoteButton : AddEditTransferIntent()
    object ClickDateButton : AddEditTransferIntent()
    object ClickTagButton : AddEditTransferIntent()
}


