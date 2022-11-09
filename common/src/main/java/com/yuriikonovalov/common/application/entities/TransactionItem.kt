package com.yuriikonovalov.common.application.entities

import com.yuriikonovalov.common.application.entities.transaction.Transaction
import java.time.OffsetDateTime

sealed class TransactionItem {
    abstract val id: Long
    abstract val date: OffsetDateTime

    data class ExpenseIncome(val transaction: Transaction) : TransactionItem() {
        override val id: Long
            get() = transaction.id
        override val date: OffsetDateTime
            get() = transaction.date
    }

    data class Transfer(val transfer: com.yuriikonovalov.common.application.entities.Transfer) : TransactionItem() {
        // Uses the minus version of the id as way to avoid the same ids for transactions and transfers.
        override val id: Long
            get() = -transfer.id
        override val date: OffsetDateTime
            get() = transfer.date
    }

    companion object {
        fun from(transaction: Transaction): TransactionItem =
            ExpenseIncome(transaction)

        fun from(transfer: com.yuriikonovalov.common.application.entities.Transfer): TransactionItem =
            Transfer(transfer)
    }
}