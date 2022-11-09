package com.yuriikonovalov.common.framework.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.yuriikonovalov.common.databinding.DialogColorPickerBinding

class ColorPickerDialog : DialogFragment() {
    private lateinit var binding: DialogColorPickerBinding
    private var listener: OnPositiveButtonClickListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = materialAlertDialogBuilder()
        binding.bindNegativeButton()
        binding.bindPositiveButton()
        return builder.create()
    }

    private fun materialAlertDialogBuilder(): MaterialAlertDialogBuilder {
        binding = DialogColorPickerBinding.inflate(layoutInflater)
        return MaterialAlertDialogBuilder(requireContext())
            .setView(binding.root)
    }

    private fun DialogColorPickerBinding.bindNegativeButton() {
        negativeButton.setOnClickListener {
            dismiss()
        }
    }

    private fun DialogColorPickerBinding.bindPositiveButton() {
        positiveButton.setOnClickListener {
            listener?.onPositiveButtonClick(picker.selectedColor)
            dismiss()
        }
    }

    fun setOnPositiveButtonClickListener(listener: OnPositiveButtonClickListener): ColorPickerDialog {
        this.listener = listener
        return this
    }

    fun interface OnPositiveButtonClickListener {
        fun onPositiveButtonClick(color: Int)
    }

    companion object {
        const val TAG = "AppColorPickerDialog"

        fun show(
            fragmentManager: FragmentManager,
            onPositiveButtonClickListener: OnPositiveButtonClickListener
        ) {
            val dialog = ColorPickerDialog()
            dialog.setOnPositiveButtonClickListener(onPositiveButtonClickListener)
            dialog.show(fragmentManager, TAG)
        }
    }
}