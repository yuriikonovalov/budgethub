package com.yuriikonovalov.onboarding.framework.ui.security.biometricauthentication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yuriikonovalov.common.application.usecases.UpdateBiometricAuthenticationOn
import com.yuriikonovalov.onboarding.presentation.security.biometricauthentication.BiometricAuthenticationEvent
import com.yuriikonovalov.onboarding.presentation.security.biometricauthentication.BiometricAuthenticationIntent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BiometricAuthenticationViewModel @Inject constructor(
    private val updateBiometricAuthenticationOn: UpdateBiometricAuthenticationOn
) : ViewModel() {
    private val _eventFlow = MutableStateFlow<BiometricAuthenticationEvent?>(null)
    val eventFlow get() = _eventFlow.asStateFlow()
    val eventConsumer: () -> Unit = { _eventFlow.value = null }

    fun handleIntent(intent: BiometricAuthenticationIntent) {
        when (intent) {
            is BiometricAuthenticationIntent.ClickSkipButton -> onClickSkipButton()
            is BiometricAuthenticationIntent.ClickEnableButton -> onClickEnableButton()
        }
    }

    private fun onClickSkipButton() {
        _eventFlow.value = BiometricAuthenticationEvent.ClickSkipButton
    }

    private fun onClickEnableButton() {
        viewModelScope.launch {
            updateBiometricAuthenticationOn(on = true)
            _eventFlow.value = BiometricAuthenticationEvent.EnableBiometricAuthentication
        }
    }
}