package com.yuriikonovalov.common.presentation.addedittransaction

import android.net.Uri
import com.yuriikonovalov.common.application.entities.Tag
import com.yuriikonovalov.common.application.entities.account.Account
import com.yuriikonovalov.common.application.entities.category.CategoryType
import com.yuriikonovalov.common.application.entities.transaction.TransactionType
import java.time.OffsetDateTime

sealed class AddEditTransactionEvent {
    data class ShowAccounts(val accounts: List<Account>) : AddEditTransactionEvent()
    data class ShowCategories(val type: CategoryType) : AddEditTransactionEvent()
    data class NoteButtonClick(val note: String? = null) : AddEditTransactionEvent()
    data class DateButtonClick(val minDate: OffsetDateTime?, val date: OffsetDateTime?) :
        AddEditTransactionEvent()

    data class PhotoButtonClick(val imageUri: Uri? = null) : AddEditTransactionEvent()
    data class TagButtonClick(val tags: List<Tag>) : AddEditTransactionEvent()
    object NavigateBack : AddEditTransactionEvent()
    data class EditModeInput(val type: TransactionType, val amount: Double) :
        AddEditTransactionEvent()

    object CreateAccount : AddEditTransactionEvent()
}