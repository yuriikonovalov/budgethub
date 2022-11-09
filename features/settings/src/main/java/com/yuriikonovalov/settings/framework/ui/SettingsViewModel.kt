package com.yuriikonovalov.settings.framework.ui

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yuriikonovalov.common.application.usecases.*
import com.yuriikonovalov.settings.application.usecases.ClearData
import com.yuriikonovalov.settings.application.usecases.GetDatabaseBackupFile
import com.yuriikonovalov.settings.application.usecases.RestoreData
import com.yuriikonovalov.settings.application.usecases.SaveBackupFile
import com.yuriikonovalov.settings.presentation.SettingEvent
import com.yuriikonovalov.settings.presentation.SettingsIntent
import com.yuriikonovalov.settings.presentation.SettingsState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val isPasswordAuthenticationOn: IsPasswordAuthenticationOn,
    private val updatePasswordAuthenticationOn: UpdatePasswordAuthenticationOn,
    private val setPassword: SetPassword,
    private val isBiometricAuthenticationAvailable: IsBiometricAuthenticationAvailable,
    private val isBiometricAuthenticationOn: IsBiometricAuthenticationOn,
    private val updateBiometricAuthenticationOn: UpdateBiometricAuthenticationOn,
    private val clearData: ClearData,
    private val getBackupFile: GetDatabaseBackupFile,
    private val saveBackupFile: SaveBackupFile,
    private val restoreData: RestoreData
) : ViewModel() {
    private val _stateFlow = MutableStateFlow(SettingsState())
    private val _eventFlow = MutableStateFlow<SettingEvent?>(null)
    private val currentState get() = stateFlow.value
    val stateFlow get() = _stateFlow.asStateFlow()
    val eventFlow get() = _eventFlow.asStateFlow()
    val eventConsumer: () -> Unit = { _eventFlow.value = null }

    init {
        loadPasswordAuthentication()
        loadBiometricAuthentication()
    }

    private fun loadPasswordAuthentication() {
        viewModelScope.launch {
            isPasswordAuthenticationOn().collect { on ->
                _stateFlow.update { it.updatePasswordAuthenticationOn(on) }
            }
        }
    }


    private fun loadBiometricAuthentication() {
        viewModelScope.launch {
            val available = isBiometricAuthenticationAvailable()
            _stateFlow.update { it.updateBiometricAuthenticationAvailable(available) }
            if (available) {
                isBiometricAuthenticationOn().collect { on ->
                    _stateFlow.update { it.updateBiometricAuthenticationOn(on) }
                }
            }
        }
    }

    fun handleIntent(intent: SettingsIntent) {
        when (intent) {
            is SettingsIntent.ClickBiometricAuthenticationButton -> onClickBiometricAuthenticationButton()
            is SettingsIntent.ClickPasswordAuthenticationButton -> onClickPasswordAuthenticationButton()
            is SettingsIntent.ClickBackupData -> onClickBackupData()
            is SettingsIntent.ClickRestoreData -> onClickRestoreData()
            is SettingsIntent.ClickClearData -> onClickClearData()
            is SettingsIntent.ClearData -> onClearData()
            is SettingsIntent.ClickSaveBackupFile -> onClickSaveBackupFile()
            is SettingsIntent.ClickShareBackupFile -> onClickShareBackupFile()
            is SettingsIntent.SaveBackFileInFolder -> onSaveBackFileInFolder(intent.treeUri)
            is SettingsIntent.RestoreData -> onRestoreData(intent.uri)
            is SettingsIntent.EnablePasswordAuthentication -> onEnablePasswordAuthentication(intent.password)
            is SettingsIntent.DisablePasswordAuthentication -> onDisablePasswordAuthentication()
        }
    }

    private fun onEnablePasswordAuthentication(password: String) {
        viewModelScope.launch {
            setPassword(password).onSuccess { successful ->
                if (successful) {
                    updatePasswordAuthenticationOn(on = true)
                }
            }
        }
    }

    private fun onDisablePasswordAuthentication() {
        viewModelScope.launch {
            updatePasswordAuthenticationOn(false)
            // Turn off biometric authentication when password authentication is turned off,
            // but when the password authentication is turned on - don't turn on the biometric authentication.
            updateBiometricAuthenticationOn(false)
        }
    }


    private fun onClickPasswordAuthenticationButton() {
        _eventFlow.value = if (currentState.passwordAuthenticationOn) {
            SettingEvent.InputPasswordToDisableAuthentication
        } else {
            SettingEvent.CreatePasswordToEnableAuthentication
        }
    }

    private fun onClickBiometricAuthenticationButton() {
        viewModelScope.launch {
            updateBiometricAuthenticationOn(!currentState.biometricAuthenticationOn)
        }
    }

    private fun onRestoreData(uri: Uri) {
        viewModelScope.launch {
            restoreData(uri).onSuccess { restored ->
                _eventFlow.value = if (restored) {
                    SettingEvent.RestartApp
                } else {
                    SettingEvent.ShowToast(SettingEvent.Toast.RestoreDataFailure)
                }
            }
        }
    }

    private fun onSaveBackFileInFolder(treeUri: Uri) {
        viewModelScope.launch {
            val resource = getBackupFile()
            resource.onSuccess { file ->
                saveBackupFile(treeUri, file).onSuccess { saved ->
                    _eventFlow.value = if (saved) {
                        SettingEvent.ShowToast(SettingEvent.Toast.SaveBackupFileSuccess)
                    } else {
                        SettingEvent.ShowToast(SettingEvent.Toast.SaveBackupFileFailure)
                    }
                }
            }
            resource.onFailure {
                _eventFlow.value =
                    SettingEvent.ShowToast(SettingEvent.Toast.CreateBackupFileFailure)
            }
        }
    }

    private fun onClickBackupData() {
        _eventFlow.value = SettingEvent.ChooseBackupFileDestination
    }

    private fun onClickRestoreData() {
        _eventFlow.value = SettingEvent.ClickRestoreData
    }

    private fun onClickClearData() {
        _eventFlow.value = SettingEvent.ClickClearData
    }

    private fun onClearData() {
        viewModelScope.launch {
            clearData()
            _eventFlow.value = SettingEvent.ShowToast(SettingEvent.Toast.ClearDataSuccess)
        }
    }

    private fun onClickSaveBackupFile() {
        _eventFlow.value = SettingEvent.SaveBackupFile
    }

    private fun onClickShareBackupFile() {
        viewModelScope.launch {
            val resource = getBackupFile()
            resource.onSuccess { file ->
                _eventFlow.value = SettingEvent.ShareBackupFile(file)
            }

            resource.onFailure {
                _eventFlow.value =
                    SettingEvent.ShowToast(SettingEvent.Toast.CreateBackupFileFailure)
            }
        }
    }

}