package com.yuriikonovalov.settings.data.sources

import com.yuriikonovalov.common.data.local.*
import com.yuriikonovalov.settings.application.usecases.sources.ClearDataSource
import javax.inject.Inject

class ClearDataSourceImpl @Inject constructor(
    private val accountsLocalDataSource: AccountsLocalDataSource,
    private val transactionsLocalDataSource: TransactionsLocalDataSource,
    private val transfersDataSourceLocal: TransfersLocalDataSource,
    private val tagsLocalDataSource: TagsLocalDataSource,
    private val categoriesLocalDataSource: CategoriesLocalDataSource
) : ClearDataSource {
    override suspend fun deleteTransactions() {
        transactionsLocalDataSource.deleteAll()
    }

    override suspend fun deleteTransfers() {
        transfersDataSourceLocal.deleteAll()
    }

    override suspend fun deleteCategories() {
        categoriesLocalDataSource.deleteAllCustom()
    }

    override suspend fun deleteTags() {
        tagsLocalDataSource.deleteAll()
    }

    override suspend fun deleteAccounts() {
        accountsLocalDataSource.deleteAll()
    }
}