package com.yuriikonovalov.report.framework.di

import com.yuriikonovalov.report.application.usecases.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent

@Module
@InstallIn(ActivityRetainedComponent::class)
abstract class UseCaseModule {

    @Binds
    abstract fun bindGetTransfersPaged(impl: GetTransfersPagedImpl): GetTransfersPaged

    @Binds
    abstract fun bindGetTransactionsPaged(impl: GetTransactionsPagedImpl): GetTransactionsPaged

    @Binds
    abstract fun bindGetCategoriesWithNumberOfTransactions(impl: GetCategoriesWithNumberOfTransactionsImpl): GetCategoriesWithNumberOfTransactions

    @Binds
    abstract fun bindGetAggregateIncomeCategoriesFlatByCurrency(impl: GetAggregateIncomeCategoriesFlatByCurrencyImpl): GetAggregateIncomeCategoriesFlatByCurrency

    @Binds
    abstract fun bindGetAggregateExpenseCategoriesFlatByCurrency(impl: GetAggregateExpenseCategoriesFlatByCurrencyImpl): GetAggregateExpenseCategoriesFlatByCurrency
}