package com.yuriikonovalov.common.framework.utils.money

import android.icu.text.DecimalFormat
import android.icu.text.NumberFormat
import com.yuriikonovalov.common.framework.utils.money.MoneyFormat.Formatter.DISPLAY_MONEY_FORMAT_PATTERN
import com.yuriikonovalov.common.framework.utils.money.MoneyFormat.Formatter.fractionDecimalFormat
import com.yuriikonovalov.common.framework.utils.money.MoneyFormat.Formatter.groupingSeparator
import java.util.*

object MoneyFormat {
    object Formatter {
        private const val NUMBER_FORMAT_PATTERN = "#,##0"
        const val FRACTION_FORMAT_PATTERN = "#,##0."
        const val DISPLAY_MONEY_FORMAT_PATTERN = "#,##0.00"
        const val numberOfDecimalDigits = 2

        // As a workaround for the bug with a decimal separator in some no-US locales.
        private val FIXED_LOCALE = Locale.US

        val wholeNumberDecimalFormat =
            (NumberFormat.getNumberInstance(FIXED_LOCALE) as DecimalFormat)
                .apply { applyPattern(NUMBER_FORMAT_PATTERN) }
        val fractionDecimalFormat = (NumberFormat.getNumberInstance(FIXED_LOCALE) as DecimalFormat)

        val decimalSeparator: String
            get() = wholeNumberDecimalFormat.decimalFormatSymbols.decimalSeparator.toString()
        val groupingSeparator: String
            get() = wholeNumberDecimalFormat.decimalFormatSymbols.groupingSeparator.toString()
    }


    fun getDoubleValue(money: String): Double? {
        return try {
            money.replace(groupingSeparator, "").toDouble()
        } catch (e: NumberFormatException) {
            e.printStackTrace()
            null
        }
    }

    fun getStringValue(money: Double, pattern: String = DISPLAY_MONEY_FORMAT_PATTERN): String {
        val formatter = fractionDecimalFormat
        formatter.applyPattern(pattern)
        return formatter.format(money)
    }
}