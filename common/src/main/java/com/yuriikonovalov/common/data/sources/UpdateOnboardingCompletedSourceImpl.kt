package com.yuriikonovalov.common.data.sources

import com.yuriikonovalov.common.application.usecases.datasources.UpdateOnboardingCompletedSource
import com.yuriikonovalov.common.data.local.AppPreferences
import javax.inject.Inject

class UpdateOnboardingCompletedSourceImpl @Inject constructor(
    private val appPreferences: AppPreferences
) : UpdateOnboardingCompletedSource {
    override suspend fun updateOnboardingCompleted(onboardingCompleted: Boolean) {
        appPreferences.updateOnboardingCompleted(onboardingCompleted)
    }
}