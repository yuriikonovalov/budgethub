package com.yuriikonovalov.common.framework.ui.addedittransaction

import com.google.common.truth.Truth.*
import com.yuriikonovalov.common.application.entities.category.CategoryType
import com.yuriikonovalov.common.application.usecases.*
import com.yuriikonovalov.common.data.local.TransactionImagesLocalDataSource
import com.yuriikonovalov.common.data.sources.*
import com.yuriikonovalov.shared_test.fakes.usecase.*
import com.yuriikonovalov.common.framework.data.local.database.dao.AccountDao
import com.yuriikonovalov.common.framework.data.local.database.dao.CategoryDao
import com.yuriikonovalov.common.framework.data.local.database.dao.TransactionDao
import com.yuriikonovalov.common.framework.data.local.database.dao.TransferDao
import com.yuriikonovalov.common.framework.data.local.database.datasources.AccountsLocalDataSourceImpl
import com.yuriikonovalov.common.framework.data.local.database.datasources.CategoriesLocalDataSourceImpl
import com.yuriikonovalov.common.framework.data.local.database.datasources.TransactionsLocalDataSourceImpl
import com.yuriikonovalov.common.framework.data.local.database.datasources.TransfersLocalDataSourceImpl
import com.yuriikonovalov.common.framework.data.local.database.mapper.*
import com.yuriikonovalov.shared_test.MainCoroutineRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify

@OptIn(ExperimentalCoroutinesApi::class)
class AddEditTransactionViewModelIntegrationTest {

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private val mockAccountDao: AccountDao = mock()
    private val mockTransactionDao: TransactionDao = mock()
    private val mockTransferDao: TransferDao = mock()
    private val mockCategoryDao: CategoryDao = mock()
    private val accountsLocalDataSource = AccountsLocalDataSourceImpl(
        mockAccountDao,
        AccountMapperDb((CurrencyMapperDb()))
    )

    private val transactionsLocalDataSource = TransactionsLocalDataSourceImpl(
        mockTransactionDao,
        TransactionAggregateMapperDb(
            CategoryMapperDb(),
            AccountMapperDb(CurrencyMapperDb()),
            TagMapperDb()
        ),
        TagMapperDb()
    )
    private val mockTransactionImagesLocalDataSource: TransactionImagesLocalDataSource = mock()
    private val transfersLocalDataSource = TransfersLocalDataSourceImpl(
        mockTransferDao,
        TransferMapperDb(AccountMapperDb(CurrencyMapperDb()), TagMapperDb()),
        TagMapperDb()
    )
    private val categoriesLocalDataSource = CategoriesLocalDataSourceImpl(
        mockCategoryDao,
        CategoryMapperDb()
    )

    private val getAllAccounts =
        GetAllAccountsImpl(GetAllAccountsSourceImpl(accountsLocalDataSource))

    private val refreshAccountBalance = RefreshAccountBalanceImpl(
        RefreshAccountBalanceSourceImpl(
            accountsLocalDataSource,
            transfersLocalDataSource,
            transactionsLocalDataSource
        )
    )

    private val saveTransaction = SaveTransactionImpl(
        SaveTransactionSourceImpl(
            accountsLocalDataSource,
            transactionsLocalDataSource,
            mockTransactionImagesLocalDataSource
        ),
        refreshAccountBalance
    )

    private val getTransaction =
        GetTransactionImpl(GetTransactionSourceImpl(transactionsLocalDataSource))
    private val updateTransaction = UpdateTransactionImpl(
        UpdateTransactionSourceImpl(
            transactionsLocalDataSource,
            mockTransactionImagesLocalDataSource
        ),
        refreshAccountBalance
    )
    private val getIncomeCategories =
        GetIncomeCategoriesImpl(GetIncomeCategoriesSourceImpl(categoriesLocalDataSource))
    private val getExpenseCategories =
        GetExpenseCategoriesImpl(GetExpenseCategoriesSourceImpl(categoriesLocalDataSource))

    private fun initSUT(
        id: Long = AddEditTransactionFragment.ARGUMENT_NEW_TRANSACTION
    ): AddEditTransactionViewModel {
        return AddEditTransactionViewModel(
            id,
            getAllAccounts,
            saveTransaction,
            getTransaction,
            updateTransaction,
            getIncomeCategories,
            getExpenseCategories,
            mock()
        )
    }

    @Test
    fun `starting the view model - triggers accountDao, categoryDao`() = runTest {
        // BEFORE
        initSUT()
        advanceUntilIdle()
        verify(mockAccountDao, times(1)).getAll()
        verify(mockCategoryDao, times(1)).getCategoriesByType(CategoryType.EXPENSE)
        verify(mockCategoryDao, times(1)).getCategoriesByType(CategoryType.INCOME)
    }
}