package com.yuriikonovalov.report.framework.ui.transactions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import com.google.android.material.tabs.TabLayoutMediator
import com.yuriikonovalov.common.application.entities.PeriodFilter
import com.yuriikonovalov.common.application.entities.Tag
import com.yuriikonovalov.common.application.entities.account.Account
import com.yuriikonovalov.common.application.util.toDateOffsetDateTime
import com.yuriikonovalov.common.framework.common.extentions.*
import com.yuriikonovalov.common.framework.utils.date.DateFormat.toDdMmmYyyy
import com.yuriikonovalov.common.framework.utils.doOnApplyWindowInsetsCompat
import com.yuriikonovalov.common.framework.utils.systemBars
import com.yuriikonovalov.report.R
import com.yuriikonovalov.report.application.entities.TypeFilter
import com.yuriikonovalov.report.databinding.FragmentTransactionsBinding
import com.yuriikonovalov.report.framework.ui.common.DateRangePicker
import com.yuriikonovalov.report.framework.ui.common.FilterAccountBottomSheetDialog
import com.yuriikonovalov.report.framework.ui.common.FilterPeriodBottomSheetDialog
import com.yuriikonovalov.report.framework.ui.transactions.filters.FilterTagBottomSheetDialog
import com.yuriikonovalov.report.framework.ui.transactions.filters.FilterTypeBottomSheetDialog
import com.yuriikonovalov.report.framework.ui.transactions.list.ListFragmentAdapter
import com.yuriikonovalov.report.presentation.transactions.TransactionsEvent
import com.yuriikonovalov.report.presentation.transactions.TransactionsIntent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class TransactionsFragment : Fragment() {
    private lateinit var binding: FragmentTransactionsBinding
    private val args: TransactionsFragmentArgs by navArgs()

    @Inject
    lateinit var viewModelFactory: TransactionsViewModel.Factory
    private val viewModel: TransactionsViewModel by navGraphViewModels(R.id.transactions_fragment) {
        TransactionsViewModel.provideFactory(viewModelFactory, args.period)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setFilterAccountFragmentResultListener()
        setFilterPeriodFragmentResultListener()
        setFilterTagFragmentResultListener()
        setFilterTypeFragmentResultListener()
    }

    private fun setFilterAccountFragmentResultListener() {
        setFragmentResultListener(FilterAccountBottomSheetDialog.REQUEST_KEY) { _, bundle ->
            val accounts = bundle.getParcelableArrayListCompat(
                FilterAccountBottomSheetDialog.BUNDLE_KEY,
                Account::class.java
            ) ?: emptyList()
            viewModel.handleIntent(TransactionsIntent.ChangeAccountFilter(accounts))
        }
    }

    private fun setFilterPeriodFragmentResultListener() {
        setFragmentResultListener(FilterPeriodBottomSheetDialog.REQUEST_KEY) { _, bundle ->
            val newPeriod = bundle.getParcelableCompat(
                FilterPeriodBottomSheetDialog.BUNDLE_KEY,
                PeriodFilter::class.java
            )!!

            if (newPeriod is PeriodFilter.Custom) {
                viewModel.handleIntent(TransactionsIntent.ClickCustomPeriod)
            } else {
                viewModel.handleIntent(TransactionsIntent.ChangePeriodFilter(newPeriod))
            }
        }
    }

    private fun setFilterTypeFragmentResultListener() {
        setFragmentResultListener(FilterTypeBottomSheetDialog.REQUEST_KEY) { _, bundle ->
            val type = bundle.getSerializableCompat(
                FilterTypeBottomSheetDialog.BUNDLE_KEY,
                TypeFilter::class.java
            )!!
            viewModel.handleIntent(TransactionsIntent.ChangeTypeFilter(type))
        }
    }

    private fun setFilterTagFragmentResultListener() {
        setFragmentResultListener(FilterTagBottomSheetDialog.REQUEST_KEY) { _, bundle ->
            val tags = bundle.getParcelableArrayListCompat(
                FilterTagBottomSheetDialog.BUNDLE_KEY,
                Tag::class.java
            ) ?: emptyList()
            viewModel.handleIntent(TransactionsIntent.ChangeTagFilter(tags))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        binding = FragmentTransactionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.applyWindowInsets()
        binding.bindToolbar()
        binding.bindPeriodFilter()
        binding.bindAccountFilter()
        binding.bindTagFilter()
        binding.bindTypeFilter()
        binding.bindListViewPager()
        collectEvents()
    }

    private fun collectEvents() {
        collectEvent(viewModel.eventFlow, viewModel.eventConsumer) { event ->
            when (event) {
                is TransactionsEvent.ClickAccountFilter -> onClickAccountFilterEvent(
                    event.accounts, event.accountFilter
                )
                is TransactionsEvent.ClickCustomPeriod -> onClickCustomPeriodEvent(
                    event.startDate, event.endDate
                )
                is TransactionsEvent.ClickPeriodFilter -> onClickPeriodFilterEvent(event.period)
                is TransactionsEvent.ClickTagFilter -> onClickTagFilterEvent(
                    event.tags, event.tagFilter
                )
                is TransactionsEvent.ClickTypeFilter -> onClickTypeFilterEvent(event.type)
                is TransactionsEvent.OpenTransactionDetails -> onOpenTransactionDetailsEvent(event.id)
                is TransactionsEvent.OpenTransferDetails -> onOpenTransferDetailsEvent(event.id)
            }
        }
    }

    private fun onClickPeriodFilterEvent(period: PeriodFilter) {
        findNavController().navigate(
            TransactionsFragmentDirections.toFilterPeriodBottomSheetDialog(period)
        )
    }

    private fun onClickAccountFilterEvent(accounts: List<Account>, accountFilter: List<Account>) {
        findNavController().navigate(
            TransactionsFragmentDirections.toFilterAccountBottomSheetDialog(
                accounts = accounts.toTypedArray(), selection = accountFilter.toTypedArray()
            )
        )
    }

    private fun onClickCustomPeriodEvent(startPeriod: Long?, endPeriod: Long?) {
        val picker = DateRangePicker(startPeriod, endPeriod)
        picker.setOnPositiveButtonClickListener { range ->
            viewModel.handleIntent(
                TransactionsIntent.ChangePeriodFilter(
                    PeriodFilter.Custom(
                        range.first.toDateOffsetDateTime(), range.second.toDateOffsetDateTime()
                    )
                )
            )
        }
        picker.show(childFragmentManager)
    }

    private fun onClickTagFilterEvent(tags: List<Tag>, tagFilter: List<Tag>) {
        findNavController().navigate(
            TransactionsFragmentDirections.toFilterTagBottomSheetDialog(
                tags = tags.toTypedArray(), selection = tagFilter.toTypedArray()
            )
        )
    }

    private fun onClickTypeFilterEvent(type: TypeFilter) {
        findNavController().navigate(
            TransactionsFragmentDirections.toFilterTypeBottomSheetDialog(type)
        )
    }

    private fun onOpenTransactionDetailsEvent(id: Long) {
        findNavController().navigate(TransactionsFragmentDirections.toTransactionDetailsFragment(id))
    }

    private fun onOpenTransferDetailsEvent(id: Long) {
        findNavController().navigate(TransactionsFragmentDirections.toTransferDetailsFragment(id))
    }

    private fun FragmentTransactionsBinding.bindToolbar() {
        toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun FragmentTransactionsBinding.bindPeriodFilter() {
        filterPeriod.setOnClickListener {
            viewModel.handleIntent(TransactionsIntent.ClickPeriodFilter)
        }
        launchSafely {
            viewModel.stateFlow.map { it.periodFilter }
                .distinctUntilChanged()
                .collect { period ->
                    filterPeriod.text = when (period) {
                        is PeriodFilter.Week -> getString(com.yuriikonovalov.common.R.string.this_week)
                        is PeriodFilter.Month -> getString(com.yuriikonovalov.common.R.string.this_month)
                        is PeriodFilter.Year -> getString(com.yuriikonovalov.common.R.string.this_year)
                        is PeriodFilter.All -> getString(com.yuriikonovalov.common.R.string.all)
                        is PeriodFilter.Custom -> {
                            "${period.startPeriod.toDdMmmYyyy()} - ${period.endPeriod.toDdMmmYyyy()}"
                        }
                    }
                }
        }
    }

    private fun FragmentTransactionsBinding.bindAccountFilter() {
        filterAccount.setOnClickListener {
            viewModel.handleIntent(TransactionsIntent.ClickAccountFilter)
        }
        launchSafely {
            viewModel.stateFlow.map { Pair(it.accounts.size, it.accountFilter) }
                .distinctUntilChanged()
                .collect { pair ->
                    val numberOfAllAccounts = pair.first
                    val selection = pair.second
                    filterAccount.text = when (selection.size) {
                        0 -> getString(com.yuriikonovalov.common.R.string.select_account)
                        1 -> selection.first().name
                        numberOfAllAccounts -> getString(com.yuriikonovalov.common.R.string.all_accounts)
                        else -> "${selection.first().name}+${selection.size - 1}"
                    }
                }
        }
    }

    private fun FragmentTransactionsBinding.bindTagFilter() {
        filterTag.setOnClickListener {
            viewModel.handleIntent(TransactionsIntent.ClickTagFilter)
        }

        launchSafely {
            viewModel.stateFlow.map { Pair(it.tags.size, it.tagFilter) }
                .distinctUntilChanged()
                .collect { pair ->
                    val numberOfAllTags = pair.first
                    val selection = pair.second
                    binding.filterTag.text = when (selection.size) {
                        0 -> getString(com.yuriikonovalov.common.R.string.select_tag)
                        1 -> selection.first().name
                        numberOfAllTags -> getString(com.yuriikonovalov.common.R.string.all_tags)
                        else -> "${selection.first().name}+${selection.size - 1}"
                    }
                }
        }
    }

    private fun FragmentTransactionsBinding.bindTypeFilter() {
        filterType.setOnClickListener {
            viewModel.handleIntent(TransactionsIntent.ClickTypeFilter)
        }
        launchSafely {
            viewModel.stateFlow.map { it.typeFilter }
                .distinctUntilChanged()
                .collect { type ->
                    filterType.text = when (type) {
                        TypeFilter.ALL -> getString(com.yuriikonovalov.common.R.string.all_types)
                        TypeFilter.EXPENSE -> getString(com.yuriikonovalov.common.R.string.expense)
                        TypeFilter.INCOME -> getString(com.yuriikonovalov.common.R.string.income)
                    }
                }
        }
    }

    private fun FragmentTransactionsBinding.bindListViewPager() {
        val listFragmentAdapter =
            ListFragmentAdapter(childFragmentManager, viewLifecycleOwner.lifecycle)

        pager.adapter = listFragmentAdapter
        TabLayoutMediator(tabLayout, pager) { tab, position ->
            when (position) {
                0 -> tab.text = getString(com.yuriikonovalov.common.R.string.transactions)
                1 -> tab.text = getString(com.yuriikonovalov.common.R.string.transfers)
            }
        }.attach()
    }

    private fun FragmentTransactionsBinding.applyWindowInsets() {
        toolbar.doOnApplyWindowInsetsCompat { toolbar, windowInsetsCompat, initialPadding ->
            toolbar.updatePadding(top = initialPadding.top + windowInsetsCompat.systemBars.top)
        }
        pager.doOnApplyWindowInsetsCompat { pager, windowInsetsCompat, initialPadding ->
            pager.updatePadding(bottom = initialPadding.bottom + windowInsetsCompat.systemBars.bottom)
        }
    }
}