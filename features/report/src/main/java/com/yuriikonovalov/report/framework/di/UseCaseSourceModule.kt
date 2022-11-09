package com.yuriikonovalov.report.framework.di

import com.yuriikonovalov.report.application.usecases.sources.*
import com.yuriikonovalov.report.data.sources.*
import com.yuriikonovalov.report.data.sources.GetAggregateExpenseCategoriesFlatByCurrencySourceImpl
import com.yuriikonovalov.report.data.sources.GetAggregateIncomeCategoriesFlatByCurrencySourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent

@Module
@InstallIn(ActivityRetainedComponent::class)
abstract class UseCaseSourceModule {
    @Binds
    abstract fun bindGetAggregateExpenseCategoriesFlatByCurrencySource(
        impl: GetAggregateExpenseCategoriesFlatByCurrencySourceImpl
    ): GetAggregateExpenseCategoriesFlatByCurrencySource

    @Binds
    abstract fun bindGetAggregateIncomeCategoriesFlatByCurrencySource(
        impl: GetAggregateIncomeCategoriesFlatByCurrencySourceImpl
    ): GetAggregateIncomeCategoriesFlatByCurrencySource

    @Binds
    abstract fun bindGetTransfersPagedSource(
        impl: GetTransfersPagedSourceImpl
    ): GetTransfersPagedSource

    @Binds
    abstract fun bindGetTransactionsPagedSource(
        impl: GetTransactionsPagedSourceImpl
    ): GetTransactionsPagedSource

}