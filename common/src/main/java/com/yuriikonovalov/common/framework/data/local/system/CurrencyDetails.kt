package com.yuriikonovalov.common.framework.data.local.system

import com.yuriikonovalov.common.application.entities.Currency
import android.icu.util.Currency as AndroidCurrency

/**
 * Pay attention!
 *
 * [AndroidCurrency] is an alias for [android.icu.util.Currency].
 *
 * The reason for using the alias is the domain model [Currency].
 */
object CurrencyDetails {
    fun getSymbol(codeISO: String): String {
        return CurrencySymbol.get(codeISO)
    }

    fun getName(codeISO: String): String {
        return try {
            AndroidCurrency.getInstance(codeISO).displayName
        } catch (e: Exception) {
            codeISO
        }
    }

}