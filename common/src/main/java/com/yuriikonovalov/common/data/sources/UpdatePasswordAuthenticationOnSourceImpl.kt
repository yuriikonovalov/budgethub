package com.yuriikonovalov.common.data.sources

import com.yuriikonovalov.common.application.usecases.datasources.UpdatePasswordAuthenticationOnSource
import com.yuriikonovalov.common.data.local.AppPreferences
import javax.inject.Inject

class UpdatePasswordAuthenticationOnSourceImpl @Inject constructor(
    private val appPreferences: AppPreferences
) : UpdatePasswordAuthenticationOnSource {
    override suspend fun updatePasswordAuthenticationOn(on: Boolean) {
        appPreferences.updatePasswordAuthenticationOn(on)
    }
}