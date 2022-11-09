package com.yuriikonovalov.onboarding.framework.ui.security.biometricauthentication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.yuriikonovalov.common.framework.common.extentions.collectEvent
import com.yuriikonovalov.common.framework.ui.addeditaccount.AddEditAccountFragment
import com.yuriikonovalov.common.framework.utils.doOnApplyWindowInsetsCompat
import com.yuriikonovalov.common.framework.utils.systemBars
import com.yuriikonovalov.onboarding.databinding.FragmentBiometricAuthenticationBinding
import com.yuriikonovalov.onboarding.presentation.security.biometricauthentication.BiometricAuthenticationEvent
import com.yuriikonovalov.onboarding.presentation.security.biometricauthentication.BiometricAuthenticationIntent
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BiometricAuthenticationFragment : Fragment() {
    private lateinit var binding: FragmentBiometricAuthenticationBinding
    private val viewModel: BiometricAuthenticationViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBiometricAuthenticationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.applyWindowInsets()
        binding.bindSkipButton()
        binding.bindEnableButton()
        collectEvent(viewModel.eventFlow, viewModel.eventConsumer) { event ->
            when (event) {
                is BiometricAuthenticationEvent.ClickSkipButton -> navigateToAddAccountFragment()
                is BiometricAuthenticationEvent.EnableBiometricAuthentication -> navigateToAddAccountFragment()
            }
        }
    }

    private fun navigateToAddAccountFragment() {
        findNavController().navigate(
            BiometricAuthenticationFragmentDirections.toAddEditAccountFragment(
                AddEditAccountFragment.ARGUMENT_ONBOARDING
            )
        )
    }

    private fun FragmentBiometricAuthenticationBinding.bindEnableButton() {
        enableButton.setOnClickListener {
            viewModel.handleIntent(BiometricAuthenticationIntent.ClickEnableButton)
        }
    }

    private fun FragmentBiometricAuthenticationBinding.bindSkipButton() {
        skipButton.setOnClickListener {
            viewModel.handleIntent(BiometricAuthenticationIntent.ClickSkipButton)
        }
    }

    private fun FragmentBiometricAuthenticationBinding.applyWindowInsets() {
        root.doOnApplyWindowInsetsCompat { view, windowInsetsCompat, initialPadding ->
            view.updatePadding(bottom = initialPadding.bottom + windowInsetsCompat.systemBars.bottom)
        }
    }
}