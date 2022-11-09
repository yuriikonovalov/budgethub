package com.yuriikonovalov.settings.presentation

data class SettingsState(
    val passwordAuthenticationOn: Boolean = false,
    val biometricAuthenticationAvailable: Boolean = false,
    val biometricAuthenticationOn: Boolean = false
) {
    val biometricAuthenticationEnabled: Boolean get() = passwordAuthenticationOn

    fun updatePasswordAuthenticationOn(on: Boolean): SettingsState {
        return copy(passwordAuthenticationOn = on)
    }

    fun updateBiometricAuthenticationOn(on: Boolean): SettingsState {
        return copy(biometricAuthenticationOn = on)
    }

    fun updateBiometricAuthenticationAvailable(available: Boolean): SettingsState {
        return copy(biometricAuthenticationAvailable = available)
    }
}

