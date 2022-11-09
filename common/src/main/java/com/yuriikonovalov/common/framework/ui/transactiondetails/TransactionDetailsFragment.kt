package com.yuriikonovalov.common.framework.ui.transactiondetails

import android.content.res.ColorStateList
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
import coil.load
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.yuriikonovalov.common.R
import com.yuriikonovalov.common.application.entities.transaction.TransactionType
import com.yuriikonovalov.common.databinding.FragmentTransactionDetailsBinding
import com.yuriikonovalov.common.framework.common.extentions.collectEvent
import com.yuriikonovalov.common.framework.common.extentions.launchSafely
import com.yuriikonovalov.common.framework.common.extentions.loadSvg
import com.yuriikonovalov.common.framework.utils.doOnApplyWindowInsetsCompat
import com.yuriikonovalov.common.framework.utils.systemBars
import com.yuriikonovalov.common.presentation.model.CategoryUi
import com.yuriikonovalov.common.presentation.transactiondetails.TransactionDetailsEvent
import com.yuriikonovalov.common.presentation.transactiondetails.TransactionDetailsIntent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@AndroidEntryPoint
class TransactionDetailsFragment : Fragment() {
    private lateinit var binding: FragmentTransactionDetailsBinding
    private val args: TransactionDetailsFragmentArgs by navArgs()

    @Inject
    lateinit var viewModelFactory: TransactionDetailsViewModel.Factory
    private val viewModel: TransactionDetailsViewModel by viewModels {
        TransactionDetailsViewModel.provideFactory(viewModelFactory, args.transactionId)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentTransactionDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.applyWindowInsets()
        binding.bindToolbar()
        binding.bindCategory()
        binding.bindAmount()
        binding.bindDate()
        binding.bindAccount()
        binding.bindNote()
        binding.bindPhoto()
        binding.bindTags()
        collectEvents()
    }

    private fun collectEvents() {
        collectEvent(viewModel.eventFlow, viewModel.eventConsumer) { event ->
            when (event) {
                is TransactionDetailsEvent.EditTransaction -> onEditTransactionEvent(event.transactionId)
                is TransactionDetailsEvent.NavigateUp -> onNavigateUpEvent()
            }
        }
    }

    private fun onNavigateUpEvent() {
        findNavController().navigateUp()
    }

    private fun onEditTransactionEvent(transactionId: Long) {
        findNavController().navigate(
            TransactionDetailsFragmentDirections.toAddEditTransactionFragment(transactionId)
        )
    }

    private fun FragmentTransactionDetailsBinding.bindAmount() {
        launchSafely {
            viewModel.stateFlow.map { it.amount }
                .filterNotNull()
                .distinctUntilChanged()
                .collect {
                    amount.text = it
                }
        }
        launchSafely {
            viewModel.stateFlow.map { it.currencyCode }
                .filterNotNull()
                .distinctUntilChanged()
                .collect {
                    currencyCode.text = it
                }
        }
    }

    private fun FragmentTransactionDetailsBinding.bindDate() {
        launchSafely {
            viewModel.stateFlow.map { it.date }
                .filterNotNull()
                .distinctUntilChanged()
                .collect {
                    date.text = it
                }
        }
    }

    private fun FragmentTransactionDetailsBinding.bindAccount() {
        launchSafely {
            viewModel.stateFlow.map { it.accountName }
                .filterNotNull()
                .distinctUntilChanged()
                .collect {
                    accountName.text = it
                }
        }
        launchSafely {
            viewModel.stateFlow.map { it.type }
                .filterNotNull()
                .distinctUntilChanged()
                .collect { type ->
                    accountLabel.text = when (type) {
                        TransactionType.INCOME -> getString(R.string.label_to)
                        TransactionType.EXPENSE -> getString(R.string.label_from)
                    }
                }
        }
    }

    private fun FragmentTransactionDetailsBinding.bindCategory() {
        launchSafely {
            viewModel.stateFlow.map { it.category }
                .filterNotNull()
                .distinctUntilChanged()
                .collect {
                    val ui = CategoryUi.from(requireContext(), it)
                    category.text = ui.name
                    icon.loadSvg(ui.iconPath)
                    icon.imageTintList = ColorStateList.valueOf(ui.colorIcon)
                    icon.setBackgroundColor(ui.colorBackground)
                }
        }
    }

    private fun FragmentTransactionDetailsBinding.bindNote() {
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

    private fun FragmentTransactionDetailsBinding.bindPhoto() {
        launchSafely {
            viewModel.stateFlow.map { it.photoPath }
                .distinctUntilChanged()
                .collect { path ->
                    dividerAdditionalInfo.isVisible = path != null
                    photoIcon.isVisible = path != null
                    photo.isVisible = path != null
                    photo.load(path) {
                        // Hide the photo views if there's been an error.
                        listener(onError = { _, _ ->
                            photo.isVisible = false
                            photoIcon.isVisible = false
                        })
                    }
                }
        }
    }

    private fun FragmentTransactionDetailsBinding.bindTags() {
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

    private fun FragmentTransactionDetailsBinding.bindToolbar() {
        toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_transaction_details_edit -> {
                    viewModel.handleIntent(TransactionDetailsIntent.EditTransaction)
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

    private fun FragmentTransactionDetailsBinding.applyWindowInsets() {
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
            .setTitle(getString(R.string.delete_a_transaction))
            .setMessage(getString(R.string.delete_a_transaction_message))
            .setPositiveButton(getString(R.string.confirm)) { dialog, _ ->
                dialog?.dismiss()
                viewModel.handleIntent(TransactionDetailsIntent.DeleteTransaction)
            }
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog?.dismiss()
            }

        builder.show()
    }
}