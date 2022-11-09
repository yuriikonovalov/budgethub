package com.yuriikonovalov.settings.presentation

import java.io.File

sealed class SettingEvent {
    object ClickClearData : SettingEvent()
    object ChooseBackupFileDestination : SettingEvent()
    data class ShareBackupFile(val file: File) : SettingEvent()
    object SaveBackupFile : SettingEvent()
    object ClickRestoreData : SettingEvent()
    object RestartApp : SettingEvent()

    data class ShowToast(val toast: Toast) : SettingEvent()
    object CreatePasswordToEnableAuthentication : SettingEvent()
    object InputPasswordToDisableAuthentication : SettingEvent()

    sealed interface Toast {
        object RestoreDataFailure : Toast
        object ClearDataSuccess : Toast
        object SaveBackupFileFailure : Toast
        object SaveBackupFileSuccess : Toast
        object CreateBackupFileFailure : Toast
    }
}
