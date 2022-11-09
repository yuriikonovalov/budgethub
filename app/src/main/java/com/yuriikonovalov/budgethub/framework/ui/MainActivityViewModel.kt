package com.yuriikonovalov.budgethub.framework.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yuriikonovalov.common.application.usecases.IsOnboardingCompleted
import com.yuriikonovalov.common.application.usecases.IsPasswordAuthenticationOn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val isOnboardingCompleted: IsOnboardingCompleted,
    private val isPasswordAuthenticationOn: IsPasswordAuthenticationOn
) : ViewModel() {
    private val _directionFlow = MutableStateFlow<MainActivityDirection?>(null)
    val directionFlow get() = _directionFlow.asStateFlow()

    init {
        viewModelScope.launch {
            val onboardingCompleted = isOnboardingCompleted().first()
            val passwordRequired = isPasswordAuthenticationOn().first()
            _directionFlow.value = when {
                onboardingCompleted && passwordRequired -> MainActivityDirection.Login
                onboardingCompleted && passwordRequired.not() -> MainActivityDirection.Home
                onboardingCompleted.not() -> MainActivityDirection.Onboarding
                else -> {
                    throw UnsupportedOperationException(
                        "Not expected case onboardingCompleted: $onboardingCompleted and loginRequired: $passwordRequired"
                    )
                }
            }
        }
    }

    fun consumeDirection() {
        _directionFlow.value = null
    }
}