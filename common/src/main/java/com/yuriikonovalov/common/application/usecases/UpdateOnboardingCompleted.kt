package com.yuriikonovalov.common.application.usecases

interface UpdateOnboardingCompleted {
    suspend operator fun invoke(completed: Boolean)
}