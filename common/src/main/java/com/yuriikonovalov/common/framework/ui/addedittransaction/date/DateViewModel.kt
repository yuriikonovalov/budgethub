package com.yuriikonovalov.common.framework.ui.addedittransaction.date

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.yuriikonovalov.common.presentation.addedittransaction.date.DateEvent
import com.yuriikonovalov.common.presentation.addedittransaction.date.DateIntent
import com.yuriikonovalov.common.presentation.addedittransaction.date.DateState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.OffsetDateTime

class DateViewModel @AssistedInject constructor(
    @Assisted("minDate") private val minDate: OffsetDateTime?,
    @Assisted("date") private val date: OffsetDateTime?
) : ViewModel() {
    private val _stateFlow = MutableStateFlow(DateState())
    private val _eventFlow = MutableStateFlow<DateEvent?>(null)
    val stateFlow get() = _stateFlow.asStateFlow()
    val eventFlow get() = _eventFlow.asStateFlow()
    val eventConsumer = { _eventFlow.value = null }

    init {
        _stateFlow.value = DateState(
            minDate = minDate,
            maxDate = OffsetDateTime.now(),
            date = date ?: OffsetDateTime.now()
        )
    }

    fun handleIntent(intent: DateIntent) {
        when (intent) {
            is DateIntent.ChangeDate -> onChangeDate(intent.date)
            is DateIntent.ClickPositiveButton -> onClickPositiveButton()
        }
    }

    private fun onClickPositiveButton() {
        _eventFlow.value = DateEvent.PositiveButtonClick(stateFlow.value.date)
    }

    private fun onChangeDate(date: OffsetDateTime) {
        _stateFlow.update { it.updateDate(date) }
    }

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("minDate") minDate: OffsetDateTime?,
            @Assisted("date") date: OffsetDateTime?
        ): DateViewModel
    }

    companion object {
        @Suppress("UNCHECKED_CAST")
        fun provideFactory(
            assistedFactory: Factory,
            minDate: OffsetDateTime?,
            date: OffsetDateTime?
        ) = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(minDate, date) as T
            }
        }
    }
}