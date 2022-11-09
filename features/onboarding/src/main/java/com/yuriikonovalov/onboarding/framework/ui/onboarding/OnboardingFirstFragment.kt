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
import com.yuriikonovalov.onboarding.databinding.FragmentOnboardingFirstBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnboardingFirstFragment : Fragment() {
    private lateinit var binding: FragmentOnboardingFirstBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentOnboardingFirstBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.applyWindowInsets()

        binding.nextButton.setOnClickListener {
            findNavController().navigate(OnboardingFirstFragmentDirections.toOnboardingSecondFragment())
        }

        binding.skipButton.setOnClickListener {
            findNavController().navigate(OnboardingFirstFragmentDirections.toOnboardingThirdFragment())
        }
    }

    private fun FragmentOnboardingFirstBinding.applyWindowInsets() {
        root.doOnApplyWindowInsetsCompat { view, windowInsetsCompat, initialPadding ->
            view.updatePadding(bottom = initialPadding.bottom + windowInsetsCompat.systemBars.bottom)
        }
    }
}