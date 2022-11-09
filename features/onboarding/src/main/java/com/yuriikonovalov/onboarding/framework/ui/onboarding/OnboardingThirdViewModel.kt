package com.yuriikonovalov.onboarding.framework.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yuriikonovalov.common.application.usecases.UpdateOnboardingCompleted
import com.yuriikonovalov.onboarding.presentation.onboarding.OnboardingEvent
import com.yuriikonovalov.onboarding.presentation.onboarding.OnboardingIntent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingThirdViewModel @Inject constructor(
    private val updateOnboardingCompleted: UpdateOnboardingCompleted
) : ViewModel() {
    private val _eventFlow = MutableStateFlow<OnboardingEvent?>(null)
    val eventFlow get() = _eventFlow.asStateFlow()
    val eventConsumer: () -> Unit = { _eventFlow.value = null }

    fun handleIntent(intent: OnboardingIntent) {
        when (intent) {
            OnboardingIntent.CompleteOnboarding -> onCompleteOnboarding()
        }
    }

    private fun onCompleteOnboarding() {
        viewModelScope.launch {
            updateOnboardingCompleted(completed = true)
            _eventFlow.value = OnboardingEvent.CompleteOnboarding
        }
    }
}