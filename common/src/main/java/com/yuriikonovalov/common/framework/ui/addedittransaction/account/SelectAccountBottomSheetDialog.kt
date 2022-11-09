package com.yuriikonovalov.common.framework.ui.addedittransaction.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.yuriikonovalov.common.application.entities.account.Account
import com.yuriikonovalov.common.databinding.BottomSheetDialogSelectAccountBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SelectAccountBottomSheetDialog(
    private val list: List<Account>
) : BottomSheetDialogFragment() {
    private lateinit var binding: BottomSheetDialogSelectAccountBinding
    private var listener: Listener? = null
    private val accountAdapter by lazy {
        AccountCardAdapter {
            listener?.onDialogPositiveClick(it)
            dialog?.dismiss()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetDialogSelectAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.bindCloseButton()
        binding.bindAccounts()
    }

    private fun BottomSheetDialogSelectAccountBinding.bindAccounts() {
        accounts.layoutManager = LinearLayoutManager(requireContext())
        accounts.adapter = accountAdapter
        accountAdapter.submitList(list)
    }

    private fun BottomSheetDialogSelectAccountBinding.bindCloseButton() {
        closeButton.setOnClickListener {
            dialog?.dismiss()
        }
    }

    fun setOnDialogPositiveClickListener(listener: Listener) {
        this.listener = listener
    }

    fun interface Listener {
        fun onDialogPositiveClick(account: Account)
    }

    companion object {
        const val TAG = "SelectAccountBottomSheetDialog"
    }
}