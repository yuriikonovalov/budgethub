package com.yuriikonovalov.onboarding.framework.ui.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.yuriikonovalov.common.framework.common.extentions.collectEvent
import com.yuriikonovalov.common.framework.utils.doOnApplyWindowInsetsCompat
import com.yuriikonovalov.common.framework.utils.systemBars
import com.yuriikonovalov.onboarding.databinding.FragmentOnboardingThirdBinding
import com.yuriikonovalov.onboarding.presentation.onboarding.OnboardingEvent
import com.yuriikonovalov.onboarding.presentation.onboarding.OnboardingIntent
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnboardingThirdFragment : Fragment() {
    private lateinit var binding: FragmentOnboardingThirdBinding
    private val viewModel: OnboardingThirdViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOnboardingThirdBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.applyWindowInsets()
        binding.bindGetStartedButton()
        collectEvent(viewModel.eventFlow, viewModel.eventConsumer) { event ->
            when (event) {
                is OnboardingEvent.CompleteOnboarding -> onCompleteOnboardingEvent()
            }
        }
    }


    private fun onCompleteOnboardingEvent() {
        findNavController().navigate(OnboardingThirdFragmentDirections.toPasswordFragment())
    }

    private fun FragmentOnboardingThirdBinding.bindGetStartedButton() {
        getStartedButton.setOnClickListener {
            viewModel.handleIntent(OnboardingIntent.CompleteOnboarding)
        }
    }

    private fun FragmentOnboardingThirdBinding.applyWindowInsets() {
        root.doOnApplyWindowInsetsCompat { view, windowInsetsCompat, initialPadding ->
            view.updatePadding(bottom = initialPadding.bottom + windowInsetsCompat.systemBars.bottom)
        }
    }
}