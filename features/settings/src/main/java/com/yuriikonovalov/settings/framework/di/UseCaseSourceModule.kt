package com.yuriikonovalov.settings.framework.di

import com.yuriikonovalov.settings.application.usecases.sources.ClearDataSource
import com.yuriikonovalov.settings.application.usecases.sources.GetDatabaseBackupFileSource
import com.yuriikonovalov.settings.application.usecases.sources.RestoreDataSource
import com.yuriikonovalov.settings.application.usecases.sources.SaveBackupFileSource
import com.yuriikonovalov.settings.data.sources.ClearDataSourceImpl
import com.yuriikonovalov.settings.data.sources.GetDatabaseBackupFileSourceImpl
import com.yuriikonovalov.settings.data.sources.RestoreDataSourceImpl
import com.yuriikonovalov.settings.data.sources.SaveBackupFileSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent

@Module
@InstallIn(ActivityRetainedComponent::class)
abstract class UseCaseSourceModule {
    @Binds
    abstract fun bindClearDataSource(impl: ClearDataSourceImpl): ClearDataSource

    @Binds
    abstract fun bindRestoreDataSource(impl: RestoreDataSourceImpl): RestoreDataSource

    @Binds
    abstract fun bindGetDatabaseBackupFileSource(impl: GetDatabaseBackupFileSourceImpl): GetDatabaseBackupFileSource

    @Binds
    abstract fun bindSaveBackupFileSource(impl: SaveBackupFileSourceImpl): SaveBackupFileSource
}