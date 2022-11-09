package com.yuriikonovalov.common.application.usecases

import com.yuriikonovalov.common.application.usecases.datasources.UpdateOnboardingCompletedSource
import javax.inject.Inject

class UpdateOnboardingCompletedImpl @Inject constructor(
    private val source: UpdateOnboardingCompletedSource
) : UpdateOnboardingCompleted {
    override suspend operator fun invoke(completed: Boolean) {
        source.updateOnboardingCompleted(completed)
    }
}