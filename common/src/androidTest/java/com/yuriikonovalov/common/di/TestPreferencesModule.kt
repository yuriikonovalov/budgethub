package com.yuriikonovalov.common.di

import com.yuriikonovalov.common.data.local.AppPreferences
import com.yuriikonovalov.common.framework.di.PreferencesModule
import com.yuriikonovalov.shared_test.fakes.datasource.FakeAppPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [PreferencesModule::class]
)
object TestPreferencesModule {
    @Provides
    @Singleton
    fun bindAppPreferences(): AppPreferences = FakeAppPreferences()
}