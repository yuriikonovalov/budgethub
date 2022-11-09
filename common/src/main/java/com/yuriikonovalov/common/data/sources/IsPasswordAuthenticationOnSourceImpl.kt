package com.yuriikonovalov.common.data.sources

import com.yuriikonovalov.common.application.usecases.datasources.IsPasswordAuthenticationOnSource
import com.yuriikonovalov.common.data.local.AppPreferences
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class IsPasswordAuthenticationOnSourceImpl @Inject constructor(
    private val appPreferences: AppPreferences
) : IsPasswordAuthenticationOnSource {
    override fun isPasswordAuthenticationOn(): Flow<Boolean> {
        return appPreferences.passwordAuthenticationOn()
    }
}