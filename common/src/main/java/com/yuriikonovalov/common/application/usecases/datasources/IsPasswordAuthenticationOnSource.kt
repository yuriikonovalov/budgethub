package com.yuriikonovalov.common.application.usecases.datasources

import kotlinx.coroutines.flow.Flow

interface IsPasswordAuthenticationOnSource {
    fun isPasswordAuthenticationOn(): Flow<Boolean>
}