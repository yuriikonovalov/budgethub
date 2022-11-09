package com.yuriikonovalov.common.presentation.transactiondetails

import com.yuriikonovalov.common.application.entities.category.Category
import com.yuriikonovalov.common.application.entities.transaction.Transaction
import com.yuriikonovalov.common.application.entities.transaction.TransactionType
import com.yuriikonovalov.common.framework.utils.date.DateFormat.toEeeeDdMmmmYyyy
import com.yuriikonovalov.common.framework.utils.money.MoneyFormat

data class TransactionDetailsState(
    val transaction: Transaction? = null
) {
    val currencyCode: String? get() = transaction?.account?.currency?.code
    val amount: String? get() = updateAmount()
    val accountName: String? get() = transaction?.account?.name
    val date: String? get() = transaction?.date?.toEeeeDdMmmmYyyy()
    val type: TransactionType? get() = transaction?.type
    val category: Category? get() = transaction?.category
    val tags: String? get() = updateTags()
    val note: String? get() = transaction?.note
    val photoPath: String? get() = transaction?.imagePath

    private fun updateAmount(): String? {
        return transaction?.let {
            when (it.type) {
                TransactionType.EXPENSE -> {
                    "-${MoneyFormat.getStringValue(it.amount)}"
                }
                TransactionType.INCOME -> {
                    "+${MoneyFormat.getStringValue(it.amount)}"
                }
            }
        }
    }

    private fun updateTags(): String? {
        return transaction?.tags?.let { tags ->
            if (tags.isEmpty()) {
                null
            } else {
                buildString {
                    tags.forEachIndexed { index, tag ->
                        append(tag.name)
                        if (tags.size > 1 && index != tags.lastIndex) {
                            append(", ")
                        }
                    }
                }
            }
        }
    }

    fun updateTransaction(transaction: Transaction?): TransactionDetailsState {
        return copy(transaction = transaction)
    }
}
