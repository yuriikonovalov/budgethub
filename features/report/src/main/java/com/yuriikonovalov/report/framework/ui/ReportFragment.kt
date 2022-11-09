package com.yuriikonovalov.report.framework.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.util.Pair
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.datepicker.MaterialDatePicker
import com.yuriikonovalov.common.application.entities.PeriodFilter
import com.yuriikonovalov.common.application.util.toDateOffsetDateTime
import com.yuriikonovalov.common.framework.common.extentions.collectEvent
import com.yuriikonovalov.common.framework.common.extentions.launchSafely
import com.yuriikonovalov.common.framework.common.extentions.setTintResource
import com.yuriikonovalov.common.framework.ui.common.ExpenseIncomeAdapter
import com.yuriikonovalov.common.framework.utils.date.DateFormat.toDdMmmYyyy
import com.yuriikonovalov.common.framework.utils.doOnApplyWindowInsetsCompat
import com.yuriikonovalov.common.framework.utils.systemBars
import com.yuriikonovalov.report.databinding.FragmentReportBinding
import com.yuriikonovalov.report.presentation.ReportEvent
import com.yuriikonovalov.report.presentation.ReportIntent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

@AndroidEntryPoint
class ReportFragment : Fragment() {
    private lateinit var binding: FragmentReportBinding
    private val viewModel: ReportViewModel by viewModels()
    private val categoryWithNumberOfTransactionsAdapter by lazy { CategoryWithNumberOfTransactionsAdapter() }
    private val incomeAdapter by lazy { ExpenseIncomeAdapter() }
    private val expenseAdapter by lazy { ExpenseIncomeAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentReportBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.applyWindowInsets()
        binding.bindIncome()
        binding.bindExpense()
        binding.bindPeriod()
        binding.bindCustomPeriod()
        binding.bindCategories()
        binding.bindDetailsButton()
        binding.bindSeeAllButton()
        binding.bindPlaceholder()
        collectEvents()
    }

    private fun collectEvents() {
        collectEvent(viewModel.eventFlow, viewModel.eventConsumer) { event ->
            when (event) {
                is ReportEvent.ClickReportButton -> onClickReportButtonEvent(event.periodFilter)
                is ReportEvent.ClickSeeAllButton -> onClickSeeAllButtonEvent(event.periodFilter)
                is ReportEvent.ClickCustomPeriod -> onClickCustomPeriodEvent(
                    event.startDate, event.endDate
                )
                is ReportEvent.CheckInitialPeriod -> onCheckInitialPeriodEvent(event.periodFilter)
            }
        }
    }

    private fun onCheckInitialPeriodEvent(periodFilter: PeriodFilter) {
        when (periodFilter) {
            is PeriodFilter.Custom -> {
                binding.periodCustom.isCheckable = true
                binding.periodCustom.isChecked = true
            }
            is PeriodFilter.Week -> binding.periodWeek.isChecked = true
            is PeriodFilter.Month -> binding.periodMonth.isChecked = true
            is PeriodFilter.Year -> binding.periodYear.isChecked = true
            is PeriodFilter.All -> binding.periodAll.isChecked = true
        }
    }

    private fun onClickCustomPeriodEvent(startPeriod: Long?, endPeriod: Long?) {
        val picker = MaterialDatePicker.Builder.dateRangePicker()
            .setTheme(com.yuriikonovalov.common.R.style.ThemeOverlay_App_MaterialCalendar)
            .setSelection(Pair(startPeriod, endPeriod)).build()

        picker.addOnPositiveButtonClickListener { range ->
            viewModel.handleIntent(
                ReportIntent.ChangePeriod(
                    PeriodFilter.Custom(
                        range.first.toDateOffsetDateTime(), range.second.toDateOffsetDateTime()
                    )
                )
            )
            binding.periodCustom.isCheckable = true
            binding.periodCustom.isChecked = true
        }
        picker.show(childFragmentManager, "DateRangePicker")
    }


    private fun onClickReportButtonEvent(periodFilter: PeriodFilter) {
        findNavController().navigate(ReportFragmentDirections.toDetailedReportFragment(periodFilter))
    }

    private fun onClickSeeAllButtonEvent(periodFilter: PeriodFilter) {
        findNavController().navigate(ReportFragmentDirections.toTransactionsFragment(periodFilter))
    }

    private fun FragmentReportBinding.bindPlaceholder() {
        launchSafely {
            viewModel.stateFlow.map { it.placeholderVisible }
                .distinctUntilChanged()
                .collect { visible ->
                    categories.isGone = visible
                    categoriesLabel.isGone = visible
                    detailsButton.isGone = visible
                    seeAllButton.isGone = visible
                    incomeExpenseContainer.isGone = visible
                    emptyPlaceholder.root.isVisible = visible
                }
        }
    }

