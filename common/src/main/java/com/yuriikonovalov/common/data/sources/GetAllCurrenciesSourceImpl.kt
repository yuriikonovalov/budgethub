package com.yuriikonovalov.common.data.sources

import com.yuriikonovalov.common.application.entities.Currency
import com.yuriikonovalov.common.application.usecases.datasources.GetAllCurrenciesSource
import com.yuriikonovalov.common.data.local.CurrenciesLocalDataSource
import javax.inject.Inject

class GetAllCurrenciesSourceImpl @Inject constructor(private val currenciesLocalSource: CurrenciesLocalDataSource) :
    GetAllCurrenciesSource {
    override fun getAllCurrencies(): List<Currency> {
        return currenciesLocalSource.getAllCurrencies()
    }
}