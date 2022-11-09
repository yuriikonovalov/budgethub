package com.yuriikonovalov.report.framework.ui.detailedreport

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.DashPathEffect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.model.GradientColor
import com.google.android.material.color.MaterialColors
import com.yuriikonovalov.common.application.entities.Currency
import com.yuriikonovalov.common.application.entities.PeriodFilter
import com.yuriikonovalov.common.application.entities.account.Account
import com.yuriikonovalov.common.application.util.toDateOffsetDateTime
import com.yuriikonovalov.common.framework.common.extentions.*
import com.yuriikonovalov.common.framework.utils.date.DateFormat.toDdMmmYyyy
import com.yuriikonovalov.common.framework.utils.doOnApplyWindowInsetsCompat
import com.yuriikonovalov.common.framework.utils.systemBars
import com.yuriikonovalov.report.R
import com.yuriikonovalov.report.databinding.FragmentDetailedReportBinding
import com.yuriikonovalov.report.databinding.ViewBarCardBinding
import com.yuriikonovalov.report.databinding.ViewPieCardBinding
import com.yuriikonovalov.report.framework.ui.common.*
import com.yuriikonovalov.report.framework.ui.common.DateRangePicker
import com.yuriikonovalov.report.framework.ui.common.FilterAccountBottomSheetDialog
import com.yuriikonovalov.report.framework.ui.common.FilterPeriodBottomSheetDialog
import com.yuriikonovalov.report.presentation.detailedreport.DetailedReportEvent
import com.yuriikonovalov.report.presentation.detailedreport.DetailedReportIntent
import com.yuriikonovalov.report.presentation.detailedreport.valueformatter.BarValueFormatter
import com.yuriikonovalov.report.presentation.detailedreport.valueformatter.PieValueFormatter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@AndroidEntryPoint
class DetailedReportFragment : Fragment() {
    private lateinit var binding: FragmentDetailedReportBinding
    private val args: DetailedReportFragmentArgs by navArgs()

    @Inject
    lateinit var viewModelFactory: DetailedReportViewModel.Factory
    private val viewModel: DetailedReportViewModel by viewModels {
        DetailedReportViewModel.provideFactory(viewModelFactory, args.period)
    }

    private val incomeAggregateCategoryAdapter by lazy { AggregateCategoryFlatByCurrencyAdapter() }
    private val expenseAggregateCategoryAdapter by lazy { AggregateCategoryFlatByCurrencyAdapter() }

