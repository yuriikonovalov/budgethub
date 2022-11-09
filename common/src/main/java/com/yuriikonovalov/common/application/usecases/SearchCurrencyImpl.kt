package com.yuriikonovalov.common.application.usecases

import com.yuriikonovalov.common.application.Resource
import com.yuriikonovalov.common.application.entities.Currency
import javax.inject.Inject

class SearchCurrencyImpl @Inject constructor(
    private val getAllCurrencies: GetAllCurrencies
) : SearchCurrency {
    override operator fun invoke(query: String): Resource<List<Currency>> {
        var result = emptyList<Currency>()
        getAllCurrencies().onSuccess { currencies ->
            result = currencies.filter { currency ->
                currency.search(query)
            }
        }
        return Resource.successIfNotEmpty(result)
    }

    private fun Currency.search(query: String): Boolean {
        return code.contains(query, true)
                || name.contains(query, true)
                || symbol.contains(query, true)
    }
}