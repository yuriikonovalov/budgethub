package com.yuriikonovalov.common.framework.ui.addedittransaction

import com.google.common.truth.Truth.*
import com.yuriikonovalov.common.application.entities.category.CategoryType
import com.yuriikonovalov.common.application.entities.transaction.TransactionType
import com.yuriikonovalov.common.application.usecases.*
import com.yuriikonovalov.common.data.sources.*
import com.yuriikonovalov.shared_test.fakes.usecase.*
import com.yuriikonovalov.common.presentation.addedittransaction.AddEditTransactionEvent
import com.yuriikonovalov.common.presentation.addedittransaction.AddEditTransactionIntent
import com.yuriikonovalov.common.presentation.addedittransaction.AddEditTransactionState
import com.yuriikonovalov.shared_test.MainCoroutineRule
import com.yuriikonovalov.shared_test.model.accounts
import com.yuriikonovalov.shared_test.model.categories
import com.yuriikonovalov.shared_test.model.category
import com.yuriikonovalov.shared_test.model.transaction
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock

@OptIn(ExperimentalCoroutinesApi::class)
class AddEditTransactionViewModelTest {

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private fun initSUT(
        id: Long = AddEditTransactionFragment.ARGUMENT_NEW_TRANSACTION,
        getAllAccounts: GetAllAccounts = FakeGetAllAccounts(),
        saveTransaction: SaveTransaction = FakeSaveTransaction(),
        getTransaction: GetTransaction = FakeGetTransaction(transaction),
        updateTransaction: UpdateTransaction = FakeUpdateTransaction(),
        getIncomeCategories: GetIncomeCategories = FakeGetIncomeCategories(),
        getExpenseCategories: GetExpenseCategories = FakeGetExpenseCategories()
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
    fun `if an argument is ARGUMENT_NEW_TRANSACTION id - mode value should ADD`() {
        // BEFORE
        val sut = initSUT(id = AddEditTransactionFragment.ARGUMENT_NEW_TRANSACTION)

        // THEN
        val actual = sut.stateFlow.value.mode
        assertThat(actual).isEqualTo(AddEditTransactionState.Mode.ADD)
    }

    @Test
    fun `if an argument is a transaction's id - mode value should EDIT`() = runTest {
        // BEFORE
        val transaction = transaction
        val sut = initSUT(id = transaction.id, getTransaction = FakeGetTransaction(transaction))

        // WHEN
        // Load transaction.
        runCurrent()

        // THEN
        val actual = sut.stateFlow.value.mode
        assertThat(actual).isEqualTo(AddEditTransactionState.Mode.EDIT)
    }

    @Test
    fun `if click an account button when there's no accounts - event value should be CreateAccount`() =
        runTest {
            // BEFORE
            val sut = initSUT(getAllAccounts = FakeGetAllAccounts(emptyList()))

            // WHEN
            // Load accounts.
            advanceUntilIdle()
            sut.handleIntent(AddEditTransactionIntent.ClickAccount)

            // THEN
            val actual = sut.eventFlow.value
            assertThat(actual).isInstanceOf(AddEditTransactionEvent.CreateAccount::class.java)
        }

    @Test
    fun `if click an account button when there's accounts - event value should be ShowAccounts`() =
        runTest {
            // BEFORE
            val sut = initSUT(getAllAccounts = FakeGetAllAccounts(accounts(2)))

            // WHEN
            // Load accounts.
            advanceUntilIdle()
            sut.handleIntent(AddEditTransactionIntent.ClickAccount)

            // THEN
            val actual = sut.eventFlow.value
            assertThat(actual).isInstanceOf(AddEditTransactionEvent.ShowAccounts::class.java)
        }

    @Test
    fun `save a transaction - event value should be NavigateBack`() = runTest {
        // BEFORE
        val sut = initSUT()

        // WHEN
        // Load accounts.
        runCurrent()
        sut.handleIntent(AddEditTransactionIntent.ChangeAmount(100.0))
        sut.handleIntent(AddEditTransactionIntent.ClickSaveButton)
        // Save a transaction.
        runCurrent()

        // THEN
        val actual = sut.eventFlow.value
        assertThat(actual).isInstanceOf(AddEditTransactionEvent.NavigateBack::class.java)
    }

    @Test
    fun `if an account is not null, amount more than 0 and a category is not null - a save button in enabled`() =
        runTest {
            // BEFORE
            val sut = initSUT()

            // WHEN
            // Load accounts.
            runCurrent()
            // For an account and a category there's default values.
            sut.handleIntent(AddEditTransactionIntent.ChangeAmount(100.0))

            // THEN
            val actual = sut.stateFlow.value.saveButtonEnabled
            assertThat(actual).isTrue()
        }

    @Test
    fun `if at least an account is null - a save button in not enabled`() = runTest {
        // BEFORE
        val sut = initSUT(getAllAccounts = FakeGetAllAccounts(emptyList()))

        // WHEN
        // Load accounts.
        runCurrent()
        sut.handleIntent(AddEditTransactionIntent.ChangeAmount(100.0))
        sut.handleIntent(AddEditTransactionIntent.ChangeCategory(category))

        // THEN
        val actual = sut.stateFlow.value.saveButtonEnabled
        assertThat(actual).isFalse()
    }

    @Test
    fun `if at least amount is not more than 0 - a save button in not enabled`() = runTest {
        // BEFORE
        val sut = initSUT()

        // WHEN
        // Load accounts.
        runCurrent()
        // The default value for amount is 0.0
        // An account and a category are set to default values.

        // THEN
        val actual = sut.stateFlow.value.saveButtonEnabled
        assertThat(actual).isFalse()
    }

    @Test
    fun `if at least a category is null - a save button in not enabled`() = runTest {
        // BEFORE
        val sut = initSUT(
            getIncomeCategories = FakeGetIncomeCategories(emptyList()),
            getExpenseCategories = FakeGetExpenseCategories(emptyList())
        )

        // WHEN
        // Load accounts.
        runCurrent()
        sut.handleIntent(AddEditTransactionIntent.ChangeAmount(100.0))
        // An account is set to default value.
        // A category is set to null as there's no categories at all.

        // THEN
        val actual = sut.stateFlow.value.saveButtonEnabled
        assertThat(actual).isFalse()
    }

    @Test
    fun `if change transaction type - category value should be set to the first item of the category of corresponding type`() =
        runTest {
            // BEFORE
            val incomeCategories = categories(3, CategoryType.INCOME)
            val expenseCategories = categories(3, CategoryType.EXPENSE)
            val sut = initSUT(
                getIncomeCategories = FakeGetIncomeCategories(incomeCategories),
                getExpenseCategories = FakeGetExpenseCategories(expenseCategories)
            )
            val expectedIncomeCategory = incomeCategories.first()
            val expectedExpenseCategory = expenseCategories.first()

            // WHEN
            // Load data.
            runCurrent()
            sut.handleIntent(AddEditTransactionIntent.ChangeType(TransactionType.INCOME))
            //      THEN ->
            var actualCategory = sut.stateFlow.value.category
            assertThat(actualCategory).isEqualTo(expectedIncomeCategory)

            sut.handleIntent(AddEditTransactionIntent.ChangeType(TransactionType.EXPENSE))
            //      THEN ->
            actualCategory = sut.stateFlow.value.category
            assertThat(actualCategory).isEqualTo(expectedExpenseCategory)
        }
}