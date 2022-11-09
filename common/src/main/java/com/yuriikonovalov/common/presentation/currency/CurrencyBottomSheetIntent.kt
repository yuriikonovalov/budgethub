package com.yuriikonovalov.common.presentation.currency

sealed class CurrencyBottomSheetIntent {
    data class Search(val input: String) : CurrencyBottomSheetIntent()
}