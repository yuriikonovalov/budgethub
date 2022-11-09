package com.yuriikonovalov.common.framework.di

import android.content.Context
import android.content.SharedPreferences
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.yuriikonovalov.common.data.local.AppPreferences
import com.yuriikonovalov.common.framework.data.local.preferences.EncryptedPreferencesConstants
import com.yuriikonovalov.common.framework.data.local.preferences.PreferencesConstants.PREFERENCES_NAME
import com.yuriikonovalov.common.framework.data.local.preferences.AppPreferencesImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class PreferencesModule {

    @Binds
    abstract fun bindAppPreferences(impl: AppPreferencesImpl): AppPreferences

    companion object {
        @Singleton
        @Provides
        fun providePreferencesDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
            return PreferenceDataStoreFactory.create(
                corruptionHandler = ReplaceFileCorruptionHandler(
                    produceNewData = { emptyPreferences() }
                ),
                scope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
                produceFile = {
                    context.preferencesDataStoreFile(PREFERENCES_NAME)
                }
            )
        }


        @Singleton
        @Provides
        fun provideEncryptedPreferences(@ApplicationContext content: Context): SharedPreferences {
            val keyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
            return EncryptedSharedPreferences.create(
                EncryptedPreferencesConstants.PREFERENCES_NAME,
                keyAlias,
                content,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        }
    }
}