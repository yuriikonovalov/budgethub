package com.yuriikonovalov.budgethub.framework.ui

import android.content.Context
import android.graphics.Color
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItem
import androidx.test.espresso.contrib.RecyclerViewActions.scrollTo
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.filters.LargeTest
import com.google.common.truth.Truth.assertThat
import com.yuriikonovalov.budgethub.R
import com.yuriikonovalov.common.application.entities.Currency
import com.yuriikonovalov.common.application.entities.account.Account
import com.yuriikonovalov.common.application.entities.account.AccountType
import com.yuriikonovalov.common.application.entities.category.Category
import com.yuriikonovalov.common.application.entities.category.CategoryType
import com.yuriikonovalov.common.application.usecases.SaveAccount
import com.yuriikonovalov.common.application.usecases.SaveCategory
import com.yuriikonovalov.common.application.usecases.SaveTransaction
import com.yuriikonovalov.common.data.local.AppPreferences
import com.yuriikonovalov.common.framework.ui.addedittransaction.category.CategoryAdapter
import com.yuriikonovalov.common.framework.ui.common.TransactionItemAdapter
import com.yuriikonovalov.shared_test.model.account
import com.yuriikonovalov.shared_test.model.category
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.anyOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.OffsetDateTime
import javax.inject.Inject
import com.yuriikonovalov.common.R as RCommon
import com.yuriikonovalov.home.R as RHome

@ExperimentalCoroutinesApi
@LargeTest
@HiltAndroidTest
class MainActivityGeneralTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val activityScenarioRule = ActivityScenarioRule(MainActivity::class.java)

    @Inject
    @ApplicationContext
    lateinit var context: Context

    @Inject
    lateinit var appPreferences: AppPreferences

    @Inject
    lateinit var saveCategory: SaveCategory

    @Inject
    lateinit var saveAccount: SaveAccount

    @Inject
    lateinit var saveTransaction: SaveTransaction

    private lateinit var navController: NavController

    @Before
    fun setup() {
        hiltRule.inject()
    }

    private suspend fun setAccounts(accounts: List<Account>) {
        accounts.forEach {
            saveAccount(it)
        }
    }

    private suspend fun setCategories(categories: List<Category>) {
        categories.forEach {
            saveCategory(it)
        }
    }

    private suspend fun setPreferences(
        onboardingCompleted: Boolean = true,
        passwordAuthenticationOn: Boolean = false,
        biometricAuthenticationOn: Boolean = false,
        password: String = ""
    ) {
        appPreferences.updateOnboardingCompleted(onboardingCompleted)
        appPreferences.setPassword(password)
        appPreferences.updatePasswordAuthenticationOn(passwordAuthenticationOn)
        appPreferences.updateBiometricAuthenticationOn(biometricAuthenticationOn)
    }

    private fun setNavController() {
        activityScenarioRule.scenario.onActivity {
            val navHostFragment =
                it.supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
            navController = navHostFragment.navController
        }
    }

    @Test
    fun addTransactionAndOpenDetails() = runTest {
        setPreferences()
        setNavController()
        setAccounts(listOf(TestAccount.CARD, TestAccount.DEPOSIT))
        setCategories(
            listOf(
                TestCategory.Expense.FOOD,
                TestCategory.Expense.MEDICINE,
                TestCategory.Income.SALARY,
                TestCategory.Income.DIVIDENDS
            )
        )
        val amount = 743.66
        val category = TestCategory.Income.DIVIDENDS

        // Open AddEditTransaction fragment.
        onView(withId(RCommon.id.add_edit_transaction_fragment))
            .perform(click())

        // Check navContoller's current destination.
        navController.assertAt(RCommon.id.add_edit_transaction_fragment)

        // Check INCOME type.
        onView(withId(RCommon.id.type_income))
            .perform(click())

        // Check that the selected category is of the correct type.
        onView(withTagValue(`is`(text(RCommon.string.tag_view_category_title))))
            .check(
                matches(
                    anyOf(
                        withText(TestCategory.Income.SALARY.name),
                        withText(TestCategory.Income.DIVIDENDS.name)
                    )
                )
            )

        // Select category.
        onView(withId(RCommon.id.category))
            .perform(click())
        onView(withId(RCommon.id.categories))
            .perform(
                actionOnItem<CategoryAdapter.CategoryViewHolder>(
                    hasDescendant(withText(category.name)),
                    click()
                )
            )

        // Input amount.
        onView(withTagValue(`is`(text(RCommon.string.tag_view_amount_amount_edit_text))))
            .perform(click(), typeText(amount.toString()))

        Espresso.closeSoftKeyboard()
        // Before clicking a save button - save the previousBackStackEntry to use in an assertion.
        val originDestinationId = navController.previousBackStackEntry?.destination?.id

        onView(withId(RCommon.id.save_button))
            .check(matches(isEnabled()))
            .perform(click())

        navController.assertAt(originDestinationId)

        // Press on the transaction.
        onView(withId(RHome.id.transactions))
            .check(matches(isDisplayed()))
            .perform(
                scrollTo<TransactionItemAdapter.ExpenseIncomeTransactionViewHolder>(
                    hasDescendant(withText(category.name))
                ),
                actionOnItem<TransactionItemAdapter.ExpenseIncomeTransactionViewHolder>(
                    hasDescendant(withText(category.name)),
                    click()
                )
            )

        navController.assertAt(RCommon.id.transaction_details_fragment)
    }

    private fun NavController.assertAt(destinationId: Int?) {
        assertThat(currentDestination?.id).isEqualTo(destinationId)
    }

    private fun text(stringRes: Int): String = context.getString(stringRes)

    private object TestAccount {
        val CARD = account.copy(
            id = 0,
            name = "Card 5432",
            type = AccountType.CARD,
            balance = 14500.0,
            dateOfCreation = OffsetDateTime.now().minusMonths(1),
            color = Color.YELLOW,
            currency = Currency("USD", "US Dollar", "US\$")
        )

        val DEPOSIT = account.copy(
            id = 0,
            name = "Deposit A",
            type = AccountType.DEPOSIT,
            balance = 73050.53,
            dateOfCreation = OffsetDateTime.now().minusMonths(2),
            color = Color.CYAN,
            currency = Currency("UAH", "Ukrainian Hryvnia", "₴")
        )
    }

    private object TestCategory {
        object Income {
            val SALARY = category.copy(
                id = 0,
                type = CategoryType.INCOME,
                name = "Salary",
                color = Color.BLUE
            )

            val DIVIDENDS = SALARY.copy(
                name = "Dividends",
                color = Color.YELLOW
            )
        }

        object Expense {
            val FOOD = category.copy(
                id = 0,
                type = CategoryType.EXPENSE,
                name = "Food",
                color = Color.GREEN
            )

            val MEDICINE = FOOD.copy(
                name = "Medicine",
                color = Color.RED
            )
        }
    }

}