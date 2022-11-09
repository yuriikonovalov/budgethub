package com.yuriikonovalov.common.framework.data.local.preferences

import android.content.SharedPreferences
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import com.yuriikonovalov.common.data.local.AppPreferences
import com.yuriikonovalov.common.framework.common.DispatcherProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject

class AppPreferencesImpl @Inject constructor(
    private val preferencesDataStore: DataStore<Preferences>,
    private val encryptedPreferences: SharedPreferences,
    private val dispatcherProvider: DispatcherProvider,
) : AppPreferences {

    override fun onboardingCompleted(): Flow<Boolean> {
        return preferencesDataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }.map { preferences: Preferences ->
                preferences[PreferencesConstants.AppPreferencesKey.ONBOARDING_COMPLETED] ?: false
            }
    }

    override fun biometricAuthenticationOn(): Flow<Boolean> {
        return preferencesDataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences ->
                preferences[PreferencesConstants.AppPreferencesKey.BIOMETRIC_AUTHENTICATION_ENABLED]
                    ?: false
            }
    }

    override fun passwordAuthenticationOn(): Flow<Boolean> {
        return preferencesDataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences ->
                preferences[PreferencesConstants.AppPreferencesKey.LOGIN_REQUIRED] ?: false
            }
    }

    override suspend fun updateOnboardingCompleted(onboardingCompleted: Boolean) {
        preferencesDataStore.edit { preferences ->
            preferences[PreferencesConstants.AppPreferencesKey.ONBOARDING_COMPLETED] =
                onboardingCompleted
        }
    }

    override suspend fun setPassword(password: String): Boolean {
        return withContext(dispatcherProvider.io) {
            encryptedPreferences.edit()
                .putString(EncryptedPreferencesConstants.PASSWORD_KEY, password)
                .commit()
        }
    }

    override suspend fun getPassword(): String? {
        return withContext(dispatcherProvider.io) {
            encryptedPreferences.getString(EncryptedPreferencesConstants.PASSWORD_KEY, null)
        }
    }

    override suspend fun updateBiometricAuthenticationOn(on: Boolean) {
        withContext(dispatcherProvider.io) {
            preferencesDataStore.edit { preferences ->
                preferences[PreferencesConstants.AppPreferencesKey.BIOMETRIC_AUTHENTICATION_ENABLED] =
                    on
            }
        }
    }

    override suspend fun updatePasswordAuthenticationOn(on: Boolean) {
        withContext(dispatcherProvider.io) {
            preferencesDataStore.edit { preferences ->
                preferences[PreferencesConstants.AppPreferencesKey.LOGIN_REQUIRED] = on
            }
        }
    }

}