package com.yuriikonovalov.accounts.framework.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.yuriikonovalov.accounts.databinding.FragmentAccountsBinding
import com.yuriikonovalov.accounts.presentation.AccountsEvent
import com.yuriikonovalov.accounts.presentation.AccountsIntent
import com.yuriikonovalov.common.framework.common.extentions.collectEvent
import com.yuriikonovalov.common.framework.common.extentions.launchSafely
import com.yuriikonovalov.common.framework.ui.addeditaccount.AddEditAccountFragment
import com.yuriikonovalov.common.framework.ui.addedittransaction.AddEditTransferFragment
import com.yuriikonovalov.common.framework.utils.doOnApplyWindowInsetsCompat
import com.yuriikonovalov.common.framework.utils.systemBars
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

@AndroidEntryPoint
class AccountsFragment : Fragment() {
    private lateinit var binding: FragmentAccountsBinding
    private val viewModel: AccountsViewModel by viewModels()
    private val accountAdapter: AccountAdapter by lazy { AccountAdapter(::showMenu) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentAccountsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.applyWindowInsets()
        binding.bindAccountList()
        binding.bindCreateAccountButton()
        binding.bindTransferButton()
        binding.bindPlaceholder()
        collectEvents()
    }

    private fun collectEvents() {
        collectEvent(viewModel.eventFlow, viewModel.eventConsumer) { event ->
            when (event) {
                is AccountsEvent.AddTransfer -> onAddTransferEvent()
                is AccountsEvent.CreateAccount -> onCreateAccountEvent()
                is AccountsEvent.EditAccount -> onEditAccountEvent(event.id)
            }
        }
    }

    private fun onEditAccountEvent(id: Long) {
        findNavController().navigate(AccountsFragmentDirections.toAddEditAccountFragment(id))
    }

    private fun onCreateAccountEvent() {
        findNavController().navigate(
            AccountsFragmentDirections.toAddEditAccountFragment(AddEditAccountFragment.ARGUMENT_NEW_ACCOUNT)
        )
    }

    private fun onAddTransferEvent() {
        findNavController().navigate(
            AccountsFragmentDirections.toAddEditTransferFragment(AddEditTransferFragment.ARGUMENT_NEW_TRANSFER)
        )
    }

    private fun FragmentAccountsBinding.bindPlaceholder() {
        placeholderContainer.updatePadding(top = appBar.height)
        createAccountPlaceholderButton.setOnClickListener {
            viewModel.handleIntent(AccountsIntent.CreateAccount)
        }
        launchSafely {
            viewModel.stateFlow
                .map { it.placeholderVisible }
                .distinctUntilChanged()
                .collect { visible ->
                    placeholderContainer.isVisible = visible
                }
        }
    }

    private fun FragmentAccountsBinding.bindCreateAccountButton() {
        createAccountButton.setOnClickListener {
            viewModel.handleIntent(AccountsIntent.CreateAccount)
        }
        launchSafely {
            viewModel.stateFlow
                .map { it.createButtonVisible }
                .distinctUntilChanged()
                .collect { visible ->
                    createAccountButton.isVisible = visible
                }
        }
    }

    private fun FragmentAccountsBinding.bindTransferButton() {
        transferButton.setOnClickListener {
            viewModel.handleIntent(AccountsIntent.AddTransfer)
        }
        launchSafely {
            viewModel.stateFlow
                .map { it.transferButtonVisible }
                .distinctUntilChanged()
                .collect { visible ->
                    transferButton.isVisible = visible
                }
        }
    }

    private fun FragmentAccountsBinding.bindAccountList() {
        accountList.layoutManager = LinearLayoutManager(requireContext())
        accountList.adapter = accountAdapter
        launchSafely {
            viewModel.stateFlow
                .map { it.accounts }
                .distinctUntilChanged()
                .collect(accountAdapter::submitList)
        }
    }

    private fun showMenu(accountId: Long, name: String) {
        val menu = MenuBottomSheetDialog(accountId)
        menu.setMenuListener(object : MenuBottomSheetDialog.Listener {
            override fun onEditMenuItemClick(id: Long) {
                viewModel.handleIntent(AccountsIntent.EditAccount(id))
            }

            override fun onDeleteMenuItemClick(id: Long) {
                showConfirmationDialog(id, name)
            }
        })
        menu.show(childFragmentManager, MenuBottomSheetDialog.TAG)
    }

    private fun showConfirmationDialog(id: Long, name: String) {
        MaterialAlertDialogBuilder(requireContext())
            .setIcon(ConfirmationDialog.icon)
            .setTitle(name)
            .setMessage(getString(ConfirmationDialog.message))
            .setPositiveButton(getString(ConfirmationDialog.positiveButtonText)) { dialog, _ ->
                dialog?.dismiss()
                viewModel.handleIntent(AccountsIntent.DeleteAccount(id))
            }
            .setNegativeButton(getString(ConfirmationDialog.negativeButtonText)) { dialog, _ ->
                dialog?.dismiss()
            }
            .show()
    }

    private fun FragmentAccountsBinding.applyWindowInsets() {
        appBar.doOnApplyWindowInsetsCompat { appBar, windowInsetsCompat, initialPadding ->
            appBar.updatePadding(top = initialPadding.top + windowInsetsCompat.systemBars.top)
        }
    }
}