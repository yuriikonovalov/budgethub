package com.yuriikonovalov.common.application.usecases.datasources

interface DeleteAccountSource {
    suspend fun deleteAccount(id: Long)
}