    private val colorOnSurfaceVariant by lazy {
        MaterialColors.getColor(
            binding.root,
            com.google.android.material.R.attr.colorOnSurfaceVariant
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setFilterAccountFragmentResultListener()
        setFilterPeriodFragmentResultListener()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        binding = FragmentDetailedReportBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun setFilterAccountFragmentResultListener() {
        setFragmentResultListener(FilterAccountBottomSheetDialog.REQUEST_KEY) { _, bundle ->
            val accounts = bundle.getParcelableArrayListCompat(
                FilterAccountBottomSheetDialog.BUNDLE_KEY,
                Account::class.java
            ) ?: emptyList()
            viewModel.handleIntent(DetailedReportIntent.ChangeAccountFilter(accounts))
        }
    }

    private fun setFilterPeriodFragmentResultListener() {
        setFragmentResultListener(FilterPeriodBottomSheetDialog.REQUEST_KEY) { _, bundle ->
            val newPeriod = bundle.getParcelableCompat(
                FilterPeriodBottomSheetDialog.BUNDLE_KEY,
                PeriodFilter::class.java
            )!!

            if (newPeriod is PeriodFilter.Custom) {
                viewModel.handleIntent(DetailedReportIntent.ClickCustomPeriod)
            } else {
                viewModel.handleIntent(DetailedReportIntent.ChangePeriodFilter(newPeriod))
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.applyWindowInsets()
        binding.bindToolbar()
        binding.bindPeriodFilter()
        binding.bindAccountFilter()
        binding.bindIncomeAndExpenseBarCard()
        binding.bindIncomePieCard()
        binding.bindExpensePieCard()
        binding.bindPlaceholder()
        collectEvents()
    }

    private fun collectEvents() {
        collectEvent(viewModel.eventFlow, viewModel.eventConsumer) { event ->
            when (event) {
                is DetailedReportEvent.ClickCustomPeriod -> onClickCustomPeriodEvent(
                    event.startDate,
                    event.endDate
                )
                is DetailedReportEvent.ClickPeriodFilter -> onClickPeriodFilterEvent(event.period)
                is DetailedReportEvent.ClickAccountFilter -> onClickAccountFilterEvent(
                    event.accounts,
                    event.accountFilter
                )
                is DetailedReportEvent.ClickExpenseCurrencyFilter -> onClickExpenseCurrencyFilterEvent(
                    event.currencies
                )
                is DetailedReportEvent.ClickIncomeCurrencyFilter -> onClickIncomeCurrencyFilterEvent(
                    event.currencies
                )
                is DetailedReportEvent.ClickIncomeAndExpenseCurrencyFilter -> onClickIncomeAndExpenseCurrencyFilterEvent(
                    event.currencies
                )
            }
        }
    }

    private fun onClickIncomeAndExpenseCurrencyFilterEvent(currencies: List<Currency>) {
        binding.incomeAndExpenseBarCard.currencyFilter.popupWindow(currencies) {
            viewModel.handleIntent(DetailedReportIntent.ChangeIncomeAndExpenseCurrencyFilter(it))
        }
    }

    private fun onClickIncomeCurrencyFilterEvent(currencies: List<Currency>) {
        binding.incomePieCard.currencyFilter.popupWindow(currencies) {
            viewModel.handleIntent(DetailedReportIntent.ChangeIncomeCurrencyFilter(it))
        }
    }

    private fun onClickExpenseCurrencyFilterEvent(currencies: List<Currency>) {
        binding.expensePieCard.currencyFilter.popupWindow(currencies) {
            viewModel.handleIntent(DetailedReportIntent.ChangeExpenseCurrencyFilter(it))
        }
    }

    private fun onClickAccountFilterEvent(accounts: List<Account>, accountFilter: List<Account>) {
        findNavController().navigate(
            DetailedReportFragmentDirections.toFilterAccountBottomSheetDialog(
                accounts = accounts.toTypedArray(), selection = accountFilter.toTypedArray()
            )
        )
    }

    private fun onClickPeriodFilterEvent(period: PeriodFilter) {
        findNavController().navigate(
            DetailedReportFragmentDirections.toFilterPeriodBottomSheetDialog(period)
        )
    }

    private fun onClickCustomPeriodEvent(startPeriod: Long?, endPeriod: Long?) {
        val picker = DateRangePicker(startPeriod, endPeriod)
        picker.setOnPositiveButtonClickListener { range ->
            viewModel.handleIntent(
                DetailedReportIntent.ChangePeriodFilter(
                    PeriodFilter.Custom(
                        range.first.toDateOffsetDateTime(),
                        range.second.toDateOffsetDateTime()
                    )
                )
            )
        }
        picker.show(childFragmentManager)
    }

    private fun FragmentDetailedReportBinding.bindIncomeAndExpenseBarCard() {
        setupBarChart()
        incomeAndExpenseBarCard.bindCurrencyFilter()
        launchSafely {
            viewModel.stateFlow.map { it.incomeAndExpenseBarEntries }
                .distinctUntilChanged()
                .collect { entries ->
                    val gradients = listOf(
                        GradientColor(Color.TRANSPARENT, Color.GREEN),
                        GradientColor(Color.TRANSPARENT, Color.RED),
                    )
                    val dataSet = BarDataSet(entries, "").apply {
                        gradientColors = gradients
                        valueTextSize = 11f
                        valueFormatter = BarValueFormatter()
                        valueTextColor = colorOnSurfaceVariant
                    }

                    incomeAndExpenseBarCard.barChart.apply {
                        data = null
                        data = BarData(dataSet)
                        barData.isHighlightEnabled = false
                        barData.barWidth = 0.5f
                        invalidate()
                    }
                }
        }
        launchSafely {
            viewModel.stateFlow.map { it.incomeAndExpenseBarCardVisible }
                .distinctUntilChanged()
                .collect { visible ->
                    incomeAndExpenseBarCard.root.isVisible = visible
                }
        }
    }

    private fun ViewBarCardBinding.bindCurrencyFilter() {
        currencyFilter.setOnClickListener {
            viewModel.handleIntent(DetailedReportIntent.ClickIncomeAndExpenseCurrencyFilter)
        }
        launchSafely {
            viewModel.stateFlow.map { it.incomeAndExpenseCurrencyFilter }
                .filterNotNull()
                .distinctUntilChanged()
                .collect {
                    currencyFilter.text = it.code
                }
        }
    }

    private fun FragmentDetailedReportBinding.bindIncomePieCard() {
        incomePieCard.title.text = getString(com.yuriikonovalov.common.R.string.income)
        incomePieCard.pieChart.applyReportStyle()
        incomePieCard.bindIncomeCurrencyFilter()
        binding.incomePieCard.list.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = incomeAggregateCategoryAdapter
        }
        launchSafely {
            viewModel.stateFlow.map { it.incomeAggregateCategoriesFiltered }
                .distinctUntilChanged()
                .collect(incomeAggregateCategoryAdapter::submitList)
        }

        launchSafely {
            viewModel.stateFlow.map { it.incomePieEntries }
                .distinctUntilChanged()
                .collect { entries ->
                    incomePieCard.pieChart.apply {
                        data = entries.toPieData()
                        highlightValues(null)
                        invalidate()
                    }
                }
        }
        launchSafely {
            viewModel.stateFlow.map { it.incomePieCardVisible }
                .distinctUntilChanged()
                .collect { visible ->
                    incomePieCard.root.isVisible = visible
                }
        }
    }

    private fun ViewPieCardBinding.bindIncomeCurrencyFilter() {
        currencyFilter.setOnClickListener {
            viewModel.handleIntent(DetailedReportIntent.ClickIncomeCurrencyFilter)
        }
        launchSafely {
            viewModel.stateFlow.map { it.incomeCurrencyFilter }
                .filterNotNull()
                .distinctUntilChanged()
                .collect {
                    currencyFilter.text = it.code
                }
        }
    }

    private fun FragmentDetailedReportBinding.bindExpensePieCard() {
        expensePieCard.title.text = getString(com.yuriikonovalov.common.R.string.expense)
        expensePieCard.pieChart.applyReportStyle()
        expensePieCard.bindExpenseCurrencyFilter()
        expensePieCard.list.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = expenseAggregateCategoryAdapter
        }

        launchSafely {
            viewModel.stateFlow.map { it.expenseAggregateCategoriesFiltered }
                .distinctUntilChanged()
                .collect(expenseAggregateCategoryAdapter::submitList)
        }

        launchSafely {
            viewModel.stateFlow.map { it.expensePieEntries }
                .distinctUntilChanged()
                .collect { entries ->
                    expensePieCard.pieChart.apply {
                        data = entries.toPieData()
                        highlightValues(null)
                        invalidate()
                    }
                }
        }

        launchSafely {
            viewModel.stateFlow.map { it.expensePieCardVisible }
                .distinctUntilChanged()
                .collect { visible ->
                    expensePieCard.root.isVisible = visible
                }
        }
    }

    private fun ViewPieCardBinding.bindExpenseCurrencyFilter() {
        currencyFilter.setOnClickListener {
            viewModel.handleIntent(DetailedReportIntent.ClickExpenseCurrencyFilter)
        }
        launchSafely {
            viewModel.stateFlow.map { it.expenseCurrencyFilter }
                .filterNotNull()
                .distinctUntilChanged()
                .collect {
                    currencyFilter.text = it.code
                }
        }
    }

    private fun FragmentDetailedReportBinding.bindPlaceholder() {
        emptyPlaceholder.message.text =
            getString(com.yuriikonovalov.common.R.string.there_s_no_data_for_given_filters)
        launchSafely {
            viewModel.stateFlow.map { it.placeholderVisible }
                .distinctUntilChanged()
                .collect { visible ->
                    emptyPlaceholder.root.isVisible = visible
                }
        }
    }

    private fun FragmentDetailedReportBinding.bindToolbar() {
        toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun FragmentDetailedReportBinding.bindPeriodFilter() {
        filterPeriod.setOnClickListener {
            viewModel.handleIntent(DetailedReportIntent.ClickPeriodFilter)
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

    private fun FragmentDetailedReportBinding.bindAccountFilter() {
        filterAccount.setOnClickListener {
            viewModel.handleIntent(DetailedReportIntent.ClickAccountFilter)
        }
        launchSafely {
            viewModel.stateFlow.map { Pair(it.accounts.size, it.accountFilter) }
                .distinctUntilChanged()
                .collect { pair ->
                    filterAccount.text = when (pair.second.size) {
                        0 -> getString(com.yuriikonovalov.common.R.string.select_account)
                        1 -> pair.second.first().name
                        pair.first -> getString(com.yuriikonovalov.common.R.string.all_accounts)
                        else -> "${pair.second.first().name}+${pair.second.size - 1}"
                    }
                }
        }
    }

    private fun FragmentDetailedReportBinding.setupBarChart() {
        with(incomeAndExpenseBarCard.barChart) {
            description.isEnabled = false
            setPinchZoom(false)
            setDrawBarShadow(false)
            setDrawValueAboveBar(true)
            setDrawGridBackground(false)
            isDoubleTapToZoomEnabled = false
            setScaleEnabled(false)

            legend.isEnabled = false

            axisRight.isEnabled = false
            axisLeft.axisMinimum = 0f
            axisLeft.setDrawAxisLine(false)
            axisLeft.setGridDashedLine(DashPathEffect(floatArrayOf(20f, 20f), 20f))
            axisLeft.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
            axisLeft.textColor = colorOnSurfaceVariant

            xAxis.setDrawGridLines(false)
            xAxis.setDrawAxisLine(false)
            xAxis.setDrawLabels(false)
        }
    }

    private fun FragmentDetailedReportBinding.applyWindowInsets() {
        appBar.doOnApplyWindowInsetsCompat { appBar, windowInsetsCompat, initialPadding ->
            appBar.updatePadding(top = initialPadding.top + windowInsetsCompat.systemBars.top)
        }
        content.doOnApplyWindowInsetsCompat { content, windowInsetsCompat, initialPadding ->
            content.updatePadding(bottom = initialPadding.bottom + windowInsetsCompat.systemBars.bottom)
        }
    }

    private fun PieChart.applyReportStyle() {
        val colorSurfaceVariant = MaterialColors.getColor(
            binding.root,
            com.google.android.material.R.attr.colorSurfaceVariant
        )
        setExtraOffsets(25f, 25f, 25f, 25f)
        description.isEnabled = false
        legend.isEnabled = false
        setDrawEntryLabels(true)
        isHighlightPerTapEnabled = false
        isRotationEnabled = true
        isDrawHoleEnabled = true
        holeRadius = 40f
        setHoleColor(colorSurfaceVariant)
        transparentCircleRadius = 45f
        setTransparentCircleColor(colorSurfaceVariant)
        setTransparentCircleAlpha(70)
        animateY(1400, Easing.EaseInOutQuad)
    }

    private fun List<PieEntry>.toPieData(): PieData {
        val dataSet = PieDataSet(this, "").apply {
            colors = this@toPieData.map { entry -> entry.data as Int }
            sliceSpace = 3f
            valueLinePart1OffsetPercentage = 90f
            valueLinePart1Length = 0.5f
            valueLinePart2Length = 0.5f
            yValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE
            isUsingSliceColorAsValueLineColor = true
        }
        return PieData(dataSet).apply {
            setValueTextSize(11f)
            setValueFormatter(PieValueFormatter())
            setValueTextColor(colorOnSurfaceVariant)
        }
    }

    @SuppressLint("InflateParams")
    private fun View.popupWindow(
        currencies: List<Currency>,
        onItemSelected: (item: Currency) -> Unit,
    ): PopupWindow {
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.popup_window_currency, null)
        val recyclerView = view.findViewById<RecyclerView>(R.id.list)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val popupWindow = PopupWindow(
            view,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        val adapter = CurrencyPopupWindowAdapter {
            popupWindow.dismiss()
            onItemSelected(it)
        }
        adapter.submitList(currencies)
        recyclerView.adapter = adapter
        popupWindow.apply {
            isOutsideTouchable = true
            isFocusable = true
            elevation = 16.dpToPx.toFloat()
            showAsDropDown(this@popupWindow)
        }
        return popupWindow
    }
}