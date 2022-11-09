package com.yuriikonovalov.common.framework.common.extentions

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.biometric.BiometricManager

fun Context.resolveActivity(intent: Intent): Boolean {
    return this.packageManager.resolveActivity(
        intent,
        PackageManager.MATCH_DEFAULT_ONLY
    ) != null
}


fun Context.isBiometricAuthenticationAvailable(): Boolean {
    return BiometricManager.from(this)
        .canAuthenticate(
            BiometricManager.Authenticators.BIOMETRIC_STRONG or
                    BiometricManager.Authenticators.DEVICE_CREDENTIAL
        ) == BiometricManager.BIOMETRIC_SUCCESS
}

