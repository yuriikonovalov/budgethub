package com.yuriikonovalov.common.framework.data.local.preferences

import androidx.datastore.preferences.core.booleanPreferencesKey

object PreferencesConstants {
    const val PREFERENCES_NAME = "app_preferences"

    object AppPreferencesKey {
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        val LOGIN_REQUIRED = booleanPreferencesKey("login_required")
        val BIOMETRIC_AUTHENTICATION_ENABLED =
            booleanPreferencesKey("biometric_authentication_enabled")
    }
}