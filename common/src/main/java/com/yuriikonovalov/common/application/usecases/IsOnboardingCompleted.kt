package com.yuriikonovalov.common.application.usecases

import kotlinx.coroutines.flow.Flow

interface IsOnboardingCompleted {
    operator fun invoke(): Flow<Boolean>
}