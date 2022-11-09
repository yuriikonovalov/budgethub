package com.yuriikonovalov.common.framework.ui.currency

import androidx.lifecycle.ViewModel
import com.yuriikonovalov.common.application.usecases.GetAllCurrencies
import com.yuriikonovalov.common.application.usecases.SearchCurrency
import com.yuriikonovalov.common.presentation.currency.CurrencyBottomSheetIntent
import com.yuriikonovalov.common.presentation.currency.CurrencyBottomSheetState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class CurrencyBottomSheetViewModel @Inject constructor(
    private val getAllCurrencies: GetAllCurrencies,
    private val searchCurrency: SearchCurrency
) : ViewModel() {
    private val initialState = CurrencyBottomSheetState()
    private val _stateFlow: MutableStateFlow<CurrencyBottomSheetState> =
        MutableStateFlow(initialState)
    val stateFlow get() = _stateFlow.asStateFlow()

    init {
        loadCurrencies()
    }

    fun handleIntent(intent: CurrencyBottomSheetIntent) {
        when (intent) {
            is CurrencyBottomSheetIntent.Search -> onSearch(intent.input)
        }
    }

    private fun onSearch(input: String) {
        if (input.isBlank()) {
            getAllCurrencies().onSuccess { currencies ->
                _stateFlow.update { it.updateCurrencies(currencies) }
            }
        } else {
            searchCurrency(input).onSuccess { currencies ->
                _stateFlow.update { it.updateCurrencies(currencies) }
            }
        }
    }

    private fun loadCurrencies() {
        getAllCurrencies()
            .onSuccess { currencies ->
                _stateFlow.update { it.updateCurrencies(currencies) }
            }
    }
}