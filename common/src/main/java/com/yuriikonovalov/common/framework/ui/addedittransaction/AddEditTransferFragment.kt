package com.yuriikonovalov.common.framework.ui.addedittransaction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.yuriikonovalov.common.R
import com.yuriikonovalov.common.application.entities.Tag
import com.yuriikonovalov.common.application.entities.account.Account
import com.yuriikonovalov.common.databinding.FragmentAddEditTransferBinding
import com.yuriikonovalov.common.framework.common.extentions.collectEvent
import com.yuriikonovalov.common.framework.common.extentions.launchSafely
import com.yuriikonovalov.common.framework.ui.addedittransaction.account.SelectAccountBottomSheetDialog
import com.yuriikonovalov.common.framework.ui.addedittransaction.date.DateBottomSheetDialog
import com.yuriikonovalov.common.framework.ui.addedittransaction.note.EnterNoteDialog
import com.yuriikonovalov.common.framework.ui.addedittransaction.tag.TagBottomSheetDialog
import com.yuriikonovalov.common.framework.ui.toStringResource
import com.yuriikonovalov.common.framework.utils.doOnApplyWindowInsetsCompat
import com.yuriikonovalov.common.framework.utils.money.MoneyFormat
import com.yuriikonovalov.common.framework.utils.money.MoneyTextWatcher
import com.yuriikonovalov.common.framework.utils.systemBars
import com.yuriikonovalov.common.presentation.addedittransaction.AddEditTransferEvent
import com.yuriikonovalov.common.presentation.addedittransaction.AddEditTransferIntent
import com.yuriikonovalov.common.presentation.model.AccountUi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import java.time.OffsetDateTime
import javax.inject.Inject

@AndroidEntryPoint
class AddEditTransferFragment : Fragment() {
    private lateinit var binding: FragmentAddEditTransferBinding
    private val args: AddEditTransferFragmentArgs by navArgs()

    @Inject
    lateinit var viewModelFactory: AddEditTransferViewModel.Factory
    private val viewModel: AddEditTransferViewModel by viewModels {
        AddEditTransferViewModel.provideFactory(viewModelFactory, args.transferId)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        binding = FragmentAddEditTransferBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.applyWindowInsets()
        binding.bindToolbar()
        binding.bindAccountFrom()
        binding.bindAmountFrom()
        binding.bindAccountTo()
        binding.bindAmountTo()
        binding.bindNoteButton()
        binding.bindDateButton()
        binding.bindPhotoButton()
        binding.bindTagButton()
        binding.bindSaveButton()
        collectEvents()
    }

    private fun collectEvents() {
        collectEvent(viewModel.eventFlow, viewModel.eventConsumer) { event ->
            when (event) {
                is AddEditTransferEvent.AccountFromClick -> onAccountFromClickEvent(event.accounts)
                is AddEditTransferEvent.AccountToClick -> onAccountToClickEvent(event.accounts)
                is AddEditTransferEvent.NoteButtonClick -> onNoteButtonClickEvent(event.note)
                is AddEditTransferEvent.DateButtonClick -> onDateButtonClickEvent(
                    event.minDate, event.date
                )
                is AddEditTransferEvent.NavigateBack -> onNavigateBackEvent()
                is AddEditTransferEvent.TagButtonClick -> onTagButtonClickEvent(event.tags)
                is AddEditTransferEvent.EditModeInput -> onEditModeInputEvent(
                    event.amountFrom,
                    event.amountTo
                )
            }
        }
    }

    private fun onEditModeInputEvent(amountFrom: Double, amountTo: Double) {
        binding.amountFrom.amount.setText(MoneyFormat.getStringValue(amountFrom))
        binding.amountTo.amount.setText(MoneyFormat.getStringValue(amountTo))
    }

    private fun onAccountFromClickEvent(accounts: List<Account>) {
        val dialog = SelectAccountBottomSheetDialog(accounts)
        dialog.setOnDialogPositiveClickListener { account ->
            viewModel.handleIntent(AddEditTransferIntent.ChangeAccountFrom(account))
        }
        dialog.show(childFragmentManager, SelectAccountBottomSheetDialog.TAG)
    }

    private fun onAccountToClickEvent(accounts: List<Account>) {
        val dialog = SelectAccountBottomSheetDialog(accounts)
        dialog.setOnDialogPositiveClickListener { account ->
            viewModel.handleIntent(AddEditTransferIntent.ChangeAccountTo(account))
        }
        dialog.show(childFragmentManager, SelectAccountBottomSheetDialog.TAG)
    }

    private fun onNoteButtonClickEvent(note: String?) {
        val enterNoteDialog = EnterNoteDialog(note = note)
        enterNoteDialog.setOnDialogPositiveClickListener { input ->
            viewModel.handleIntent(AddEditTransferIntent.ChangeNote(note = input))
        }
        enterNoteDialog.show(childFragmentManager, EnterNoteDialog.TAG)
    }

    private fun onDateButtonClickEvent(minDate: OffsetDateTime?, date: OffsetDateTime?) {
        val dateModalBottomSheet = DateBottomSheetDialog(minDate, date)
        dateModalBottomSheet.setOnPositiveClickListener { selection ->
            viewModel.handleIntent(AddEditTransferIntent.ChangeDate(selection))
        }
        dateModalBottomSheet.show(childFragmentManager, DateBottomSheetDialog.TAG)
    }

    private fun onNavigateBackEvent() {
        findNavController().navigateUp()
    }

    private fun onTagButtonClickEvent(tags: List<Tag>) {
        TagBottomSheetDialog.show(childFragmentManager, tags) {
            viewModel.handleIntent(AddEditTransferIntent.ChangeTags(it))
        }
    }

