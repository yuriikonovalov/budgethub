package com.yuriikonovalov.common.data.local

import com.yuriikonovalov.common.application.entities.Currency

interface CurrenciesLocalDataSource {
    fun getAllCurrencies(): List<Currency>
    fun getCurrencyByCode(codeISO: String): Currency
}