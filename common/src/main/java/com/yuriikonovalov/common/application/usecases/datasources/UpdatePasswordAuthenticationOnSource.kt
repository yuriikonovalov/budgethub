package com.yuriikonovalov.common.application.usecases.datasources

interface UpdatePasswordAuthenticationOnSource {
    suspend fun updatePasswordAuthenticationOn(on: Boolean)
}