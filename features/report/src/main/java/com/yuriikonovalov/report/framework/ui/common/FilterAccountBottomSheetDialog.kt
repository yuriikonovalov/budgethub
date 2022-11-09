package com.yuriikonovalov.report.framework.ui.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isGone
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.yuriikonovalov.common.application.entities.account.Account
import com.yuriikonovalov.report.databinding.BottomSheetDialogFilterAccountBinding
import com.yuriikonovalov.report.presentation.model.AccountItem

class FilterAccountBottomSheetDialog : BottomSheetDialogFragment() {
    private lateinit var binding: BottomSheetDialogFilterAccountBinding
    private val args: FilterAccountBottomSheetDialogArgs by navArgs()

    private val accountFilterAdapter = AccountFilterAdapter { account ->
        val newSelection = args.selection.toList().updateSelection(account)
        setFragmentResult(newSelection)
        dismiss()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetDialogFilterAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.bindCloseButton()
        binding.bindClearFilterButton()
        binding.bindAccounts()
    }

    private fun BottomSheetDialogFilterAccountBinding.bindAccounts() {
        accounts.layoutManager = LinearLayoutManager(requireContext())
        accounts.adapter = accountFilterAdapter
        val accounts = args.accounts.map {
            AccountItem(account = it, checked = args.selection.contains(it))
        }
        accountFilterAdapter.submitList(accounts)
    }

    private fun BottomSheetDialogFilterAccountBinding.bindClearFilterButton() {
        clearFilterButton.isGone = args.selection.isEmpty()
        clearFilterButton.setOnClickListener {
            setFragmentResult(emptyList())
            dismiss()
        }
    }

    private fun BottomSheetDialogFilterAccountBinding.bindCloseButton() {
        closeButton.setOnClickListener {
            dismiss()
        }
    }

    private fun List<Account>.updateSelection(account: Account): List<Account> {
        return if (this.contains(account)) {
            this.minusElement(account)
        } else {
            this.plusElement(account)
        }
    }

    private fun setFragmentResult(accounts: List<Account>) {
        setFragmentResult(
            REQUEST_KEY,
            bundleOf(BUNDLE_KEY to accounts)
        )
    }

    companion object {
        const val REQUEST_KEY = "FILTER_ACCOUNT_REQUEST_KEY"
        const val BUNDLE_KEY = "FILTER_ACCOUNT_BUNDLE_KEY"
    }
}