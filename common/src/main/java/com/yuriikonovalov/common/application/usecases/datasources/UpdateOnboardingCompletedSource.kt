package com.yuriikonovalov.common.application.usecases.datasources

interface UpdateOnboardingCompletedSource {
    suspend fun updateOnboardingCompleted(onboardingCompleted: Boolean)
}