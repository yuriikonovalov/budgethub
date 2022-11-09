package com.yuriikonovalov.report.framework.ui.transactions.filters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.yuriikonovalov.report.application.entities.TypeFilter
import com.yuriikonovalov.report.databinding.BottomSheetDialogFilterTypeBinding

class FilterTypeBottomSheetDialog : BottomSheetDialogFragment() {
    private lateinit var binding: BottomSheetDialogFilterTypeBinding
    private val args: FilterTypeBottomSheetDialogArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetDialogFilterTypeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.bindCloseButton()
        binding.bindTypeButtons()
        binding.bindType()
    }

    private fun BottomSheetDialogFilterTypeBinding.bindTypeButtons() {
        typeAll.setOnClickListener {
            setFragmentResult(TypeFilter.ALL)
            dismiss()
        }

        typeExpense.setOnClickListener {
            setFragmentResult(TypeFilter.EXPENSE)
            dismiss()
        }

        typeIncome.setOnClickListener {
            setFragmentResult(TypeFilter.INCOME)
            dismiss()
        }
    }

    private fun BottomSheetDialogFilterTypeBinding.bindType() {
        when (args.type) {
            TypeFilter.ALL -> typeAll.isChecked = true
            TypeFilter.EXPENSE -> typeExpense.isChecked = true
            TypeFilter.INCOME -> typeIncome.isChecked = true
        }
    }

    private fun BottomSheetDialogFilterTypeBinding.bindCloseButton() {
        closeButton.setOnClickListener {
            dismiss()
        }
    }

    private fun setFragmentResult(type: TypeFilter) {
        setFragmentResult(
            REQUEST_KEY,
            bundleOf(
                BUNDLE_KEY to type
            )
        )
    }

    companion object {
        const val REQUEST_KEY = "FILTER_TYPE_REQUEST_KEY"
        const val BUNDLE_KEY = "FILTER_TYPE_BUNDLE_KEY"
    }
}