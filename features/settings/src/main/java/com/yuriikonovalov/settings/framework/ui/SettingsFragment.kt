package com.yuriikonovalov.settings.framework.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.jakewharton.processphoenix.ProcessPhoenix
import com.yuriikonovalov.common.framework.common.extentions.collectEvent
import com.yuriikonovalov.common.framework.common.extentions.launchSafely
import com.yuriikonovalov.common.framework.common.extentions.toContentUri
import com.yuriikonovalov.common.framework.utils.doOnApplyWindowInsetsCompat
import com.yuriikonovalov.common.framework.utils.systemBars
import com.yuriikonovalov.settings.R
import com.yuriikonovalov.settings.databinding.FragmentSettingsBinding
import com.yuriikonovalov.settings.framework.ui.createpassword.CreatePasswordDialog
import com.yuriikonovalov.settings.framework.ui.inputpassword.InputPasswordDialog
import com.yuriikonovalov.settings.presentation.SettingEvent
import com.yuriikonovalov.settings.presentation.SettingsIntent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import java.io.File

@AndroidEntryPoint
class SettingsFragment : Fragment() {
    private lateinit var binding: FragmentSettingsBinding
    private val viewModel: SettingsViewModel by viewModels()

    private val chooseFolderLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri = result.data?.data!!
                viewModel.handleIntent(SettingsIntent.SaveBackFileInFolder(uri))
            }
        }

    private val chooseFileLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                viewModel.handleIntent(SettingsIntent.RestoreData(it))
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.applyWindowInsets()
        binding.bindPasswordAuthenticationSwitch()
        binding.bindBiometricAuthentication()
        binding.bindBackupDataButton()
        binding.bindRestoreDataButton()
        binding.bindClearDataButton()
        collectEvents()
    }


    private fun collectEvents() {
        collectEvent(viewModel.eventFlow, viewModel.eventConsumer) { event ->
            when (event) {
                is SettingEvent.CreatePasswordToEnableAuthentication -> onCreatePasswordToEnableAuthenticationEvent()
                is SettingEvent.InputPasswordToDisableAuthentication -> onInputPasswordToDisableAuthenticationEvent()
                is SettingEvent.ClickRestoreData -> onClickRestoreDataEvent()
                is SettingEvent.ClickClearData -> onClickClearDataEvent()
                is SettingEvent.ChooseBackupFileDestination -> onChooseBackupFileDestinationEvent()
                is SettingEvent.SaveBackupFile -> onSaveBackupFileEvent()
                is SettingEvent.ShareBackupFile -> onShareBackupFileEvent(event.file)
                is SettingEvent.RestartApp -> onRestartAppEvent()
                is SettingEvent.ShowToast -> onShowToastEvent(event.toast)
            }
        }
    }

    private fun onInputPasswordToDisableAuthenticationEvent() {
        InputPasswordDialog().apply {
            onCorrectPassword = {
                viewModel.handleIntent(SettingsIntent.DisablePasswordAuthentication)
            }
        }.show(childFragmentManager, InputPasswordDialog.TAG)
    }

    private fun onCreatePasswordToEnableAuthenticationEvent() {
        CreatePasswordDialog().apply {
            onPositiveButtonClick = { password ->
                viewModel.handleIntent(SettingsIntent.EnablePasswordAuthentication(password))
            }
        }.show(childFragmentManager, CreatePasswordDialog.TAG)
    }

    private fun onClickRestoreDataEvent() {
        chooseFileLauncher.launch("application/*")
    }

    private fun onClickClearDataEvent() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.clear_data_title))
            .setMessage(getString(R.string.clear_data_message))
            .setPositiveButton(getString(com.yuriikonovalov.common.R.string.confirm)) { dialog, _ ->
                viewModel.handleIntent(SettingsIntent.ClearData)
                dialog.dismiss()
            }
            .setNegativeButton(getString(com.yuriikonovalov.common.R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun onChooseBackupFileDestinationEvent() {
        BackupDataDialog().apply {
            onSaveClick = { viewModel.handleIntent(SettingsIntent.ClickSaveBackupFile) }
            onShareClick = { viewModel.handleIntent(SettingsIntent.ClickShareBackupFile) }
        }.show(childFragmentManager, BackupDataDialog.TAG)
    }

    private fun onSaveBackupFileEvent() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
        chooseFolderLauncher.launch(intent)
    }

    private fun onShareBackupFileEvent(file: File) {
        val uri = file.toContentUri(requireContext())
        val intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, uri)
            type = "application/*"
        }
        startActivity(Intent.createChooser(intent, null))
    }

    private fun onRestartAppEvent() {
        ProcessPhoenix.triggerRebirth(requireContext())
    }

    private fun onShowToastEvent(toast: SettingEvent.Toast) {
        when (toast) {
            is SettingEvent.Toast.RestoreDataFailure -> toast(R.string.restore_data_failure)
            is SettingEvent.Toast.ClearDataSuccess -> toast(R.string.clear_data_success)
            is SettingEvent.Toast.SaveBackupFileSuccess -> toast(R.string.save_backup_file_success)
            is SettingEvent.Toast.SaveBackupFileFailure -> toast(R.string.save_backup_file_failure)
            is SettingEvent.Toast.CreateBackupFileFailure -> toast(R.string.create_backup_file_failure)
        }
    }

    private fun toast(@StringRes resId: Int) {
        Toast.makeText(requireContext(), resId, Toast.LENGTH_SHORT).show()
    }

    private fun FragmentSettingsBinding.bindPasswordAuthenticationSwitch() {
        password.title.setText(R.string.password_authentication)
        password.button.setOnClickListener {
            viewModel.handleIntent(SettingsIntent.ClickPasswordAuthenticationButton)
        }
        launchSafely {
            viewModel.stateFlow.map { it.passwordAuthenticationOn }
                .distinctUntilChanged()
                .collect { on ->
                    password.button.text =
                        if (on) getString(R.string.disable) else getString(R.string.enable)
                    password.button.isActivated = on
                }
        }
    }

    private fun FragmentSettingsBinding.bindBiometricAuthentication() {
        biometric.title.setText(R.string.biometric_authentication)
        biometric.button.setOnClickListener {
            viewModel.handleIntent(SettingsIntent.ClickBiometricAuthenticationButton)
        }
        launchSafely {
            viewModel.stateFlow.map { it.biometricAuthenticationAvailable }
                .distinctUntilChanged()
                .collect { available ->
                    biometric.root.isVisible = available
                }
        }
        launchSafely {
            viewModel.stateFlow.map { it.biometricAuthenticationOn }
                .distinctUntilChanged()
                .collect { on ->
                    biometric.button.text =
                        if (on) getString(R.string.disable) else getString(R.string.enable)
                    biometric.button.isActivated = on
                }
        }

        launchSafely {
            viewModel.stateFlow.map { it.biometricAuthenticationEnabled }
                .distinctUntilChanged()
                .collect { enabled ->
                    biometric.title.isEnabled = enabled
                    biometric.button.isEnabled = enabled
                }
        }
    }


    private fun FragmentSettingsBinding.bindBackupDataButton() {
        dataBackupButton.title.setText(R.string.backup_data)
        dataBackupButton.root.setOnClickListener {
            viewModel.handleIntent(SettingsIntent.ClickBackupData)
        }
    }

    private fun FragmentSettingsBinding.bindRestoreDataButton() {
        restoreDataButton.title.setText(R.string.restore_data)
        restoreDataButton.root.setOnClickListener {
            viewModel.handleIntent(SettingsIntent.ClickRestoreData)
        }
    }

    private fun FragmentSettingsBinding.bindClearDataButton() {
        clearDataButton.title.setText(R.string.clear_data)
        clearDataButton.root.setOnClickListener {
            viewModel.handleIntent(SettingsIntent.ClickClearData)
        }
    }

    private fun FragmentSettingsBinding.applyWindowInsets() {
        appBar.doOnApplyWindowInsetsCompat { appBar, windowInsetsCompat, initialPadding ->
            appBar.updatePadding(top = initialPadding.top + windowInsetsCompat.systemBars.top)
        }
    }
}