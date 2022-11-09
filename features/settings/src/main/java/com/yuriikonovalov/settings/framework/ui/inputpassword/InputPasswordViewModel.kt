package com.yuriikonovalov.settings.framework.ui.inputpassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yuriikonovalov.common.application.usecases.CheckPassword
import com.yuriikonovalov.settings.presentation.inputpassword.InputPasswordEvent
import com.yuriikonovalov.settings.presentation.inputpassword.InputPasswordIntent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InputPasswordViewModel @Inject constructor(
    private val checkPassword: CheckPassword
) : ViewModel() {
    private val _eventFlow: MutableStateFlow<InputPasswordEvent?> = MutableStateFlow(null)
    val eventFlow get() = _eventFlow.asStateFlow()
    val eventConsumer = { _eventFlow.value = null }

    fun handleIntent(intent: InputPasswordIntent) {
        when (intent) {
            is InputPasswordIntent.CheckPassword -> onCheckPassword(intent.password)
        }
    }

    private fun onCheckPassword(password: String) {
        viewModelScope.launch {
            val resource = checkPassword(password)
            resource.onSuccess { correct ->
                _eventFlow.value = if (correct) {
                    InputPasswordEvent.CorrectPassword
                } else {
                    InputPasswordEvent.IncorrectPasswordToast
                }
            }
            resource.onFailure {
                _eventFlow.value = InputPasswordEvent.IncorrectPasswordToast
            }
        }
    }
}