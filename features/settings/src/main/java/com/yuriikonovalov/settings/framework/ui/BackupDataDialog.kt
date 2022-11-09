package com.yuriikonovalov.settings.framework.ui

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.yuriikonovalov.settings.databinding.DialogBackupDataBinding

class BackupDataDialog : DialogFragment() {
    private lateinit var binding: DialogBackupDataBinding
    var onSaveClick: (() -> Unit)? = null
    var onShareClick: (() -> Unit)? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogBackupDataBinding.inflate(layoutInflater)
        binding.bindSaveButton()
        binding.bindShareButton()
        binding.bindNegativeButton()
        return MaterialAlertDialogBuilder(requireContext())
            .setView(binding.root)
            .create()
    }

    private fun DialogBackupDataBinding.bindShareButton() {
        shareButton.setOnClickListener {
            onShareClick?.invoke()
            dismiss()
        }
    }

    private fun DialogBackupDataBinding.bindSaveButton() {
        saveButton.setOnClickListener {
            onSaveClick?.invoke()
            dismiss()
        }
    }

    private fun DialogBackupDataBinding.bindNegativeButton() {
        negativeButton.setOnClickListener {
            dismiss()
        }
    }

    companion object {
        const val TAG = "BackupDataDialog"
    }
}