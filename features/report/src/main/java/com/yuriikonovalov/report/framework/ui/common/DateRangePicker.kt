package com.yuriikonovalov.report.framework.ui.common

import androidx.core.util.Pair
import androidx.fragment.app.FragmentManager
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener

class DateRangePicker(startRange: Long?, endRange: Long?) {
    private val picker = MaterialDatePicker.Builder.dateRangePicker()
        .setTheme(com.yuriikonovalov.common.R.style.ThemeOverlay_App_MaterialCalendar)
        .setSelection(Pair(startRange, endRange))
        .build()

    fun setOnPositiveButtonClickListener(listener: MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>) {
        picker.addOnPositiveButtonClickListener(listener)
    }

    fun show(fragmentManager: FragmentManager) {
        picker.show(fragmentManager, TAG)
    }

    companion object {
        const val TAG = "DateRangePicker"
    }
}