package com.yuriikonovalov.common.application.usecases

import com.yuriikonovalov.common.application.Resource
import com.yuriikonovalov.common.application.entities.Currency
import com.yuriikonovalov.common.application.usecases.datasources.GetAllCurrenciesSource
import javax.inject.Inject

class GetAllCurrenciesImpl @Inject constructor(
    private val source: GetAllCurrenciesSource
) : GetAllCurrencies {
    override operator fun invoke(): Resource<List<Currency>> {
        return Resource.successIfNotEmpty(source.getAllCurrencies())
    }
}