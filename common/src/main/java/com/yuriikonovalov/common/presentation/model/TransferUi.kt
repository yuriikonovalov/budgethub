package com.yuriikonovalov.common.presentation.model

import android.graphics.Color
import androidx.core.graphics.ColorUtils
import com.yuriikonovalov.common.application.entities.Transfer
import com.yuriikonovalov.common.application.util.TRANSFER_ICON
import com.yuriikonovalov.common.framework.utils.date.DateFormat.toDdMmmmYyyy
import com.yuriikonovalov.common.framework.utils.money.MoneyFormat

data class TransferUi(
    val currencyFromCode: String,
    val currencyToCode: String,
    val amountFrom: String,
    val amountTo: String,
    val date: String,
    val backgroundColor: Int,
    val iconColor: Int,
    val icon: String = TRANSFER_ICON,
) {
    companion object {
        fun from(domain: Transfer): TransferUi {
            return TransferUi(
                currencyFromCode = domain.accountFrom.currency.code,
                currencyToCode = domain.accountTo.currency.code,
                amountFrom = domain.amountFromUi(),
                amountTo = domain.amountToUi(),
                date = domain.date.toDdMmmmYyyy(),
                backgroundColor = ColorUtils.setAlphaComponent(Color.BLACK, 50),
                iconColor = Color.BLACK
            )
        }

        private fun Transfer.amountFromUi(): String {
            val amountString = MoneyFormat.getStringValue(amountFrom)
            return "- $amountString"
        }

        private fun Transfer.amountToUi(): String {
            val amountString = MoneyFormat.getStringValue(amountTo)
            return "+ $amountString"
        }
    }
}
