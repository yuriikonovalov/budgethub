package com.yuriikonovalov.onboarding.presentation.onboarding

sealed interface OnboardingIntent {
    object CompleteOnboarding : OnboardingIntent
}
