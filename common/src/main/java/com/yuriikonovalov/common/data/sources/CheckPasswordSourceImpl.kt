package com.yuriikonovalov.common.data.sources

import com.yuriikonovalov.common.application.usecases.datasources.CheckPasswordSource
import com.yuriikonovalov.common.data.local.AppPreferences
import javax.inject.Inject

class CheckPasswordSourceImpl @Inject constructor(
    private val appPreferences: AppPreferences
) : CheckPasswordSource {
    override suspend fun getPassword(): String? {
        return appPreferences.getPassword()
    }
}