package com.yuriikonovalov.settings.presentation

import android.net.Uri

sealed class SettingsIntent {
    object ClickPasswordAuthenticationButton : SettingsIntent()
    object ClickBiometricAuthenticationButton : SettingsIntent()
    data class EnablePasswordAuthentication(val password: String) : SettingsIntent()
    object DisablePasswordAuthentication : SettingsIntent()
    object ClearData : SettingsIntent()
    object ClickClearData : SettingsIntent()
    object ClickRestoreData : SettingsIntent()
    object ClickBackupData : SettingsIntent()
    object ClickShareBackupFile : SettingsIntent()
    object ClickSaveBackupFile : SettingsIntent()
    data class SaveBackFileInFolder(val treeUri: Uri) : SettingsIntent()
    data class RestoreData(val uri: Uri) : SettingsIntent()
}
