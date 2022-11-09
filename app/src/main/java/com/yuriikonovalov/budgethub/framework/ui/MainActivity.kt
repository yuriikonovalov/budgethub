package com.yuriikonovalov.budgethub.framework.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.yuriikonovalov.budgethub.databinding.ActivityMainBinding
import com.yuriikonovalov.common.NavGraphDirections
import com.yuriikonovalov.common.framework.common.extentions.launchSafely
import com.yuriikonovalov.common.framework.ui.addedittransaction.AddEditTransactionFragment
import com.yuriikonovalov.common.framework.utils.doOnApplyWindowInsetsCompat
import com.yuriikonovalov.common.framework.utils.navigationBars
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.filterNotNull

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainActivityViewModel by viewModels()
    private val navController: NavController by lazy { binding.getNavController() }

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.applyWindowInsets()
        binding.bindBottomNavigation(navController)

        launchSafely {
            viewModel.directionFlow.filterNotNull().collect { direction ->
                when (direction) {
                    is MainActivityDirection.Onboarding -> {
                        navController.navigate(NavGraphDirections.toOnboarding())
                    }
                    is MainActivityDirection.Home -> {
                        navController.navigate(NavGraphDirections.toHome())
                    }
                    is MainActivityDirection.Login -> {
                        navController.navigate(NavGraphDirections.toLogin())
                    }
                }
                // Sets the directionFlow value as null.
                viewModel.consumeDirection()
            }
        }
    }

    private fun ActivityMainBinding.applyWindowInsets() {
        bottomNavigationView.doOnApplyWindowInsetsCompat { navView, windowInsetsCompat, initialPadding ->
            navView.updatePadding(bottom = initialPadding.bottom + windowInsetsCompat.navigationBars.bottom)
        }
    }

    private fun ActivityMainBinding.bindBottomNavigation(navController: NavController) {
        bottomNavigationView.setupWithNavController(navController)
        setAddTransactionButton(navController)
        setNavigationViewVisibility(navController)
    }

    private fun ActivityMainBinding.setNavigationViewVisibility(navController: NavController) {
        // The bottom navigation view is only visible for the following destinations.
        val topDestinations = listOf(
            com.yuriikonovalov.home.R.id.home_fragment,
            com.yuriikonovalov.accounts.R.id.accounts_fragment,
            com.yuriikonovalov.report.R.id.report_fragment,
            com.yuriikonovalov.settings.R.id.settings_fragment
        )
        navController.addOnDestinationChangedListener { _, destination, _ ->
            bottomNavigationView.isVisible = destination.id in topDestinations
        }
    }

    private fun ActivityMainBinding.setAddTransactionButton(navController: NavController) {
        bottomNavigationView.menu.findItem(com.yuriikonovalov.common.R.id.add_edit_transaction_fragment)
            .setOnMenuItemClickListener {
                navController.navigate(
                    NavGraphDirections.toAddEditTransactionFragment(
                        AddEditTransactionFragment.ARGUMENT_NEW_TRANSACTION
                    )
                )
                true
            }
    }

    private fun ActivityMainBinding.getNavController(): NavController {
        val navHostFragment =
            supportFragmentManager.findFragmentById(navHostFragment.id) as NavHostFragment
        return navHostFragment.navController
    }
}