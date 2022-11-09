package com.yuriikonovalov.common.application.usecases.datasources

import kotlinx.coroutines.flow.Flow

interface IsOnboardingCompletedSource {
    fun onboardingCompleted(): Flow<Boolean>
}