package com.yuriikonovalov.common.presentation.transferdetails

import com.yuriikonovalov.common.application.entities.Transfer
import com.yuriikonovalov.common.application.entities.account.Account
import com.yuriikonovalov.common.framework.utils.date.DateFormat.toEeeeDdMmmmYyyy
import com.yuriikonovalov.common.framework.utils.money.MoneyFormat

data class TransferDetailsState(
    val transfer: Transfer? = null
) {
    val accountFrom: Account? get() = transfer?.accountFrom
    val accountTo: Account? get() = transfer?.accountTo
    val amountFrom: String? get() = updateAmountFrom()
    val amountTo: String? get() = updateAmountTo()
    val date: String? get() = transfer?.date?.toEeeeDdMmmmYyyy()
    val tags: String? get() = updateTags()
    val note: String? get() = transfer?.note

    private fun updateAmountFrom(): String? {
        return transfer?.let {
            "-${MoneyFormat.getStringValue(it.amountFrom)}"
        }
    }

    private fun updateAmountTo(): String? {
        return transfer?.let {
            "+${MoneyFormat.getStringValue(it.amountTo)}"
        }
    }

    private fun updateTags(): String? {
        return transfer?.tags?.let { tags ->
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

    fun updateTransfer(transfer: Transfer?): TransferDetailsState {
        return copy(transfer = transfer)
    }
}
