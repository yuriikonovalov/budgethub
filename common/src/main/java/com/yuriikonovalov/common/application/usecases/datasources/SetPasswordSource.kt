package com.yuriikonovalov.common.application.usecases.datasources

interface SetPasswordSource {
    suspend fun savePassword(password: String): Boolean
}