package com.yuriikonovalov.common.presentation.currency

import com.yuriikonovalov.common.application.entities.Currency

data class CurrencyBottomSheetState(
    val currencies: List<Currency> = emptyList(),
) {
    fun updateCurrencies(currencies: List<Currency>): CurrencyBottomSheetState {
        return copy(currencies = currencies)
    }
}
