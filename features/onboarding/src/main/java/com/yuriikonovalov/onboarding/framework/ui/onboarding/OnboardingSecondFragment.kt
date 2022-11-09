package com.yuriikonovalov.onboarding.framework.ui.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.yuriikonovalov.common.framework.utils.doOnApplyWindowInsetsCompat
import com.yuriikonovalov.common.framework.utils.systemBars
import com.yuriikonovalov.onboarding.databinding.FragmentOnboardingSecondBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnboardingSecondFragment : Fragment() {
    private lateinit var binding: FragmentOnboardingSecondBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentOnboardingSecondBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.applyWindowInsets()
        binding.nextButton.setOnClickListener {
            findNavController().navigate(OnboardingSecondFragmentDirections.toOnboardingThirdFragment())
        }

        binding.skipButton.setOnClickListener {
            findNavController().navigate(OnboardingSecondFragmentDirections.toOnboardingThirdFragment())
        }
    }

    private fun FragmentOnboardingSecondBinding.applyWindowInsets() {
        root.doOnApplyWindowInsetsCompat { view, windowInsetsCompat, initialPadding ->
            view.updatePadding(bottom = initialPadding.bottom + windowInsetsCompat.systemBars.bottom)
        }
    }
}