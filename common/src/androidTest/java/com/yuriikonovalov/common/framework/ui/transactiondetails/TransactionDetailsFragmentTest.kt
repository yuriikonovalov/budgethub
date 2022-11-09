package com.yuriikonovalov.common.framework.ui.transactiondetails

import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import com.yuriikonovalov.common.R
import com.yuriikonovalov.common.application.entities.category.CategoryType
import com.yuriikonovalov.common.application.entities.transaction.Transaction
import com.yuriikonovalov.common.data.local.AccountsLocalDataSource
import com.yuriikonovalov.common.data.local.CategoriesLocalDataSource
import com.yuriikonovalov.common.data.local.TransactionsLocalDataSource
import com.yuriikonovalov.common.framework.utils.EspressoIdlingResource
import com.yuriikonovalov.common.framework.utils.date.DateFormat.toEeeeDdMmmmYyyy
import com.yuriikonovalov.shared_test.launchFragmentInHiltContainer
import com.yuriikonovalov.shared_test.model.account
import com.yuriikonovalov.shared_test.model.category
import com.yuriikonovalov.shared_test.model.transaction
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.verify
import org.mockito.kotlin.mock
import javax.inject.Inject

@LargeTest
@ExperimentalCoroutinesApi
@HiltAndroidTest
class TransactionDetailsFragmentTest {

    @get:Rule
    val hiltAndroidRule = HiltAndroidRule(this)

    @Inject
    lateinit var transactionsSource: TransactionsLocalDataSource

    @Inject
    lateinit var categoriesSource: CategoriesLocalDataSource

    @Inject
    lateinit var accountsSource: AccountsLocalDataSource

    @Inject
    lateinit var idlingResource: EspressoIdlingResource

    private lateinit var navController: NavController

    @Before
    fun setup() {
        hiltAndroidRule.inject()
        navController = mock()
        IdlingRegistry.getInstance().register(idlingResource.instance)
    }

    @After
    fun teardown() {
        IdlingRegistry.getInstance().unregister(idlingResource.instance)
    }

    private fun argument(id: Long? = null) = bundleOf("transactionId" to (id ?: -1))

    private suspend fun setTestTransaction(): Transaction {
        val account = account.copy(id = 0)
        val income = category.copy(id = 0, name = "Income", type = CategoryType.INCOME)
        val idAccount = accountsSource.saveAccount(account)
        val idCategory = categoriesSource.saveCategory(income)
        val transaction = transaction.copy(
            id = 0,
            account = account.copy(id = idAccount),
            category = income.copy(id = idCategory),
            note = "Transaction note"
        )
        val generatedId = transactionsSource.saveTransaction(transaction)
        return transaction.copy(id = generatedId)
    }

    @Test
    fun pressEditButton_navigateToAddEditTransactionFragment() = runTest {
        val id = setTestTransaction().id
        launchFragmentInHiltContainer<TransactionDetailsFragment>(fragmentArgs = argument(id)) {
            Navigation.setViewNavController(requireView(), navController)
        }

        onView(withId(R.id.menu_transaction_details_edit))
            .perform(click())

        verify(navController).navigate(
            TransactionDetailsFragmentDirections.toAddEditTransactionFragment(id)
        )
    }


    @Test
    fun pressNavigationButton_navigateUp() {
        launchFragmentInHiltContainer<TransactionDetailsFragment>(fragmentArgs = argument()) {
            Navigation.setViewNavController(requireView(), navController)
        }

        onView(withContentDescription(R.string.content_description_toolbar_navigation_button))
            .perform(click())

        verify(navController).navigateUp()
    }

    @Test
    fun pressDeleteButton_showConfirmationDialog() {
        launchFragmentInHiltContainer<TransactionDetailsFragment>(fragmentArgs = argument()) {
            Navigation.setViewNavController(requireView(), navController)
        }

        onView(withId(R.id.menu_transaction_details_delete))
            .perform(click())

        onView(withText(R.string.delete_a_transaction))
            .check(matches(isDisplayed()))
    }

    @Test
    fun delete_navigateUp() = runTest {
        val id = setTestTransaction().id
        launchFragmentInHiltContainer<TransactionDetailsFragment>(fragmentArgs = argument(id)) {
            Navigation.setViewNavController(requireView(), navController)
        }

        onView(withId(R.id.menu_transaction_details_delete))
            .perform(click())

        onView(withId(android.R.id.button1))
            .perform(click())

        verify(navController).navigateUp()
    }

    @Test
    fun loadDetailsFromLocal_detailsShown() = runTest {
        val expected = setTestTransaction()

        // BEFORE
        launchFragmentInHiltContainer<TransactionDetailsFragment>(fragmentArgs = argument(expected.id))

        onView(withId(R.id.note_icon))
            .check(matches(isDisplayed()))

        onView(withId(R.id.note))
            .check(matches(isDisplayed()))
            .check(matches(withText(expected.note)))

        onView(withId(R.id.date))
            .check(matches(withText(expected.date.toEeeeDdMmmmYyyy())))

        onView(withId(R.id.account_name))
            .check(matches(withText(expected.account.name)))
    }
}