package com.yuriikonovalov.common.framework.ui.addedittransaction.note

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.yuriikonovalov.common.presentation.addedittransaction.note.EnterNoteIntent
import com.yuriikonovalov.common.presentation.addedittransaction.note.EnterNoteState
import com.yuriikonovalov.common.presentation.addedittransaction.note.EnterNoteEvent
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


class EnterNoteViewModel @AssistedInject constructor(
    @Assisted note: String?
) : ViewModel() {
    private val _stateFlow = MutableStateFlow(EnterNoteState())
    private val currentState get() = stateFlow.value
    private val _eventFlow = MutableStateFlow<EnterNoteEvent?>(null)
    val stateFlow get() = _stateFlow.asStateFlow()
    val eventFlow get() = _eventFlow.asStateFlow()
    val eventConsumer: () -> Unit = { _eventFlow.value = null }

    init {
        _stateFlow.update { it.updateInput(note) }
        _eventFlow.value = EnterNoteEvent.SetText(currentState.input)
    }

    fun handleIntent(intent: EnterNoteIntent) {
        when (intent) {
            is EnterNoteIntent.ChangeInput -> onChangeInput(intent.input)
            is EnterNoteIntent.ClickPositiveButton -> onClickPositiveButton()
            is EnterNoteIntent.ClickNegativeButton -> onClickNegativeButton()
        }
    }

    private fun onChangeInput(input: String) {
        _stateFlow.value = currentState.updateInput(input)
    }

    private fun onClickPositiveButton() {
        _eventFlow.value = EnterNoteEvent.PositiveButtonClick(currentState.input)
    }

    private fun onClickNegativeButton() {
        _eventFlow.value = EnterNoteEvent.NegativeButtonClick
    }

    @AssistedFactory
    interface Factory {
        fun create(note: String?): EnterNoteViewModel
    }

    companion object {
        @Suppress("UNCHECKED_CAST")
        fun provideFactory(assistedFactory: Factory, note: String?) =
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return assistedFactory.create(note) as T
                }
            }
    }
}