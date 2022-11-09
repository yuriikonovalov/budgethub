package com.yuriikonovalov.report.presentation.model

import android.content.Context
import android.graphics.Color
import androidx.core.graphics.ColorUtils
import com.yuriikonovalov.common.R
import com.yuriikonovalov.common.framework.utils.money.MoneyFormat
import com.yuriikonovalov.common.presentation.model.NameMapperUi
import com.yuriikonovalov.report.application.entities.AggregateCategoryFlatByCurrency

data class AggregateCategoryFlatByCurrencyUi(
    val name: String,
    val colorIcon: Int,
    val colorBackground: Int,
    val iconPath: String?,
    val amount: Double,
    val amountString: String,
    val numberOfTransactions: Int,
    val numberOfTransactionsString: String,
) {
    companion object {
        fun from(
            context: Context,
            domain: AggregateCategoryFlatByCurrency,
        ): AggregateCategoryFlatByCurrencyUi {
            return AggregateCategoryFlatByCurrencyUi(
                name = domain.nameUi(context),
                colorIcon = domain.colorIconUi(),
                colorBackground = domain.colorBackgroundUi(),
                iconPath = domain.iconPath,
                amount = domain.amount,
                amountString = domain.amountUi(),
                numberOfTransactions = domain.numberOfTransactions,
                numberOfTransactionsString = domain.numberOfTransactionsUi(context)
            )
        }


        private fun AggregateCategoryFlatByCurrency.amountUi(): String {
            val amount = MoneyFormat.getStringValue(amount)
            val currencyCode = currency.code
            return "$amount $currencyCode"
        }

        private fun AggregateCategoryFlatByCurrency.numberOfTransactionsUi(context: Context): String {
            return context.resources.getQuantityString(
                R.plurals.transactions,
                numberOfTransactions,
                numberOfTransactions
            )
        }

        private fun AggregateCategoryFlatByCurrency.nameUi(context: Context): String {
            return NameMapperUi.mapName(context, name)
        }

        private fun AggregateCategoryFlatByCurrency.colorBackgroundUi(): Int {
            return ColorUtils.setAlphaComponent(color ?: Color.BLACK, 50)
        }

        private fun AggregateCategoryFlatByCurrency.colorIconUi(): Int {
            return color ?: Color.BLACK
        }
    }
}
