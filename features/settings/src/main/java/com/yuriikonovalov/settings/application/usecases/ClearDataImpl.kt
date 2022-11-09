package com.yuriikonovalov.settings.application.usecases

import com.yuriikonovalov.settings.application.usecases.sources.ClearDataSource
import javax.inject.Inject

class ClearDataImpl @Inject constructor(private val source: ClearDataSource) : ClearData {
    override suspend operator fun invoke() {
        source.deleteAccounts()
        source.deleteTransfers()
        source.deleteTransactions()
        source.deleteCategories()
        source.deleteTags()
    }
}