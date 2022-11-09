package com.yuriikonovalov.accounts.framework.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.yuriikonovalov.accounts.databinding.BottomSheetDialogMenuBinding

class MenuBottomSheetDialog(private val accountId: Long) : BottomSheetDialogFragment() {
    private lateinit var binding: BottomSheetDialogMenuBinding
    private var listener: Listener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetDialogMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.deleteButton.setOnClickListener {
            listener?.onDeleteMenuItemClick(accountId)
            dismiss()
        }
        binding.editButton.setOnClickListener {
            listener?.onEditMenuItemClick(accountId)
            dismiss()
        }
    }

    internal interface Listener {
        fun onEditMenuItemClick(id: Long)
        fun onDeleteMenuItemClick(id: Long)
    }

    internal fun setMenuListener(listener: Listener) {
        this.listener = listener
    }

    companion object {
        const val TAG = "MenuBottomSheetDialog"
    }

}