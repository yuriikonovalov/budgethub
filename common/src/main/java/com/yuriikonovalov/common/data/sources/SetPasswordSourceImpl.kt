package com.yuriikonovalov.common.data.sources

import com.yuriikonovalov.common.application.usecases.datasources.SetPasswordSource
import com.yuriikonovalov.common.data.local.AppPreferences
import javax.inject.Inject

class SetPasswordSourceImpl @Inject constructor(
    private val appPreferences: AppPreferences
) : SetPasswordSource {
    override suspend fun savePassword(password: String): Boolean {
        return appPreferences.setPassword(password)
    }
}