    private fun FragmentReportBinding.bindCustomPeriod() {
        periodCustom.isCheckable = false
        periodCustom.setOnClickListener {
            viewModel.handleIntent(ReportIntent.ClickCustomPeriod)
        }
        launchSafely {
            viewModel.stateFlow.map { it.customPeriod }
                .distinctUntilChanged()
                .collect { period ->
                    periodCustom.text = if (period != null) {
                        "${period.first.toDdMmmYyyy()} - ${period.second.toDdMmmYyyy()}"
                    } else {
                        getString(com.yuriikonovalov.common.R.string.custom_range)
                    }
                }
        }
    }

    private fun FragmentReportBinding.bindPeriod() {
        periodGroup.setOnCheckedStateChangeListener { _, checkedId ->
            when (checkedId.first()) {
                periodWeek.id -> {
                    viewModel.handleIntent(ReportIntent.ChangePeriod(PeriodFilter.Week))
                    periodCustom.isCheckable = false
                }
                periodMonth.id -> {
                    viewModel.handleIntent(ReportIntent.ChangePeriod(PeriodFilter.Month))
                    periodCustom.isCheckable = false
                }
                periodYear.id -> {
                    viewModel.handleIntent(ReportIntent.ChangePeriod(PeriodFilter.Year))
                    periodCustom.isCheckable = false
                }
                periodAll.id -> {
                    viewModel.handleIntent(ReportIntent.ChangePeriod(PeriodFilter.All))
                    periodCustom.isCheckable = false
                }
            }
        }
    }

    private fun FragmentReportBinding.bindDetailsButton() {
        detailsButton.setOnClickListener {
            viewModel.handleIntent(ReportIntent.ClickReportButton)
        }
    }

    private fun FragmentReportBinding.bindSeeAllButton() {
        seeAllButton.setOnClickListener {
            viewModel.handleIntent(ReportIntent.ClickSeeAllButton)
        }
    }

    private fun FragmentReportBinding.bindCategories() {
        categories.layoutManager = LinearLayoutManager(requireContext())
        categories.adapter = categoryWithNumberOfTransactionsAdapter
        launchSafely {
            viewModel.stateFlow.map { it.categories }
                .distinctUntilChanged()
                .collect(categoryWithNumberOfTransactionsAdapter::submitList)
        }
    }

    private fun FragmentReportBinding.bindExpense() {
        expenseCard.icon.setImageResource(com.yuriikonovalov.common.R.drawable.ic_arrow_down)
        expenseCard.icon.setTintResource(com.yuriikonovalov.common.R.color.red)
        expenseCard.label.setText(com.yuriikonovalov.common.R.string.expense)
        expenseCard.placeholder.setText(com.yuriikonovalov.common.R.string.no_expense_for_this_period)
        expenseCard.list.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = expenseAdapter
        }
        launchSafely {
            viewModel.stateFlow.map { it.expenses }
                .distinctUntilChanged()
                .collect(expenseAdapter::submitList)
        }

//        launchSafely {
//            viewModel.stateFlow.map { it.expensesLoading }
//                .distinctUntilChanged()
//                .collect { visible ->
//                    expenseCard.progressBar.isVisible = visible
//                }
//        }
        launchSafely {
            viewModel.stateFlow.map { it.expensePlaceholderVisible }
                .distinctUntilChanged()
                .collect { visible ->
                    expenseCard.placeholder.isVisible = visible
                }
        }
    }

    private fun FragmentReportBinding.bindIncome() {
        incomeCard.icon.setImageResource(com.yuriikonovalov.common.R.drawable.ic_arrow_up)
        incomeCard.icon.setTintResource(com.yuriikonovalov.common.R.color.green)
        incomeCard.label.setText(com.yuriikonovalov.common.R.string.income)
        incomeCard.placeholder.setText(com.yuriikonovalov.common.R.string.no_income_for_this_period)
        incomeCard.list.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = incomeAdapter
        }
        launchSafely {
            viewModel.stateFlow.map { it.incomes }
                .distinctUntilChanged()
                .collect(incomeAdapter::submitList)
        }

//        launchSafely {
//            viewModel.stateFlow.map { it.incomesLoading }
//                .distinctUntilChanged()
//                .collect { visible ->
//                    incomeCard.progressBar.isVisible = visible
//                }
//        }
        launchSafely {
            viewModel.stateFlow.map { it.incomePlaceholderVisible }
                .distinctUntilChanged()
                .collect { visible ->
                    incomeCard.placeholder.isVisible = visible
                }
        }
    }

    private fun FragmentReportBinding.applyWindowInsets() {
        appBar.doOnApplyWindowInsetsCompat { appBar, windowInsetsCompat, initialPadding ->
            appBar.updatePadding(top = initialPadding.top + windowInsetsCompat.systemBars.top)
        }
    }
}