    private fun FragmentAddEditTransferBinding.bindAccountFrom() {
        launchSafely {
            viewModel.stateFlow.map { it.accountFrom }
                .filterNotNull()
                .distinctUntilChanged()
                .collect {
                    val ui = AccountUi.from(it)
                    accountFrom.title.text = ui.name
                    accountFrom.balance.text = ui.balance
                    accountFrom.currencyCode.text = ui.currencyCode
                    accountFrom.type.setText(ui.type.toStringResource())
                    accountFrom.background.setBackgroundColor(ui.color)
                }
        }
        accountFrom.root.setOnClickListener {
            viewModel.handleIntent(AddEditTransferIntent.ClickAccountFrom)
        }
    }

    private fun FragmentAddEditTransferBinding.bindAccountTo() {
        launchSafely {
            viewModel.stateFlow.map { it.accountTo }
                .filterNotNull()
                .distinctUntilChanged()
                .collect {
                    val ui = AccountUi.from(it)
                    accountTo.title.text = ui.name
                    accountTo.balance.text = ui.balance
                    accountTo.currencyCode.text = ui.currencyCode
                    accountTo.type.setText(ui.type.toStringResource())
                    accountTo.background.setBackgroundColor(ui.color)
                }
        }
        accountTo.root.setOnClickListener {
            viewModel.handleIntent(AddEditTransferIntent.ClickAccountTo)
        }
    }

    private fun FragmentAddEditTransferBinding.bindAmountFrom() {

        val moneyTextWatcher = MoneyTextWatcher(amountFrom.amount)
        amountFrom.amount.addTextChangedListener(moneyTextWatcher)
        amountFrom.amount.doOnTextChanged { text, _, _, _ ->
            text?.toString()?.let {
                val value = MoneyFormat.getDoubleValue(it)
                viewModel.handleIntent(AddEditTransferIntent.ChangeAmountFrom(value))
            }
        }

        launchSafely {
            viewModel.stateFlow
                .map { it.accountFrom }
                .filterNotNull()
                .distinctUntilChanged()
                .collect { account ->
                    amountFrom.amountContainer.suffixText = account.currency.code
                }
        }
        launchSafely {
            viewModel.stateFlow.map { it.differentCurrencies }
                .distinctUntilChanged()
                .collect { different ->
                    if (different) {
                        amountFrom.amountContainer.prefixText = "-"
                        amountFrom.label.setText(R.string.amount_to_send)
                    } else {
                        amountFrom.amountContainer.prefixText = null
                        amountFrom.label.setText(R.string.amount)
                    }
                }
        }
    }

    private fun FragmentAddEditTransferBinding.bindAmountTo() {
        amountTo.label.setText(R.string.amount_to_receive)

        val moneyTextWatcher = MoneyTextWatcher(amountTo.amount)
        amountTo.amount.addTextChangedListener(moneyTextWatcher)
        amountTo.amount.doOnTextChanged { text, _, _, _ ->
            text?.toString()?.let {
                val value = MoneyFormat.getDoubleValue(it)
                viewModel.handleIntent(AddEditTransferIntent.ChangeAmountTo(value))
            }
        }
        amountTo.amountContainer.prefixText = "+"
        launchSafely {
            viewModel.stateFlow
                .map { it.accountTo }
                .filterNotNull()
                .distinctUntilChanged()
                .collect { account ->
                    amountTo.amountContainer.suffixText = account.currency.code
                }
        }
        launchSafely {
            viewModel.stateFlow.map { it.differentCurrencies }
                .distinctUntilChanged()
                .collect { different ->
                    amountTo.root.isVisible = different
                }
        }
    }

    private fun FragmentAddEditTransferBinding.bindNoteButton() {
        buttons.note.setOnClickListener {
            viewModel.handleIntent(AddEditTransferIntent.ClickNoteButton)
        }
    }

    private fun FragmentAddEditTransferBinding.bindDateButton() {
        buttons.date.setOnClickListener {
            viewModel.handleIntent(AddEditTransferIntent.ClickDateButton)
        }
    }

    private fun FragmentAddEditTransferBinding.bindTagButton() {
        buttons.tag.setOnClickListener {
            viewModel.handleIntent(AddEditTransferIntent.ClickTagButton)
        }
    }

    private fun FragmentAddEditTransferBinding.bindToolbar() {
        toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun FragmentAddEditTransferBinding.bindPhotoButton() {
        buttons.photo.isGone = true
    }

    private fun FragmentAddEditTransferBinding.bindSaveButton() {
        buttons.saveButton.setOnClickListener {
            viewModel.handleIntent(AddEditTransferIntent.ClickSaveButton)
        }
        launchSafely {
            viewModel.stateFlow.map { it.saveButtonEnabled }
                .distinctUntilChanged()
                .collect { enabled ->
                    buttons.saveButton.isEnabled = enabled
                }
        }
    }

    private fun FragmentAddEditTransferBinding.applyWindowInsets() {
        appBar.doOnApplyWindowInsetsCompat { appBar, windowInsetsCompat, initialPadding ->
            appBar.updatePadding(top = initialPadding.top + windowInsetsCompat.systemBars.top)
        }
        buttons.root.doOnApplyWindowInsetsCompat { container, windowInsetsCompat, initialPadding ->
            container.updatePadding(bottom = initialPadding.bottom + windowInsetsCompat.systemBars.bottom)
        }
    }

    companion object {
        const val ARGUMENT_NEW_TRANSFER = Long.MIN_VALUE
    }
}

