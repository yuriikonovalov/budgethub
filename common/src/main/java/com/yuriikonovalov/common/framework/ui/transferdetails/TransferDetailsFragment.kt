package com.yuriikonovalov.common.framework.ui.transferdetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.yuriikonovalov.common.R
import com.yuriikonovalov.common.databinding.FragmentTransferDetailsBinding
import com.yuriikonovalov.common.framework.common.extentions.collectEvent
import com.yuriikonovalov.common.framework.common.extentions.launchSafely
import com.yuriikonovalov.common.framework.utils.doOnApplyWindowInsetsCompat
import com.yuriikonovalov.common.framework.utils.systemBars
import com.yuriikonovalov.common.presentation.transferdetails.TransferDetailsEvent
import com.yuriikonovalov.common.presentation.transferdetails.TransferDetailsIntent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@AndroidEntryPoint
class TransferDetailsFragment : Fragment() {
    private lateinit var binding: FragmentTransferDetailsBinding
    private val args: TransferDetailsFragmentArgs by navArgs()

    @Inject
    lateinit var viewModelFactory: TransferDetailsViewModel.Factory
    private val viewModel: TransferDetailsViewModel by viewModels {
        TransferDetailsViewModel.provideFactory(viewModelFactory, args.transferId)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTransferDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.applyWindowInsets()
        binding.bindToolbar()
        binding.bindDate()
        binding.bindFromSection()
        binding.bindToSection()
        binding.bindNote()
        binding.bindTags()
        collectEvent(viewModel.eventFlow, viewModel.eventConsumer) { event ->
            when (event) {
                is TransferDetailsEvent.EditTransfer -> onEditTransferEvent(event.transferId)
                is TransferDetailsEvent.NavigateUp -> onNavigateUpEvent()
            }
        }
    }

    private fun onNavigateUpEvent() {
        findNavController().navigateUp()
    }

    private fun onEditTransferEvent(transferId: Long) {
        findNavController().navigate(
            TransferDetailsFragmentDirections.toAddEditTransferFragment(transferId)
        )
    }

    private fun FragmentTransferDetailsBinding.bindFromSection() {
        launchSafely {
            viewModel.stateFlow.map { it.accountFrom }
                .filterNotNull()
                .distinctUntilChanged()
                .collect {
                    accountFromName.text = it.name
                    currencyFromCode.text = it.currency.code
                }
        }
        launchSafely {
            viewModel.stateFlow.map { it.amountFrom }
                .filterNotNull()
                .distinctUntilChanged()
                .collect {
                    amountFrom.text = it
                }
        }
    }

    private fun FragmentTransferDetailsBinding.bindToSection() {
        launchSafely {
            viewModel.stateFlow.map { it.accountTo }
                .filterNotNull()
                .distinctUntilChanged()
                .collect {
                    accountToName.text = it.name
                    currencyToCode.text = it.currency.code
                }
        }
        launchSafely {
            viewModel.stateFlow.map { it.amountTo }
                .filterNotNull()
                .distinctUntilChanged()
                .collect {
                    amountTo.text = it
                }
        }
    }

    private fun FragmentTransferDetailsBinding.bindDate() {
        launchSafely {
            viewModel.stateFlow.map { it.date }
                .filterNotNull()
                .distinctUntilChanged()
                .collect {
                    date.text = it
                }
        }
    }

    private fun FragmentTransferDetailsBinding.bindNote() {
        launchSafely {
            viewModel.stateFlow.map { it.note }
                .distinctUntilChanged()
                .collect { text ->
                    dividerAdditionalInfo.isVisible = text != null
                    noteContainer.isVisible = text != null
                    note.text = text
                }
        }
    }

    private fun FragmentTransferDetailsBinding.bindTags() {
        launchSafely {
            viewModel.stateFlow.map { it.tags }
                .distinctUntilChanged()
                .collect { text ->
                    dividerAdditionalInfo.isVisible = text != null
                    tagContainer.isVisible = text != null
                    tags.text = text
                }
        }
    }

    private fun FragmentTransferDetailsBinding.bindToolbar() {
        toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_transaction_details_edit -> {
                    viewModel.handleIntent(TransferDetailsIntent.EditTransfer)
                    true
                }
                R.id.menu_transaction_details_delete -> {
                    showConfirmationDialog()
                    true
                }
                else -> false
            }
        }
    }

    private fun FragmentTransferDetailsBinding.applyWindowInsets() {
        appBar.doOnApplyWindowInsetsCompat { appBar, windowInsetsCompat, initialPadding ->
            appBar.updatePadding(top = initialPadding.top + windowInsetsCompat.systemBars.top)
        }

        content.doOnApplyWindowInsetsCompat { content, windowInsetsCompat, initialPadding ->
            content.updatePadding(bottom = initialPadding.bottom + windowInsetsCompat.systemBars.bottom)
        }
    }

    private fun showConfirmationDialog() {
        val builder = MaterialAlertDialogBuilder(requireContext())
            .setIcon(R.drawable.ic_delete)
            .setTitle(getString(R.string.delete_a_transfer))
            .setMessage(getString(R.string.delete_a_transfer_message))
            .setPositiveButton(getString(R.string.confirm)) { dialog, _ ->
                dialog?.dismiss()
                viewModel.handleIntent(TransferDetailsIntent.DeleteTransfer)
            }
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog?.dismiss()
            }
        builder.show()
    }
}