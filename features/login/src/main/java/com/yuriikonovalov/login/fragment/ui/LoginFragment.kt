package com.yuriikonovalov.login.fragment.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.yuriikonovalov.common.NavGraphDirections
import com.yuriikonovalov.common.framework.common.extentions.collectEvent
import com.yuriikonovalov.common.framework.common.extentions.launchSafely
import com.yuriikonovalov.common.framework.utils.doOnApplyWindowInsetsCompat
import com.yuriikonovalov.common.framework.utils.systemBars
import com.yuriikonovalov.login.R
import com.yuriikonovalov.login.databinding.FragmentLoginBinding
import com.yuriikonovalov.login.presentation.LoginEvent
import com.yuriikonovalov.login.presentation.LoginIntent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private val viewModel: LoginViewModel by viewModels()
    private lateinit var biometricPrompt: BiometricPrompt

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        biometricPrompt = createBiometricPrompt()
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.applyWindowInsets()
        binding.bindPasswordInput()
        binding.bindContinueButton()
        binding.bindUseBiometricButton()

        collectEvent(viewModel.eventFlow, viewModel.eventConsumer) { event ->
            when (event) {
                is LoginEvent.LoginSuccessful -> onLoginSuccessfulEvent()
                is LoginEvent.IncorrectPassword -> onIncorrectPasswordEvent()
                is LoginEvent.ClickUseBiometricAuthentication -> onClickUseBiometricAuthenticationEvent()
            }
        }
    }

    private fun onLoginSuccessfulEvent() {
        findNavController().navigate(NavGraphDirections.toHome())
    }

    private fun onIncorrectPasswordEvent() {
        Toast.makeText(requireContext(), getString(R.string.incorrect_password), Toast.LENGTH_SHORT)
            .show()
    }

    private fun onClickUseBiometricAuthenticationEvent() {
        val promptInfo = createPromptInfo()
        biometricPrompt.authenticate(promptInfo)
    }

    private fun FragmentLoginBinding.bindUseBiometricButton() {
        useBiometricButton.setOnClickListener {
            viewModel.handleIntent(LoginIntent.ClickUseBiometricButton)
        }
        launchSafely {
            viewModel.stateFlow.map { it.useBiometricButtonVisible }
                .distinctUntilChanged()
                .collect { visible ->
                    useBiometricButton.isVisible = visible
                }
        }
    }

    private fun FragmentLoginBinding.bindContinueButton() {
        continueButton.setOnClickListener {
            viewModel.handleIntent(LoginIntent.ClickContinueButton)
        }
    }

    private fun FragmentLoginBinding.bindPasswordInput() {
        password.doOnTextChanged { text, _, _, _ ->
            text?.toString()?.let { input ->
                viewModel.handleIntent(LoginIntent.ChangePassword(input))
            }
        }
    }

    private fun createBiometricPrompt(): BiometricPrompt {
        val executor = ContextCompat.getMainExecutor(requireContext())
        val callback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                findNavController().navigate(NavGraphDirections.toHome())
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                if (errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON) {
                    requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
                    binding.password.requestFocus()
                }
            }
        }

        return BiometricPrompt(this, executor, callback)
    }

    private fun createPromptInfo(): BiometricPrompt.PromptInfo {
        return BiometricPrompt.PromptInfo.Builder()
            .setTitle(getString(R.string.biometric_prompt_title))
            .setConfirmationRequired(false)
            .setNegativeButtonText(getString(R.string.biometric_prompt_negative_button))
            .build()
    }

    private fun FragmentLoginBinding.applyWindowInsets() {
        toolbar.doOnApplyWindowInsetsCompat { view, windowInsetsCompat, initialPadding ->
            view.updatePadding(top = initialPadding.top + windowInsetsCompat.systemBars.top)
        }
        root.doOnApplyWindowInsetsCompat { view, windowInsetsCompat, initialPadding ->
            view.updatePadding(bottom = initialPadding.bottom + windowInsetsCompat.systemBars.bottom)
        }
    }
}