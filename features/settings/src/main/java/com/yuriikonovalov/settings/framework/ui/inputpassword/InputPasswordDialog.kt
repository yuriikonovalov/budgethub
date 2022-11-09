package com.yuriikonovalov.settings.framework.ui.inputpassword

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.yuriikonovalov.common.framework.common.extentions.collectEvent
import com.yuriikonovalov.settings.databinding.DialogInputPasswordBinding
import com.yuriikonovalov.settings.presentation.inputpassword.InputPasswordEvent
import com.yuriikonovalov.settings.presentation.inputpassword.InputPasswordIntent
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InputPasswordDialog : DialogFragment() {
    private lateinit var binding: DialogInputPasswordBinding
    private val viewModel: InputPasswordViewModel by viewModels()
    var onCorrectPassword: (() -> Unit)? = null

    override fun getTheme(): Int {
        return com.yuriikonovalov.common.R.style.Theme_DialogFragment
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = DialogInputPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.bindPositiveButton()
        binding.bindNegativeButton()

        collectEvent(viewModel.eventFlow, viewModel.eventConsumer) { event ->
            when (event) {
                is InputPasswordEvent.CorrectPassword -> onCorrectPasswordEvent()
                is InputPasswordEvent.IncorrectPasswordToast -> onIncorrectPasswordToastEvent()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        softInputModeToVisible()
    }

    private fun onCorrectPasswordEvent() {
        onCorrectPassword?.invoke()
        dismiss()
    }

    private fun onIncorrectPasswordToastEvent() {
        Toast.makeText(
            requireContext(),
            getString(com.yuriikonovalov.common.R.string.incorrect_password),
            Toast.LENGTH_SHORT
        )
            .show()
    }

    private fun DialogInputPasswordBinding.bindPositiveButton() {
        positiveButton.setOnClickListener {
            val password = binding.input.text?.toString() ?: ""
            viewModel.handleIntent(InputPasswordIntent.CheckPassword(password = password))
        }
    }

    private fun DialogInputPasswordBinding.bindNegativeButton() {
        toolbar.setNavigationOnClickListener {
            dismiss()
        }
    }

    private fun softInputModeToVisible() {
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        binding.input.requestFocus()
    }

    companion object {
        const val TAG = "InputPasswordDialog"
    }
}