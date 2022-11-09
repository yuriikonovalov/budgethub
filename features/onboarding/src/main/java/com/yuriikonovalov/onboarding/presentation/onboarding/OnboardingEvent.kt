package com.yuriikonovalov.onboarding.presentation.onboarding

sealed interface OnboardingEvent {
    object CompleteOnboarding : OnboardingEvent
}
