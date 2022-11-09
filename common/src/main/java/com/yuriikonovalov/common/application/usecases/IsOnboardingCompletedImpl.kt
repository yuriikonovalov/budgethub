package com.yuriikonovalov.common.application.usecases

import com.yuriikonovalov.common.application.usecases.datasources.IsOnboardingCompletedSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class IsOnboardingCompletedImpl @Inject constructor(
    private val source: IsOnboardingCompletedSource
) : IsOnboardingCompleted {
    override operator fun invoke(): Flow<Boolean> {
        return source.onboardingCompleted()
    }
}