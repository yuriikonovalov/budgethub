package com.yuriikonovalov.common.framework.ui.currency

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.updatePadding
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.yuriikonovalov.common.application.entities.Currency
import com.yuriikonovalov.common.databinding.BottomSheetDialogCurrencyBinding
import com.yuriikonovalov.common.framework.common.extentions.launchSafely
import com.yuriikonovalov.common.framework.utils.doOnApplyWindowInsetsCompat
import com.yuriikonovalov.common.framework.utils.ime
import com.yuriikonovalov.common.presentation.currency.CurrencyBottomSheetIntent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

@AndroidEntryPoint
class CurrencyBottomSheetDialog : BottomSheetDialogFragment() {
    private lateinit var binding: BottomSheetDialogCurrencyBinding
    private val viewModel: CurrencyBottomSheetViewModel by viewModels()
    private var onCurrencyClick: ((currency: Currency) -> Unit)? = null
    private val adapter by lazy {
        CurrencyBottomSheetAdapter { currency ->
            onCurrencyClick?.invoke(currency)
            dismiss()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = BottomSheetDialogCurrencyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Set STATE_EXPANDED
        (dialog as BottomSheetDialog).behavior.state = BottomSheetBehavior.STATE_EXPANDED
        applyWindowInsets()
        binding.bindSearch()
        binding.bindCurrencies()
    }

    private fun BottomSheetDialogCurrencyBinding.bindSearch() {
        searchInput.doOnTextChanged { text, _, _, _ ->
            text?.toString()?.let {
                viewModel.handleIntent(CurrencyBottomSheetIntent.Search(it))
            }
        }
    }

    private fun BottomSheetDialogCurrencyBinding.bindCurrencies() {
        currencies.layoutManager = LinearLayoutManager(requireContext())
        currencies.adapter = adapter
        launchSafely {
            viewModel.stateFlow.map { it.currencies }
                .distinctUntilChanged()
                .collect(adapter::submitList)
        }
    }

    private fun applyWindowInsets() {
        dialog?.window?.decorView?.doOnApplyWindowInsetsCompat { view, windowInsetsCompat, _ ->
            view.updatePadding(bottom = windowInsetsCompat.ime.bottom)
        }
    }

    companion object {
        const val TAG = "CurrencyBottomSheetDialog"

        fun show(
            fragmentManager: FragmentManager,
            onCurrencyClick: (currency: Currency) -> Unit,
        ) {
            val bottomSheet = CurrencyBottomSheetDialog()
            bottomSheet.onCurrencyClick = onCurrencyClick
            bottomSheet.show(fragmentManager, TAG)
        }
    }
}