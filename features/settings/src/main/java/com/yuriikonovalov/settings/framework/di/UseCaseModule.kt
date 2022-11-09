package com.yuriikonovalov.settings.framework.di

import com.yuriikonovalov.settings.application.usecases.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent

@Module
@InstallIn(ActivityRetainedComponent::class)
abstract class UseCaseModule {

    @Binds
    abstract fun bindClearData(impl: ClearDataImpl): ClearData

    @Binds
    abstract fun bindGetDatabaseBackupFile(impl: GetDatabaseBackupFileImpl): GetDatabaseBackupFile

    @Binds
    abstract fun bindRestoreData(impl: RestoreDataImpl): RestoreData

    @Binds
    abstract fun bindSaveBackupFile(impl: SaveBackupFileImpl): SaveBackupFile
}