package com.yuriikonovalov.home.framework.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.yuriikonovalov.common.framework.common.extentions.collectEvent
import com.yuriikonovalov.common.framework.common.extentions.launchSafely
import com.yuriikonovalov.common.framework.common.extentions.setTintResource
import com.yuriikonovalov.common.framework.ui.common.ExpenseIncomeAdapter
import com.yuriikonovalov.common.framework.ui.common.TransactionItemAdapter
import com.yuriikonovalov.common.framework.utils.date.DateFormat.toMmmmYyyy
import com.yuriikonovalov.common.framework.utils.doOnApplyWindowInsetsCompat
import com.yuriikonovalov.common.framework.utils.systemBars
import com.yuriikonovalov.home.R
import com.yuriikonovalov.home.databinding.FragmentHomeBinding
import com.yuriikonovalov.home.presentation.home.HomeEvent
import com.yuriikonovalov.home.presentation.home.HomeIntent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private val viewModel: HomeViewModel by viewModels()
    private val availableBalanceAdapter by lazy {
        AvailableBalanceAdapter {
            viewModel.handleIntent(HomeIntent.ClickAvailableBalancePercent)
        }
    }
    private val transactionItemAdapter by lazy {
        TransactionItemAdapter(
            onTransactionClick = { viewModel.handleIntent(HomeIntent.OpenTransactionDetails(it)) },
            onTransferClick = { viewModel.handleIntent(HomeIntent.OpenTransferDetails(it)) })
    }
    private val incomeAdapter by lazy { ExpenseIncomeAdapter() }
    private val expenseAdapter by lazy { ExpenseIncomeAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.applyWindowInsets()
        binding.bindToolbar()
        binding.bindAvailableBalance()
        binding.bindIncomeExpenseContainer()
        binding.bindIncome()
        binding.bindExpense()
        binding.bindTransactions()
        binding.bindEmptyPlaceholder()
        collectEvents()
    }

    private fun collectEvents() {
        collectEvent(viewModel.eventFlow, viewModel.eventConsumer) { event ->
            when (event) {
                is HomeEvent.OpenTransactionDetails -> onOpenTransactionDetailsEvent(event.id)
                is HomeEvent.OpenTransferDetails -> onOpenTransferDetailsEvent(event.id)
                is HomeEvent.ClickAvailableBalancePercent -> onClickAvailableBalancePercentEvent()
            }
        }
    }

    private fun onClickAvailableBalancePercentEvent() {
        Toast.makeText(
            requireContext(),
            getString(R.string.available_balance_percent_toast),
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun onOpenTransferDetailsEvent(id: Long) {
        findNavController().navigate(HomeFragmentDirections.toTransferDetailsFragment(id))
    }

    private fun onOpenTransactionDetailsEvent(id: Long) {
        findNavController().navigate(HomeFragmentDirections.toTransactionDetailsFragment(id))
    }


    private fun FragmentHomeBinding.bindEmptyPlaceholder() {
        launchSafely {
            viewModel.stateFlow.map { it.emptyPlaceholderVisible }
                .distinctUntilChanged()
                .collect { visible ->
                    emptyPlaceholder.isVisible = visible
                }
        }
    }

    private fun FragmentHomeBinding.bindTransactions() {
        transactions.layoutManager = LinearLayoutManager(requireContext())
        transactions.adapter = transactionItemAdapter
        launchSafely {
            viewModel.stateFlow.map { it.transactions }
                .distinctUntilChanged()
                .collect(transactionItemAdapter::submitList)
        }
        launchSafely {
            viewModel.stateFlow.map { it.transactionsLoading }
                .distinctUntilChanged()
                .collect { transactionProgressBar.isVisible = it }
        }
        launchSafely {
            viewModel.stateFlow.map { it.transactionsVisible }
                .distinctUntilChanged()
                .collect {
                    transactions.isVisible = it
                    transactionLabel.isVisible = it
                }
        }
        launchSafely {
            viewModel.stateFlow.map { it.transactionPlaceholderVisible }
                .distinctUntilChanged()
                .collect { visible ->
                    transactionPlaceholder.root.isVisible = visible
                }
        }
    }

    private fun FragmentHomeBinding.bindIncomeExpenseContainer() {
        launchSafely {
            viewModel.stateFlow.map { it.incomeExpenseVisible }
                .distinctUntilChanged()
                .collect { visible ->
                    incomeExpenseContainer.isVisible = visible
                }
        }
    }

    private fun FragmentHomeBinding.bindExpense() {
        expenseCard.icon.setImageResource(com.yuriikonovalov.common.R.drawable.ic_arrow_down)
        expenseCard.icon.setTintResource(com.yuriikonovalov.common.R.color.red)
        expenseCard.label.setText(com.yuriikonovalov.common.R.string.expense)
        expenseCard.placeholder.setText(R.string.no_expense_for_this_month)
        expenseCard.list.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = expenseAdapter
        }
        launchSafely {
            viewModel.stateFlow.map { it.expenses }
                .distinctUntilChanged()
                .collect(expenseAdapter::submitList)
        }

        launchSafely {
            viewModel.stateFlow.map { it.expensesLoading }
                .distinctUntilChanged()
                .collect { visible ->
                    expenseCard.progressBar.isVisible = visible
                }
        }
        launchSafely {
            viewModel.stateFlow.map { it.expensePlaceholderVisible }
                .distinctUntilChanged()
                .collect { visible ->
                    expenseCard.placeholder.isVisible = visible
                }
        }
    }

    private fun FragmentHomeBinding.bindIncome() {
        incomeCard.icon.setImageResource(com.yuriikonovalov.common.R.drawable.ic_arrow_up)
        incomeCard.icon.setTintResource(com.yuriikonovalov.common.R.color.green)
        incomeCard.label.setText(com.yuriikonovalov.common.R.string.income)
        incomeCard.placeholder.setText(R.string.no_income_for_this_month)
        incomeCard.list.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = incomeAdapter
        }
        launchSafely {
            viewModel.stateFlow.map { it.incomes }
                .distinctUntilChanged()
                .collect(incomeAdapter::submitList)
        }

        launchSafely {
            viewModel.stateFlow.map { it.incomesLoading }
                .distinctUntilChanged()
                .collect { visible ->
                    incomeCard.progressBar.isVisible = visible
                }
        }
        launchSafely {
            viewModel.stateFlow.map { it.incomePlaceholderVisible }
                .distinctUntilChanged()
                .collect { visible ->
                    incomeCard.placeholder.isVisible = visible
                }
        }
    }

    private fun FragmentHomeBinding.bindAvailableBalance() {
        availableBalance.availableBalances.layoutManager = LinearLayoutManager(requireContext())
        availableBalance.availableBalances.adapter = availableBalanceAdapter
        launchSafely {
            viewModel.stateFlow.map { it.availableBalances }
                .distinctUntilChanged()
                .collect {
                    availableBalanceAdapter.submitList(it)
                    availableBalance.root.isVisible = it.isNotEmpty()
                }
        }

        launchSafely {
            viewModel.stateFlow.map { it.availableBalancesLoading }
                .distinctUntilChanged()
                .collect { visible ->
                    availableBalance.progressBar.isVisible = visible
                }
        }
    }

    private fun FragmentHomeBinding.bindToolbar() {
        launchSafely {
            viewModel.stateFlow.map { it.date }
                .collect {
                    date.text = it.toMmmmYyyy()
                }
        }
    }

    private fun FragmentHomeBinding.applyWindowInsets() {
        appBar.doOnApplyWindowInsetsCompat { appBar, windowInsetsCompat, initialPadding ->
            appBar.updatePadding(top = initialPadding.top + windowInsetsCompat.systemBars.top)
        }
    }
}