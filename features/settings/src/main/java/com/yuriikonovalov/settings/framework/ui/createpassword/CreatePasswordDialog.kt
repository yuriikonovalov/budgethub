package com.yuriikonovalov.settings.framework.ui.createpassword

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.yuriikonovalov.common.framework.common.extentions.collectEvent
import com.yuriikonovalov.common.framework.common.extentions.launchSafely
import com.yuriikonovalov.settings.databinding.DialogCreatePasswordBinding
import com.yuriikonovalov.settings.presentation.createpassword.CreatePasswordEvent
import com.yuriikonovalov.settings.presentation.createpassword.CreatePasswordIntent
import com.yuriikonovalov.settings.presentation.createpassword.CreatePasswordState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

@AndroidEntryPoint
class CreatePasswordDialog : DialogFragment() {
    private lateinit var binding: DialogCreatePasswordBinding
    private val viewModel: CreatePasswordViewModel by viewModels()
    var onPositiveButtonClick: ((password: String) -> Unit)? = null

    override fun getTheme(): Int {
        return com.yuriikonovalov.common.R.style.Theme_DialogFragment
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = DialogCreatePasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.bindMessage()
        binding.bindPasswordInput()
        binding.bindNegativeButton()
        binding.bindOkButton()
        binding.bindContinueButton()
        collectEvents()
    }

    private fun collectEvents() {
        collectEvent(viewModel.eventFlow, viewModel.eventConsumer) { event ->
            when (event) {
                is CreatePasswordEvent.ClickPositiveButton -> onClickPositiveButtonEvent(event.password)
                is CreatePasswordEvent.ClearInput -> onClearInputEvent()
                is CreatePasswordEvent.PasswordsNotMatch -> onPasswordsNotMatchEvent()
            }
        }
    }

    private fun onClickPositiveButtonEvent(password: String) {
        onPositiveButtonClick?.invoke(password)
        dismiss()
    }

    private fun onClearInputEvent() {
        binding.password.text = null
    }

    private fun onPasswordsNotMatchEvent() {
        binding.message.text = getString(com.yuriikonovalov.common.R.string.passwords_don_t_match)
    }

    private fun softInputModeToVisible() {
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        binding.password.requestFocus()
    }

    override fun onStart() {
        super.onStart()
        softInputModeToVisible()
    }

    private fun DialogCreatePasswordBinding.bindMessage() {
        launchSafely {
            viewModel.stateFlow.map { it.message }
                .distinctUntilChanged()
                .collect {
                    message.text = when (it) {
                        CreatePasswordState.Message.REPEAT_PASSWORD ->
                            getString(com.yuriikonovalov.common.R.string.repeat_password)
                        CreatePasswordState.Message.PASSWORD_MIN_LENGTH ->
                            getString(com.yuriikonovalov.common.R.string.password_min_length)
                        CreatePasswordState.Message.HAVING_FINISHED_PRESS_CONTINUE ->
                            getString(com.yuriikonovalov.common.R.string.having_finished_press_continue)
                    }
                }
        }
    }

    private fun DialogCreatePasswordBinding.bindPasswordInput() {
        password.doOnTextChanged { text, _, _, _ ->
            text?.toString()?.let { input ->
                viewModel.handleIntent(CreatePasswordIntent.ChangePassword(input))
            }
        }
        launchSafely {
            viewModel.stateFlow.map { it.errorPasswordSymbol }
                .distinctUntilChanged()
                .collect { error ->
                    passwordContainer.error = if (error) {
                        getString(com.yuriikonovalov.common.R.string.password_symbol_error)
                    } else {
                        null
                    }
                }
        }
    }

    private fun DialogCreatePasswordBinding.bindOkButton() {
        okButton.setOnClickListener {
            viewModel.handleIntent(CreatePasswordIntent.ClickOkButton)
        }
        launchSafely {
            viewModel.stateFlow.map { it.okButtonVisible }
                .distinctUntilChanged()
                .collect { visible ->
                    okButton.isVisible = visible
                }
        }
        launchSafely {
            viewModel.stateFlow.map { it.okButtonEnabled }
                .distinctUntilChanged()
                .collect { enabled ->
                    okButton.isEnabled = enabled
                }
        }
    }

    private fun DialogCreatePasswordBinding.bindContinueButton() {
        continueButton.setOnClickListener {
            viewModel.handleIntent(CreatePasswordIntent.ClickContinueButton)
        }
        launchSafely {
            viewModel.stateFlow.map { it.continueButtonVisible }
                .distinctUntilChanged()
                .collect { visible ->
                    continueButton.isVisible = visible
                }
        }
        launchSafely {
            viewModel.stateFlow.map { it.continueButtonEnabled }
                .distinctUntilChanged()
                .collect { enabled ->
                    continueButton.isEnabled = enabled
                }
        }
    }

    private fun DialogCreatePasswordBinding.bindNegativeButton() {
        toolbar.setNavigationOnClickListener {
            dismiss()
        }
    }

    companion object {
        const val TAG = "CreatePasswordDialog"
    }
}