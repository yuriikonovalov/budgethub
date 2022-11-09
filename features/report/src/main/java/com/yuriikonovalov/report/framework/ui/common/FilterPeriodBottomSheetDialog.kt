package com.yuriikonovalov.report.framework.ui.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.yuriikonovalov.common.application.entities.PeriodFilter
import com.yuriikonovalov.report.databinding.BottomSheetDialogFilterPeriodBinding
import java.time.OffsetDateTime

class FilterPeriodBottomSheetDialog : BottomSheetDialogFragment() {
    private lateinit var binding: BottomSheetDialogFilterPeriodBinding
    private val args: FilterPeriodBottomSheetDialogArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetDialogFilterPeriodBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.bindCloseButton()
        binding.bindWeekButton()
        binding.bindMonthButton()
        binding.bindYearButton()
        binding.bindAllButton()
        binding.bindCustomButton()
        binding.bindPeriod()
    }

    private fun BottomSheetDialogFilterPeriodBinding.bindCloseButton() {
        closeButton.setOnClickListener {
            dismiss()
        }
    }

    private fun BottomSheetDialogFilterPeriodBinding.bindCustomButton() {
        periodCustom.setOnClickListener {
            setFragmentResult(
                PeriodFilter.Custom(
                    OffsetDateTime.now(),
                    OffsetDateTime.now()
                )
            )
            dismiss()
        }
    }

    private fun BottomSheetDialogFilterPeriodBinding.bindAllButton() {
        periodAll.setOnClickListener {
            setFragmentResult(PeriodFilter.All)
            dismiss()
        }
    }

    private fun BottomSheetDialogFilterPeriodBinding.bindYearButton() {
        periodYear.setOnClickListener {
            setFragmentResult(PeriodFilter.Year)
            dismiss()
        }
    }

    private fun BottomSheetDialogFilterPeriodBinding.bindMonthButton() {
        periodMonth.setOnClickListener {
            setFragmentResult(PeriodFilter.Month)
            dismiss()
        }
    }

    private fun BottomSheetDialogFilterPeriodBinding.bindWeekButton() {
        periodWeek.setOnClickListener {
            setFragmentResult(PeriodFilter.Week)
            dismiss()
        }
    }

    private fun BottomSheetDialogFilterPeriodBinding.bindPeriod() {
        when (args.period) {
            is PeriodFilter.Week -> periodWeek.isChecked = true
            is PeriodFilter.Month -> periodMonth.isChecked = true
            is PeriodFilter.Year -> periodYear.isChecked = true
            is PeriodFilter.All -> periodAll.isChecked = true
            is PeriodFilter.Custom -> periodContainer.clearCheck()
        }
    }

    private fun setFragmentResult(period: PeriodFilter) {
        setFragmentResult(
            REQUEST_KEY,
            bundleOf(
                BUNDLE_KEY to period
            )
        )
    }

    companion object {
        const val REQUEST_KEY = "FILTER_PERIOD_REQUEST_KEY"
        const val BUNDLE_KEY = "FILTER_PERIOD_BUNDLE_KEY"
    }
}