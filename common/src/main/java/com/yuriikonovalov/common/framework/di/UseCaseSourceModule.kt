package com.yuriikonovalov.common.framework.di

import com.yuriikonovalov.common.application.usecases.datasources.*
import com.yuriikonovalov.common.data.sources.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class UseCaseSourceModule {

    @Binds
    abstract fun bindGetAllAccountsSource(impl: GetAllAccountsSourceImpl): GetAllAccountsSource

    @Binds
    abstract fun bindGetAllCurrenciesSource(impl: GetAllCurrenciesSourceImpl): GetAllCurrenciesSource

    @Binds
    abstract fun bindGetIncomeCategoriesSource(impl: GetIncomeCategoriesSourceImpl): GetIncomeCategoriesSource

    @Binds
    abstract fun bindGetExpenseCategoriesSource(impl: GetExpenseCategoriesSourceImpl): GetExpenseCategoriesSource

    @Binds
    abstract fun bindGetCategoryIconsSource(impl: GetCategoryIconsSourceImpl): GetCategoryIconsSource

    @Binds
    abstract fun bindSaveAccountSource(impl: SaveAccountSourceImpl): SaveAccountSource

    @Binds
    abstract fun bindSaveCategoryFamilySource(impl: SaveTagSourceImpl): SaveTagSource

    @Binds
    abstract fun bindSaveCategorySource(impl: SaveCategorySourceImpl): SaveCategorySource

    @Binds
    abstract fun bindUpdateAccountSource(impl: UpdateAccountSourceImpl): UpdateAccountSource

    @Binds
    abstract fun bindGetAccountSource(impl: GetAccountSourceImpl): GetAccountSource

    @Binds
    abstract fun bindGetTagsSource(impl: GetTagsSourceImpl): GetTagsSource

    @Binds
    abstract fun bindGetAvailableBalancesSource(impl: GetAvailableBalancesSourceImpl): GetAvailableBalancesSource

    @Binds
    abstract fun bindSaveTransactionSource(impl: SaveTransactionSourceImpl): SaveTransactionSource

    @Binds
    abstract fun bindSaveTransferSource(impl: SaveTransferSourceImpl): SaveTransferSource

    @Binds
    abstract fun bindGetTransfersForPeriodSource(impl: GetTransfersForPeriodSourceImpl): GetTransfersForPeriodSource

    @Binds
    abstract fun bindGetTransactionsForPeriodSource(impl: GetTransactionsForPeriodSourceImpl): GetTransactionsForPeriodSource

    @Binds
    abstract fun bindDeleteTagSource(impl: DeleteTagSourceImpl): DeleteTagSource

    @Binds
    abstract fun bindGetTransactionSource(impl: GetTransactionSourceImpl): GetTransactionSource

    @Binds
    abstract fun bindGetTransferSource(impl: GetTransferSourceImpl): GetTransferSource

    @Binds
    abstract fun bindUpdateTransactionSource(impl: UpdateTransactionSourceImpl): UpdateTransactionSource

    @Binds
    abstract fun bindDeleteTransactionSource(impl: DeleteTransactionSourceImpl): DeleteTransactionSource

    @Binds
    abstract fun bindDeleteTransferSource(impl: DeleteTransferSourceImpl): DeleteTransferSource

    @Binds
    abstract fun bindRefreshAccountBalanceSource(impl: RefreshAccountBalanceSourceImpl): RefreshAccountBalanceSource

    @Binds
    abstract fun bindUpdateTransferSource(impl: UpdateTransferSourceImpl): UpdateTransferSource

    @Binds
    abstract fun bindGetIncomeForPeriodSource(impl: GetIncomeForPeriodSourceImpl): GetIncomeForPeriodSource

    @Binds
    abstract fun bindGetExpenseForPeriodSource(impl: GetExpenseForPeriodSourceImpl): GetExpenseForPeriodSource

    @Binds
    abstract fun bindDeleteAccountSource(impl: DeleteAccountSourceImpl): DeleteAccountSource

    @Binds
    abstract fun bindIsOnboardingCompletedSource(impl: IsOnboardingCompletedSourceImpl): IsOnboardingCompletedSource

    @Binds
    abstract fun bindUpdateOnboardingCompletedSource(impl: UpdateOnboardingCompletedSourceImpl): UpdateOnboardingCompletedSource

    @Binds
    abstract fun bindSetPasswordSource(impl: SetPasswordSourceImpl): SetPasswordSource

    @Binds
    abstract fun bindCheckPasswordSource(impl: CheckPasswordSourceImpl): CheckPasswordSource

    @Binds
    abstract fun bindUpdateBiometricAuthenticationOnSource(impl: UpdateBiometricAuthenticationOnSourceImpl): UpdateBiometricAuthenticationOnSource

    @Binds
    abstract fun bindIsBiometricAuthenticationOnSource(impl: IsBiometricAuthenticationOnSourceImpl): IsBiometricAuthenticationOnSource

    @Binds
    abstract fun bindIsPasswordAuthenticationOnSource(impl: IsPasswordAuthenticationOnSourceImpl): IsPasswordAuthenticationOnSource

    @Binds
    abstract fun bindUpdatePasswordAuthenticationOnSource(impl: UpdatePasswordAuthenticationOnSourceImpl): UpdatePasswordAuthenticationOnSource

    @Binds
    abstract fun bindDeleteCategorySource(impl: DeleteCategorySourceImpl): DeleteCategorySource
}