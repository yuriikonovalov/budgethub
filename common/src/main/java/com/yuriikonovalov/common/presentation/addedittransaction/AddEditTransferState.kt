package com.yuriikonovalov.common.presentation.addedittransaction

import com.yuriikonovalov.common.application.entities.Tag
import com.yuriikonovalov.common.application.entities.Transfer
import com.yuriikonovalov.common.application.entities.account.Account
import java.time.OffsetDateTime

data class AddEditTransferState(
    val id: Long? = null,
    val mode: Mode = Mode.ADD,
    val accounts: List<Account> = emptyList(),
    val accountFrom: Account? = null,
    val accountTo: Account? = null,
    val amountFrom: Double? = null,
    val amountTo: Double? = null,
    val note: String? = null,
    val date: OffsetDateTime = OffsetDateTime.now(),
    val tags: List<Tag> = emptyList(),
) {
    val minDate: OffsetDateTime get() = updateMinDate()
    val differentCurrencies: Boolean get() = updateDifferentCurrencies()
    val saveButtonEnabled: Boolean get() = updateSaveButtonEnabled()

    private fun updateDifferentCurrencies(): Boolean {
        return accountFrom != null
                && accountTo != null
                && accountFrom.currency.code != accountTo.currency.code
    }

    private fun updateSaveButtonEnabled(): Boolean {
        return if (differentCurrencies) {
            accountFrom != null
                    && amountFrom != null
                    && amountFrom > 0.0
                    && accountTo != null
                    && amountTo != null
                    && amountTo > 0.0
        } else {
            accountFrom != null
                    && amountFrom != null
                    && amountFrom > 0.0
                    && accountTo != null
        }
    }

    private fun updateMinDate(): OffsetDateTime {
        // Use the nearest account creation date as minDate for the calendar view
        // to avoid a situation when the date of the default is earlier of one of the dates
        // of the account creation.
        return accountFrom?.dateOfCreation?.let { accountFromDate ->
            accountTo?.dateOfCreation?.let { accountToDate ->
                if (accountFromDate.isBefore(accountToDate)) {
                    accountToDate
                } else {
                    accountFromDate
                }
            }
        } ?: OffsetDateTime.now()
    }

    fun updateAccounts(accounts: List<Account>): AddEditTransferState {
        // For EDIT mode: keep the account selected for a transaction under editing.
        // For ADD mode: if the account list is not empty, then set value to the account.
        val newAccountFrom = accountFrom ?: accounts.firstOrNull()
        // If the account list is not empty and there's at least one account different from the accountFrom,
        // then set value to the accountTo.
        val newAccountTo = accountTo ?: accounts.firstOrNull { it != newAccountFrom }
        val newDate = tweakDateFromPair(accountFrom, accountTo)
        return copy(
            accounts = accounts,
            accountFrom = newAccountFrom,
            accountTo = newAccountTo,
            date = newDate
        )
    }

    private fun tweakDateFromPair(accountFrom: Account?, accountTo: Account?): OffsetDateTime {
        val accountFromDate = accountFrom?.let { tweakDate(it) } ?: OffsetDateTime.now()
        val accountToDate = accountTo?.let { tweakDate(it) } ?: OffsetDateTime.now()
        // the nearest date is valid.
        return if (accountFromDate.isBefore(accountToDate)) {
            accountToDate
        } else {
            accountFromDate
        }
    }

    fun updateAccountFrom(accountFrom: Account): AddEditTransferState {
        val date = tweakDate(accountFrom)
        // For case when a user selects an accountFrom the same as the accountTo.
        if (accountFrom == accountTo) {
            // Find the first different account.
            val accountTo = accounts.firstOrNull { it != accountFrom }
            return copy(accountFrom = accountFrom, accountTo = accountTo, date = date)
        }

        return copy(accountFrom = accountFrom, date = date)
    }

    fun updateAccountTo(accountTo: Account): AddEditTransferState {
        val date = tweakDate(accountTo)
        return copy(accountTo = accountTo, date = date)
    }

    private fun tweakDate(account: Account) =
        if (account.dateOfCreation.isBefore(minDate)) {
            // a selected account was created before then the previous one.
            date
        } else {
            // a selected account was created after the previous one, so set date to null to avoid
            // a situation when the transaction date is earlier than the date of the account creation.
            OffsetDateTime.now()
        }

    fun updateAmountFrom(amountFrom: Double?): AddEditTransferState {
        return copy(amountFrom = amountFrom)
    }

    fun updateAmountTo(amountTo: Double?): AddEditTransferState {
        return copy(amountTo = amountTo)
    }

    fun updateNote(note: String?): AddEditTransferState {
        return copy(note = note)
    }

    fun updateDate(date: OffsetDateTime): AddEditTransferState {
        return copy(date = date)
    }


    fun updateTags(tags: List<Tag>): AddEditTransferState {
        return copy(tags = tags)
    }

    fun updateAll(transfer: Transfer): AddEditTransferState {
        return copy(
            id = transfer.id,
            mode = Mode.EDIT,
            accountFrom = transfer.accountFrom,
            accountTo = transfer.accountTo,
            amountFrom = transfer.amountFrom,
            amountTo = transfer.amountTo,
            date = transfer.date,
            note = transfer.note,
            tags = transfer.tags
        )
    }

    enum class Mode {
        ADD, EDIT
    }
}