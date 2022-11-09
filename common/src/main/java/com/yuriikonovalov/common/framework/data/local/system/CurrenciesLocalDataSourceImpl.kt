package com.yuriikonovalov.common.framework.data.local.system

import com.yuriikonovalov.common.application.entities.Currency
import com.yuriikonovalov.common.data.local.CurrenciesLocalDataSource
import javax.inject.Inject
import android.icu.util.Currency as AndroidCurrency

/**
 * Pay attention!
 *
 * [AndroidCurrency] is an alias for [android.icu.util.Currency].
 *
 * The reason for using the alias is the domain model [Currency].
 */
class CurrenciesLocalDataSourceImpl @Inject constructor() : CurrenciesLocalDataSource {
    private val systemCurrencies = AndroidCurrency.getAvailableCurrencies()
    private val availableSymbols = CurrencySymbol.getAvailableCodes()

    override fun getAllCurrencies(): List<Currency> {
        return systemCurrencies
            .filterOnlyWithSymbols()
            .map(::toDomain)
            .sortedBy { it.code }
    }

    override fun getCurrencyByCode(codeISO: String): Currency {
        return AndroidCurrency.getInstance(codeISO).toDomain()
    }

    @JvmName("extensionToDomain")
    private fun AndroidCurrency.toDomain(): Currency {
        return toDomain(this)
    }

    private fun toDomain(androidCurrency: AndroidCurrency): Currency {
        return Currency(
            code = androidCurrency.currencyCode,
            name = androidCurrency.displayName,
            symbol = CurrencySymbol.get(androidCurrency.currencyCode)
        )
    }

    private fun Set<AndroidCurrency>.filterOnlyWithSymbols(): Set<AndroidCurrency> {
        return this.filter {
            availableSymbols.contains(it.currencyCode)
        }.toSet()
    }
}