package com.yuriikonovalov.common.presentation.addedittransaction

import android.net.Uri
import com.yuriikonovalov.common.application.entities.Tag
import com.yuriikonovalov.common.application.entities.account.Account
import com.yuriikonovalov.common.application.entities.category.Category
import com.yuriikonovalov.common.application.entities.transaction.TransactionType
import java.time.OffsetDateTime

sealed class AddEditTransactionIntent {
    data class ChangeType(val type: TransactionType) : AddEditTransactionIntent()
    data class ChangeAccount(val account: Account) : AddEditTransactionIntent()
    data class ChangeAmount(val amount: Double?) : AddEditTransactionIntent()
    data class ChangeCategory(val category: Category) : AddEditTransactionIntent()
    data class ChangeNote(val note: String?) : AddEditTransactionIntent()
    data class ChangeDate(val date: OffsetDateTime) : AddEditTransactionIntent()
    data class ChangeImage(val imageUri: Uri?) : AddEditTransactionIntent()
    data class ChangeTags(val tags: List<Tag>) : AddEditTransactionIntent()

    object ClickSaveButton : AddEditTransactionIntent()
    object ClickCategory : AddEditTransactionIntent()
    object ClickAccount : AddEditTransactionIntent()
    object ClickNoteButton : AddEditTransactionIntent()
    object ClickDateButton : AddEditTransactionIntent()
    object ClickPhotoButton : AddEditTransactionIntent()
    object ClickTagButton : AddEditTransactionIntent()
}


