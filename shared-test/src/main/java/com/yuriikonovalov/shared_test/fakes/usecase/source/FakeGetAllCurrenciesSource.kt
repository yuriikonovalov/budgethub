package com.yuriikonovalov.shared_test.fakes.usecase.source

import com.yuriikonovalov.common.application.entities.Currency
import com.yuriikonovalov.common.application.usecases.datasources.GetAllCurrenciesSource

class FakeGetAllCurrenciesSource(private val currencies: List<Currency>) : GetAllCurrenciesSource {
    override fun getAllCurrencies(): List<Currency> {
        return currencies
    }
}