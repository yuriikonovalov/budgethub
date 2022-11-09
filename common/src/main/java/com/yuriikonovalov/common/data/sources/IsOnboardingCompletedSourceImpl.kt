package com.yuriikonovalov.common.data.sources

import com.yuriikonovalov.common.application.usecases.datasources.IsOnboardingCompletedSource
import com.yuriikonovalov.common.data.local.AppPreferences
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class IsOnboardingCompletedSourceImpl @Inject constructor(
    private val appPreferences: AppPreferences
) : IsOnboardingCompletedSource {
    override fun onboardingCompleted(): Flow<Boolean> {
        return appPreferences.onboardingCompleted()
    }
}