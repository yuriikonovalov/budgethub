package com.yuriikonovalov.common.presentation.model

import com.yuriikonovalov.common.application.entities.TransactionAmount
import com.yuriikonovalov.common.framework.utils.money.MoneyFormat

data class TransactionAmountUi(
    val amount: String,
    val currencyCode: String,
    val type: TransactionAmount.Type
) {
    companion object {
        fun from(domain: TransactionAmount): TransactionAmountUi {
            return TransactionAmountUi(
                amount = domain.amountUi(),
                currencyCode = domain.currency.code,
                type = domain.type
            )
        }

        private fun TransactionAmount.amountUi(): String {
            return when (type) {
                TransactionAmount.Type.EXPENSE -> "-${MoneyFormat.getStringValue(amount)}"
                TransactionAmount.Type.INCOME -> "+${MoneyFormat.getStringValue(amount)}"
            }
        }
    }
}
