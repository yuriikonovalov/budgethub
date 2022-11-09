package com.yuriikonovalov.common.presentation.addedittransaction

import android.net.Uri
import com.yuriikonovalov.common.application.entities.Tag
import com.yuriikonovalov.common.application.entities.account.Account
import com.yuriikonovalov.common.application.entities.category.Category
import com.yuriikonovalov.common.application.entities.transaction.Transaction
import com.yuriikonovalov.common.application.entities.transaction.TransactionType
import com.yuriikonovalov.common.application.entities.transaction.equalsTo
import java.time.OffsetDateTime

data class AddEditTransactionState(
    val id: Long = 0,
    val mode: Mode = Mode.ADD,
    val type: TransactionType = TransactionType.EXPENSE,
    val accounts: List<Account> = emptyList(),
    val account: Account? = null,
    val amount: Double? = null,
    val incomeCategories: List<Category> = emptyList(),
    val expenseCategories: List<Category> = emptyList(),
    val category: Category? = null,
    val note: String? = null,
    val date: OffsetDateTime = OffsetDateTime.now(),
    val imageUri: Uri? = null,
    val tags: List<Tag> = emptyList(),
) {
    val minDate: OffsetDateTime? get() = updateMinDate()
    val saveButtonEnabled: Boolean get() = updateSaveButtonEnabled()
    val chipIncomeVisible: Boolean get() = updateChipIncomeVisible()
    val chipExpenseVisible: Boolean get() = updateChipExpenseVisible()

    private fun updateChipIncomeVisible(): Boolean {
        return mode == Mode.ADD || type == TransactionType.INCOME
    }

    private fun updateChipExpenseVisible(): Boolean {
        return mode == Mode.ADD || type == TransactionType.EXPENSE
    }

    private fun updateMinDate(): OffsetDateTime? {
        return account?.dateOfCreation
    }

    private fun updateSaveButtonEnabled(): Boolean {
        return account != null && amount != null && category != null && amount > 0.0
    }

    fun updateType(type: TransactionType): AddEditTransactionState {
        // Resets the selected category with regard to the transaction type.
        val typeEqual = category?.type?.let { type.equalsTo(it) } ?: false
        return if (typeEqual) {
            copy(type = type)
        } else {
            val category = if (type == TransactionType.EXPENSE) {
                expenseCategories.firstOrNull()
            } else {
                incomeCategories.firstOrNull()
            }
            copy(type = type, category = category)
        }
    }

    fun updateAccounts(accounts: List<Account>): AddEditTransactionState {
        // For EDIT mode: keep the selected account for a transaction under editing.
        // For ADD mode: if the account list is not empty, then set value to the account.
        val newAccount = account ?: accounts.firstOrNull()
        return copy(accounts = accounts, account = newAccount)
    }

    fun updateAccount(account: Account): AddEditTransactionState {
        val date = tweakDate(account)
        return copy(account = account, date = date)
    }

    private fun tweakDate(account: Account) = if (account.dateOfCreation.isBefore(minDate)) {
        // a selected account was created before then the previous one.
        date
    } else {
        // a selected account was created after the previous one, so set date to null to avoid
        // a situation when the transaction date is earlier than the date of the account creation.
        OffsetDateTime.now()
    }

    fun updateAmount(amount: Double?): AddEditTransactionState {
        return copy(amount = amount)
    }

    fun updateCategory(category: Category): AddEditTransactionState {
        return copy(category = category)
    }

    fun updateNote(note: String?): AddEditTransactionState {
        return copy(note = note)
    }

    fun updateDate(date: OffsetDateTime): AddEditTransactionState {
        return copy(date = date)
    }

    fun updateImageUri(imageUri: Uri?): AddEditTransactionState {
        return copy(imageUri = imageUri)
    }

    fun updateTags(tags: List<Tag>): AddEditTransactionState {
        return copy(tags = tags)
    }

    fun updateAll(transaction: Transaction): AddEditTransactionState {
        val newAccounts = accounts.toMutableList().apply {
            remove(transaction.account)
            add(0, transaction.account)
        }.toList()

        return copy(
            id = transaction.id,
            mode = Mode.EDIT,
            type = transaction.type,
            account = transaction.account,
            amount = transaction.amount,
            category = transaction.category,
            note = transaction.note,
            date = transaction.date,
            imageUri = transaction.imagePath?.let { Uri.parse(it) },
            tags = transaction.tags,
            accounts = newAccounts
        )
    }

    fun updateIncomeCategories(categories: List<Category>): AddEditTransactionState {
        // Sets a first category from the list is there's no selected category yet.
        val category = if (type == TransactionType.INCOME) {
            category ?: categories.firstOrNull()
        } else {
            null
        }
        return copy(incomeCategories = categories, category = category)
    }

    fun updateExpenseCategories(categories: List<Category>): AddEditTransactionState {
        // Sets a first category from the list is there's no selected category yet.
        val category = if (type == TransactionType.EXPENSE) {
            category ?: categories.firstOrNull()
        } else {
            null
        }
        return copy(expenseCategories = categories, category = category)
    }

    enum class Mode {
        ADD, EDIT
    }
}