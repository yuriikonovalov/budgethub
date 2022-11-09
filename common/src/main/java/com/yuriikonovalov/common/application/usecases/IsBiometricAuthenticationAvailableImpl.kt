package com.yuriikonovalov.common.application.usecases

import android.content.Context
import com.yuriikonovalov.common.framework.common.extentions.isBiometricAuthenticationAvailable
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class IsBiometricAuthenticationAvailableImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : IsBiometricAuthenticationAvailable {
    override operator fun invoke(): Boolean {
        return context.isBiometricAuthenticationAvailable()
    }
}