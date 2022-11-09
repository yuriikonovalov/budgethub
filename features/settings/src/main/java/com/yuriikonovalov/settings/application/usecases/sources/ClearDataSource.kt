package com.yuriikonovalov.settings.application.usecases.sources

interface ClearDataSource {
    suspend fun deleteTransactions()
    suspend fun deleteTransfers()
    suspend fun deleteCategories()
    suspend fun deleteTags()
    suspend fun deleteAccounts()
}