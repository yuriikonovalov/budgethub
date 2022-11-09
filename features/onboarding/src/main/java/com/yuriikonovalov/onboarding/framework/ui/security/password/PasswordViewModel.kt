package com.yuriikonovalov.onboarding.framework.ui.security.password

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yuriikonovalov.common.application.usecases.IsBiometricAuthenticationAvailable
import com.yuriikonovalov.common.application.usecases.SetPassword
import com.yuriikonovalov.common.application.usecases.UpdatePasswordAuthenticationOn
import com.yuriikonovalov.onboarding.presentation.security.password.PasswordEvent
import com.yuriikonovalov.onboarding.presentation.security.password.PasswordIntent
import com.yuriikonovalov.onboarding.presentation.security.password.PasswordState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PasswordViewModel @Inject constructor(
    private val setPassword: SetPassword,
    private val updatePasswordAuthenticationOn: UpdatePasswordAuthenticationOn,
    private val isBiometricAuthenticationAvailable: IsBiometricAuthenticationAvailable
) : ViewModel() {
    private val _stateFlow = MutableStateFlow(PasswordState())
    private val _eventFlow = MutableStateFlow<PasswordEvent?>(null)
    private val currentState get() = stateFlow.value
    val stateFlow get() = _stateFlow.asStateFlow()
    val eventFlow get() = _eventFlow.asStateFlow()
    val eventConsumer: () -> Unit = { _eventFlow.value = null }

    private val continueDestination: PasswordEvent.Destination
        get() = if (isBiometricAuthenticationAvailable()) {
            PasswordEvent.Destination.BiometricAuthentication
        } else {
            PasswordEvent.Destination.AddAccount
        }

    fun handleIntent(intent: PasswordIntent) {
        when (intent) {
            is PasswordIntent.ChangePassword -> onChangePassword(intent.password)
            is PasswordIntent.ClickPositiveButton -> onClickPositiveButton()
            is PasswordIntent.ClickSkipButton -> onClickSkip()
        }
    }

    private fun onClickPositiveButton() {
        when (currentState.stage) {
            PasswordState.Stage.INPUT -> onClickContinueButton()
            PasswordState.Stage.REPEAT -> onClickOkButton()
        }
    }

    private fun onClickSkip() {
        _eventFlow.value = PasswordEvent.Skip
    }

    private fun onChangePassword(password: String) {
        _stateFlow.update { it.updatePassword(password) }
    }

    private fun onClickContinueButton() {
        _stateFlow.update { it.updateStage(PasswordState.Stage.REPEAT) }
        _eventFlow.value = PasswordEvent.ClearInput
    }

    private fun onClickOkButton() {
        if (currentState.passwordsMatch) {
            viewModelScope.launch {
                val resource = setPassword(currentState.password)
                resource.onSuccess { saved ->
                    _eventFlow.value = if (saved) {
                        updatePasswordAuthenticationOn(on = true)
                        PasswordEvent.Continue(continueDestination)
                    } else {
                        PasswordEvent.SavePasswordFailure
                    }
                }
                resource.onFailure {
                    _eventFlow.value = PasswordEvent.SavePasswordFailure
                }
            }
        } else {
            _eventFlow.value = PasswordEvent.PasswordsNotMatch
        }
    }
}