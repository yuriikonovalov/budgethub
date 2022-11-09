package com.yuriikonovalov.settings.framework.ui.createpassword

import androidx.lifecycle.ViewModel
import com.yuriikonovalov.settings.presentation.createpassword.CreatePasswordEvent
import com.yuriikonovalov.settings.presentation.createpassword.CreatePasswordIntent
import com.yuriikonovalov.settings.presentation.createpassword.CreatePasswordState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class CreatePasswordViewModel @Inject constructor() : ViewModel() {
    private val _stateFlow = MutableStateFlow(CreatePasswordState())
    private val _eventFlow = MutableStateFlow<CreatePasswordEvent?>(null)
    private val currentState get() = stateFlow.value
    val stateFlow get() = _stateFlow.asStateFlow()
    val eventFlow get() = _eventFlow.asStateFlow()
    val eventConsumer = { _eventFlow.value = null }

    fun handleIntent(intent: CreatePasswordIntent) {
        when (intent) {
            is CreatePasswordIntent.ChangePassword -> onChangePassword(intent.password)
            is CreatePasswordIntent.ClickContinueButton -> onClickContinueButton()
            is CreatePasswordIntent.ClickOkButton -> onClickOkButton()
        }
    }

    private fun onChangePassword(password: String) {
        _stateFlow.update { it.updatePassword(password) }
    }

    private fun onClickContinueButton() {
        _stateFlow.update { it.updateStage(CreatePasswordState.Stage.REPEAT) }
        _eventFlow.value = CreatePasswordEvent.ClearInput
    }

    private fun onClickOkButton() {
        _eventFlow.value = if (currentState.passwordsMatch) {
            CreatePasswordEvent.ClickPositiveButton(currentState.password)
        } else {
            CreatePasswordEvent.PasswordsNotMatch
        }
    }
}