package com.yuriikonovalov.common.application.usecases.datasources

interface CheckPasswordSource {
    suspend fun getPassword(): String?
}