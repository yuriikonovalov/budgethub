package com.yuriikonovalov.common.framework.ui.addedittransaction

import android.content.Context
import androidx.core.os.bundleOf
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItem
import androidx.test.espresso.contrib.RecyclerViewActions.scrollTo
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import com.yuriikonovalov.common.R
import com.yuriikonovalov.common.application.entities.Tag
import com.yuriikonovalov.common.application.entities.category.CategoryType
import com.yuriikonovalov.common.application.entities.transaction.TransactionType
import com.yuriikonovalov.common.data.local.AccountsLocalDataSource
import com.yuriikonovalov.common.data.local.CategoriesLocalDataSource
import com.yuriikonovalov.common.data.local.TagsLocalDataSource
import com.yuriikonovalov.common.data.local.TransactionsLocalDataSource
import com.yuriikonovalov.common.framework.ui.addedittransaction.tag.TagBottomSheetAdapter
import com.yuriikonovalov.common.framework.utils.EspressoIdlingResource
import com.yuriikonovalov.common.framework.utils.money.MoneyFormat
import com.yuriikonovalov.shared_test.launchFragmentInHiltContainer
import com.yuriikonovalov.shared_test.model.account
import com.yuriikonovalov.shared_test.model.category
import com.yuriikonovalov.shared_test.model.transaction
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.hamcrest.Matchers.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@ExperimentalCoroutinesApi
@LargeTest
@HiltAndroidTest
class AddEditTransactionFragmentTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var idlingResource: EspressoIdlingResource

    @Inject
    lateinit var accountsSource: AccountsLocalDataSource

    @Inject
    lateinit var categoriesSource: CategoriesLocalDataSource

    @Inject
    lateinit var transactionsSource: TransactionsLocalDataSource

    @Inject
    lateinit var tagsSource: TagsLocalDataSource

    @Inject
    @ApplicationContext
    lateinit var context: Context

    @Before
    fun setup() {
        hiltRule.inject()
        IdlingRegistry.getInstance().register(idlingResource.instance)
    }

    @After
    fun teardown() {
        IdlingRegistry.getInstance().unregister(idlingResource.instance)
    }

    private fun argument(id: Long? = null) =
        bundleOf("transactionId" to (id ?: AddEditTransactionFragment.ARGUMENT_NEW_TRANSACTION))


    @Test
    fun ifAccountsAreEmpty_createAccountButtonIsDisplayed() {
        launchFragmentInHiltContainer<AddEditTransactionFragment>(fragmentArgs = argument())

        onView(withId(R.id.create_button))
            .check(matches(isDisplayed()))
    }

    @Test
    fun ifAccountsAreNotEmpty_createAccountButtonIsNotDisplayed() = runTest {
        accountsSource.saveAccount(account)
        launchFragmentInHiltContainer<AddEditTransactionFragment>(fragmentArgs = argument())

        onView(withId(R.id.create_button))
            .check(matches(not(isDisplayed())))
    }

    @Test
    fun ifStartFragment_firstAccountAndCategoryAreSetByDefault() = runTest {
        val expectedAccount = account.copy(id = 0, name = "Expected Account")
        val notExpectedAccount = account.copy(id = 0)
        val expectedCategory = category.copy(id = 0, name = "Expected Category")
        val notExpectedCategory = category.copy(id = 0)
        accountsSource.saveAccount(expectedAccount)
        accountsSource.saveAccount(notExpectedAccount)
        categoriesSource.saveCategory(expectedCategory)
        categoriesSource.saveCategory(notExpectedCategory)

        launchFragmentInHiltContainer<AddEditTransactionFragment>(fragmentArgs = argument())

        onView(withTagValue(`is`(text(R.string.tag_account_card_title))))
            .check(matches(withText(expectedAccount.name)))

        onView(withTagValue(`is`(text(R.string.tag_view_category_title))))
            .check(matches(withText(expectedCategory.name)))
    }

    @Test
    fun ifChangeTransactionType_selectedCategoryIsChangedToComplyWithTransactionType() = runTest {
        val account = account
        val income = category.copy(id = 0, name = "Income", type = CategoryType.INCOME)
        val expense = category.copy(id = 0, name = "Expense", type = CategoryType.EXPENSE)
        val idAccount = accountsSource.saveAccount(account)
        val idCategory = categoriesSource.saveCategory(income)
        categoriesSource.saveCategory(expense)
        transactionsSource.saveTransaction(
            transaction = transaction.copy(
                id = 0,
                account = account.copy(id = idAccount),
                category = income.copy(id = idCategory)
            )
        )

        launchFragmentInHiltContainer<AddEditTransactionFragment>(
            fragmentArgs = bundleOf("transactionId" to AddEditTransactionFragment.ARGUMENT_NEW_TRANSACTION)
        )

        // EXPENSE is selected when the fragment is open.
        onView(withTagValue(`is`(text(R.string.tag_view_category_title))))
            .check(matches(withText(expense.name)))

        onView(withId(R.id.type_income))
            .perform(click())
            .check(matches(isChecked()))

        onView(withTagValue(`is`(text(R.string.tag_view_category_title))))
            .check(matches(withText(income.name)))
    }


    @Test
    fun pressTagButtonAndAddTagAndSelectIt() = runTest {
        val expectedAccount = account.copy(id = 0, name = "Expected Account")
        val expectedCategory = category.copy(id = 0, name = "Expected Category")
        accountsSource.saveAccount(expectedAccount)
        categoriesSource.saveCategory(expectedCategory)
        val tagText = "tag text"

        launchFragmentInHiltContainer<AddEditTransactionFragment>(fragmentArgs = argument())

        onView(withId(R.id.tag))
            .perform(click())

        onView(withId(R.id.empty_placeholder))
            .check(matches(isDisplayed()))

        onView(withId(R.id.name_input))
            .check(matches(isDisplayed()))
            .perform(click())
            .perform(typeText(tagText))

        Espresso.closeSoftKeyboard()

        onView(withId(R.id.add_button))
            .check(matches(isEnabled()))
            .perform(click())

        onView(withId(R.id.tags))
            .perform(scrollTo<TagBottomSheetAdapter.ItemViewHolder>(withText(tagText)))
            .perform(actionOnItem<TagBottomSheetAdapter.ItemViewHolder>(withText(tagText), click()))

        onView(withText(tagText))
            .check(matches(isChecked()))
    }


    @Test
    fun ifPassExistingTransactionId_showTransactionDetails() = runTest {
        // BEFORE
        val tagExpected = Tag(id = 0, name = "checked")
        val tagUnexpected = Tag(id = 0, name = "unchecked")
        val tagExpectedId = tagsSource.saveTag(tagExpected)
        tagsSource.saveTag(tagUnexpected)
        val account = account.copy(id = 0)
        val category = category.copy(id = 0, name = "Income", type = CategoryType.INCOME)
        val idAccount = accountsSource.saveAccount(account)
        val idCategory = categoriesSource.saveCategory(category)
        val transaction = transaction.copy(
            id = 0,
            account = account.copy(id = idAccount),
            category = category.copy(id = idCategory),
            note = "Transaction note",
            type = TransactionType.INCOME,
            tags = listOf(tagExpected.copy(id = tagExpectedId))
        )
        val id = transactionsSource.saveTransaction(transaction)

        launchFragmentInHiltContainer<AddEditTransactionFragment>(fragmentArgs = argument(id))

        // Check main information.
        onView(withTagValue(`is`(text(R.string.tag_account_card_title))))
            .check(matches(withText(account.name)))

        onView(withTagValue(`is`(text(R.string.tag_view_category_title))))
            .check(matches(withText(category.name)))

        onView(withTagValue(`is`(text(R.string.tag_view_amount_amount_edit_text))))
            .check(matches(withText(MoneyFormat.getStringValue(transaction.amount))))


        // Check if the expected tag is checked.
        onView(withId(R.id.tag))
            .perform(click())

        onView(withId(R.id.tags))
            .perform(scrollTo<TagBottomSheetAdapter.ItemViewHolder>(withText(tagExpected.name)))

        onView(withText(tagExpected.name))
            .check(matches(isChecked()))

        onView(withText(tagUnexpected.name))
            .check(matches(not(isChecked())))

        // Hide the tag bottom sheet dialog.
        Espresso.pressBack()

        // Check the note.
        onView(withId(R.id.note))
            .perform(click())

        onView(withId(R.id.input))
            .check(matches(allOf(isDisplayed(), withText(transaction.note))))
    }

    private fun text(stringRes: Int): String {
        return context.getString(stringRes)
    }
}