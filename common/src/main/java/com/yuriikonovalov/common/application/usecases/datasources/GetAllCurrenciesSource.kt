package com.yuriikonovalov.common.application.usecases.datasources

import com.yuriikonovalov.common.application.entities.Currency

interface GetAllCurrenciesSource {
    fun getAllCurrencies(): List<Currency>
}