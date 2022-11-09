package com.yuriikonovalov.login.fragment.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yuriikonovalov.common.application.usecases.CheckPassword
import com.yuriikonovalov.common.application.usecases.IsBiometricAuthenticationOn
import com.yuriikonovalov.login.presentation.LoginEvent
import com.yuriikonovalov.login.presentation.LoginIntent
import com.yuriikonovalov.login.presentation.LoginState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val checkPassword: CheckPassword,
    isBiometricAuthenticationOn: IsBiometricAuthenticationOn
) : ViewModel() {
    private val _stateFlow = MutableStateFlow(LoginState())
    private val _eventFlow = MutableStateFlow<LoginEvent?>(null)
    private val currentState get() = stateFlow.value
    val stateFlow get() = _stateFlow.asStateFlow()
    val eventFlow get() = _eventFlow.asStateFlow()
    val eventConsumer: () -> Unit = { _eventFlow.value = null }

    init {
        viewModelScope.launch {
            val enabled = isBiometricAuthenticationOn().first()
            _stateFlow.update { it.updateUseBiometricButtonVisible(enabled) }
            // Prompt a user to use biometric authentication first.
            if (enabled) {
                _eventFlow.value = LoginEvent.ClickUseBiometricAuthentication
            }
        }
    }

    fun handleIntent(intent: LoginIntent) {
        when (intent) {
            is LoginIntent.ChangePassword -> onChangePassword(intent.password)
            is LoginIntent.ClickContinueButton -> onClickContinueButton()
            is LoginIntent.ClickUseBiometricButton -> onClickUseBiometricButton()
        }
    }

    private fun onChangePassword(password: String) {
        _stateFlow.update { it.updatePassword(password) }
    }

    private fun onClickContinueButton() {
        viewModelScope.launch {
            val resource = checkPassword(currentState.password)
            resource.onSuccess { correct ->
                _eventFlow.value = if (correct) {
                    LoginEvent.LoginSuccessful
                } else {
                    LoginEvent.IncorrectPassword
                }
            }
            resource.onFailure {
                _eventFlow.value = LoginEvent.IncorrectPassword
            }
        }
    }

    private fun onClickUseBiometricButton() {
        _eventFlow.value = LoginEvent.ClickUseBiometricAuthentication
    }
}