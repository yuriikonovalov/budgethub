package com.yuriikonovalov.common.framework.di

import com.yuriikonovalov.common.application.usecases.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class UseCaseModule {
    @Binds
    abstract fun bindIsPasswordAuthenticationOn(impl: IsPasswordAuthenticationOnImpl): IsPasswordAuthenticationOn

    @Binds
    abstract fun bindIsOnboardingCompleted(impl: IsOnboardingCompletedImpl): IsOnboardingCompleted

    @Binds
    abstract fun bindSaveAccount(impl: SaveAccountImpl): SaveAccount

    @Binds
    abstract fun bindUpdateAccount(impl: UpdateAccountImpl): UpdateAccount

    @Binds
    abstract fun bindGetAccount(impl: GetAccountImpl): GetAccount

    @Binds
    abstract fun bindGetAllCurrencies(impl: GetAllCurrenciesImpl): GetAllCurrencies

    @Binds
    abstract fun bindRefreshAccountBalance(impl: RefreshAccountBalanceImpl): RefreshAccountBalance

    @Binds
    abstract fun bindGetAllAccounts(impl: GetAllAccountsImpl): GetAllAccounts

    @Binds
    abstract fun bindSaveTransfer(impl: SaveTransferImpl): SaveTransfer


    @Binds
    abstract fun bindGetTransfer(impl: GetTransferImpl): GetTransfer

    @Binds
    abstract fun bindUpdateTransfer(impl: UpdateTransferImpl): UpdateTransfer

    @Binds
    abstract fun bindSaveTransaction(impl: SaveTransactionImpl): SaveTransaction

    @Binds
    abstract fun bindGetTransaction(impl: GetTransactionImpl): GetTransaction

    @Binds
    abstract fun bindUpdateTransaction(impl: UpdateTransactionImpl): UpdateTransaction

    @Binds
    abstract fun bindGetIncomeCategories(impl: GetIncomeCategoriesImpl): GetIncomeCategories

    @Binds
    abstract fun bindGetExpenseCategories(impl: GetExpenseCategoriesImpl): GetExpenseCategories

    @Binds
    abstract fun bindGetTags(impl: GetTagsImpl): GetTags

    @Binds
    abstract fun bindSaveTag(impl: SaveTagImpl): SaveTag

    @Binds
    abstract fun bindDeleteTag(impl: DeleteTagImpl): DeleteTag

    @Binds
    abstract fun bindDeleteCategory(impl: DeleteCategoryImpl): DeleteCategory

    @Binds
    abstract fun bindSearchCurrency(impl: SearchCurrencyImpl): SearchCurrency

    @Binds
    abstract fun bindSaveCategory(impl: SaveCategoryImpl): SaveCategory

    @Binds
    abstract fun bindGetCategoryIcons(impl: GetCategoryIconsImpl): GetCategoryIcons

    @Binds
    abstract fun bindDeleteTransaction(impl: DeleteTransactionImpl): DeleteTransaction

    @Binds
    abstract fun bindDeleteTransfer(impl: DeleteTransferImpl): DeleteTransfer

    @Binds
    abstract fun bindDeleteAccount(impl: DeleteAccountImpl): DeleteAccount

    @Binds
    abstract fun bindGetTransfersForPeriod(impl: GetTransfersForPeriodImpl): GetTransfersForPeriod

    @Binds
    abstract fun bindGetTransactionsForPeriod(impl: GetTransactionsForPeriodImpl): GetTransactionsForPeriod

    @Binds
    abstract fun bindGetTransactionItemsForPeriod(impl: GetTransactionItemsForPeriodImpl): GetTransactionItemsForPeriod

    @Binds
    abstract fun bindGetAvailableBalances(impl: GetAvailableBalancesImpl): GetAvailableBalances

    @Binds
    abstract fun bindGetIncomeForPeriod(impl: GetIncomeForPeriodImpl): GetIncomeForPeriod

    @Binds
    abstract fun bindGetExpenseForPeriod(impl: GetExpenseForPeriodImpl): GetExpenseForPeriod

    @Binds
    abstract fun bindCheckPassword(impl: CheckPasswordImpl): CheckPassword

    @Binds
    abstract fun bindIsBiometricAuthenticationOn(impl: IsBiometricAuthenticationOnImpl): IsBiometricAuthenticationOn

    @Binds
    abstract fun bindUpdateOnboardingCompleted(impl: UpdateOnboardingCompletedImpl): UpdateOnboardingCompleted

    @Binds
    abstract fun bindUpdateBiometricAuthenticationOn(impl: UpdateBiometricAuthenticationOnImpl): UpdateBiometricAuthenticationOn

    @Binds
    abstract fun bindSetPassword(impl: SetPasswordImpl): SetPassword

    @Binds
    abstract fun bindUpdatePasswordAuthenticationOn(impl: UpdatePasswordAuthenticationOnImpl): UpdatePasswordAuthenticationOn

    @Binds
    abstract fun bindIsBiometricAuthenticationAvailable(impl: IsBiometricAuthenticationAvailableImpl): IsBiometricAuthenticationAvailable

    @Binds
    abstract fun bindGetColors(impl: GetColorsImpl): GetColors
}