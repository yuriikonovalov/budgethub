package com.yuriikonovalov.onboarding.framework.ui.security.password

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.updatePadding
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.yuriikonovalov.common.framework.common.extentions.collectEvent
import com.yuriikonovalov.common.framework.common.extentions.launchSafely
import com.yuriikonovalov.common.framework.ui.addeditaccount.AddEditAccountFragment
import com.yuriikonovalov.common.framework.utils.doOnApplyWindowInsetsCompat
import com.yuriikonovalov.common.framework.utils.systemBars
import com.yuriikonovalov.onboarding.R
import com.yuriikonovalov.onboarding.databinding.FragmentPasswordBinding
import com.yuriikonovalov.onboarding.presentation.security.password.PasswordEvent
import com.yuriikonovalov.onboarding.presentation.security.password.PasswordIntent
import com.yuriikonovalov.onboarding.presentation.security.password.PasswordState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

@AndroidEntryPoint
class PasswordFragment : Fragment() {
    private lateinit var binding: FragmentPasswordBinding
    private val viewModel: PasswordViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.applyWindowInsets()
        binding.bindPasswordInput()
        binding.bindPositiveButton()
        binding.bindMessage()
        binding.bindSkipButton()
        collectEvents()
    }

    private fun collectEvents() {
        collectEvent(viewModel.eventFlow, viewModel.eventConsumer) { event ->
            when (event) {
                is PasswordEvent.ClearInput -> onClearInputEvent()
                is PasswordEvent.PasswordsNotMatch -> onPasswordsNotMatchEvent()
                is PasswordEvent.Continue -> onContinueEvent(
                    event.destination
                )
                is PasswordEvent.SavePasswordFailure -> onSavePasswordFailureEvent()
                is PasswordEvent.Skip -> onSkipEvent()
            }
        }
    }

    private fun onClearInputEvent() {
        binding.password.text = null
    }

    private fun onPasswordsNotMatchEvent() {
        binding.message.text = getString(com.yuriikonovalov.common.R.string.passwords_don_t_match)
    }

    private fun onSavePasswordFailureEvent() {
        Toast.makeText(
            requireContext(),
            getString(R.string.save_password_failure),
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun onContinueEvent(destination: PasswordEvent.Destination) {
        when (destination) {
            is PasswordEvent.Destination.BiometricAuthentication -> {
                findNavController().navigate(PasswordFragmentDirections.toBiometricAuthenticationFragment())
            }
            is PasswordEvent.Destination.AddAccount -> {
                findNavController().navigate(
                    PasswordFragmentDirections.toAddEditAccountFragment(
                        AddEditAccountFragment.ARGUMENT_ONBOARDING
                    )
                )
            }
        }
    }

    private fun onSkipEvent() {
        findNavController().navigate(
            PasswordFragmentDirections.toAddEditAccountFragment(
                AddEditAccountFragment.ARGUMENT_ONBOARDING
            )
        )
    }

    private fun FragmentPasswordBinding.bindMessage() {
        launchSafely {
            viewModel.stateFlow.map { it.message }
                .distinctUntilChanged()
                .collect {
                    message.text = when (it) {
                        PasswordState.Message.REPEAT_PASSWORD -> getString(com.yuriikonovalov.common.R.string.repeat_password)
                        PasswordState.Message.PASSWORD_MIN_LENGTH -> getString(com.yuriikonovalov.common.R.string.password_min_length)
                        PasswordState.Message.HAVING_FINISHED_PRESS_CONTINUE -> getString(com.yuriikonovalov.common.R.string.having_finished_press_continue)
                    }
                }
        }
    }

    private fun FragmentPasswordBinding.bindPasswordInput() {
        password.doOnTextChanged { text, _, _, _ ->
            text?.toString()?.let { input ->
                viewModel.handleIntent(PasswordIntent.ChangePassword(input))
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


    private fun FragmentPasswordBinding.bindPositiveButton() {
        positiveButton.setOnClickListener {
            viewModel.handleIntent(PasswordIntent.ClickPositiveButton)
        }
        launchSafely {
            viewModel.stateFlow.map { it.positiveButtonEnabled }
                .distinctUntilChanged()
                .collect { enabled ->
                    positiveButton.isEnabled = enabled
                }
        }
        launchSafely {
            viewModel.stateFlow.map { it.positiveButtonText }
                .distinctUntilChanged()
                .collect { text ->
                    positiveButton.text = when (text) {
                        PasswordState.PositiveButtonText.CONTINUE -> getString(
                            com.yuriikonovalov.common.R.string.continue_label
                        )
                        PasswordState.PositiveButtonText.OK -> getString(
                            com.yuriikonovalov.common.R.string.ok
                        )
                    }
                }
        }
    }

    private fun FragmentPasswordBinding.bindSkipButton() {
        skipButton.setOnClickListener {
            viewModel.handleIntent(PasswordIntent.ClickSkipButton)
        }
    }

    private fun FragmentPasswordBinding.applyWindowInsets() {
        toolbar.doOnApplyWindowInsetsCompat { toolbar, windowInsetsCompat, initialPadding ->
            toolbar.updatePadding(top = initialPadding.top + windowInsetsCompat.systemBars.top)
        }
        root.doOnApplyWindowInsetsCompat { view, windowInsetsCompat, initialPadding ->
            view.updatePadding(bottom = initialPadding.bottom + windowInsetsCompat.systemBars.bottom)
        }
    }
}