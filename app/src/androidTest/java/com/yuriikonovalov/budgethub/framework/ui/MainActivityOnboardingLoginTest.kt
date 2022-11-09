package com.yuriikonovalov.budgethub.framework.ui

import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.filters.LargeTest
import com.google.common.truth.Truth.assertThat
import com.yuriikonovalov.budgethub.R
import com.yuriikonovalov.common.application.usecases.SaveAccount
import com.yuriikonovalov.common.application.usecases.SaveCategory
import com.yuriikonovalov.common.application.usecases.SaveTransaction
import com.yuriikonovalov.common.data.local.AppPreferences
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject
import com.yuriikonovalov.home.R as RHome
import com.yuriikonovalov.login.R as RLogin
import com.yuriikonovalov.onboarding.R as ROnboarding

@ExperimentalCoroutinesApi
@LargeTest
@HiltAndroidTest
class MainActivityOnboardingLoginTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val activityScenarioRule = ActivityScenarioRule(MainActivity::class.java)

    @Inject
    lateinit var appPreferences: AppPreferences

    private lateinit var navController: NavController

    @Before
    fun setup() {
        hiltRule.inject()
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
    fun ifFirstStart_showOnboarding() = runTest {
        setPreferences(onboardingCompleted = false)
        setNavController()

        assertThat(navController.currentDestination?.id).isEqualTo(ROnboarding.id.onboarding_first_fragment)

        onView(withId(ROnboarding.id.next_button))
            .perform(click())

        assertThat(navController.currentDestination?.id).isEqualTo(ROnboarding.id.onboarding_second_fragment)


        onView(withId(ROnboarding.id.next_button))
            .perform(click())

        assertThat(navController.currentDestination?.id).isEqualTo(ROnboarding.id.onboarding_third_fragment)

        onView(withId(ROnboarding.id.get_started_button))
            .check(matches(isDisplayed()))
    }

    @Test
    fun ifOnboardingCompleted_shouldDisplayHomeFragment() = runTest {
        setPreferences()
        setNavController()

        assertThat(navController.currentDestination?.id).isEqualTo(RHome.id.home_fragment)
    }

    @Test
    fun ifPasswordOn_shouldDisplayLoginFragment() = runTest {
        setPreferences(passwordAuthenticationOn = true)
        setNavController()

        assertThat(navController.currentDestination?.id).isEqualTo(RLogin.id.login_fragment)
    }

    @Test
    fun ifPasswordOn_loginWithPassword() = runTest {
        val password = "password"
        setPreferences(
            passwordAuthenticationOn = true,
            password = password
        )
        setNavController()

        onView(withId(RLogin.id.password))
            .perform(click())
            .perform(typeText(password))

        closeSoftKeyboard()

        onView(withId(RLogin.id.continue_button))
            .perform(click())

        assertThat(navController.currentDestination?.id).isEqualTo(RHome.id.home_fragment)
    }
}