package com.yuriikonovalov.common.framework.ui.addedittransaction

import android.content.Intent
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.yuriikonovalov.common.R
import com.yuriikonovalov.common.application.entities.Tag
import com.yuriikonovalov.common.application.entities.account.Account
import com.yuriikonovalov.common.application.entities.category.Category
import com.yuriikonovalov.common.application.entities.category.CategoryType
import com.yuriikonovalov.common.application.entities.transaction.TransactionType
import com.yuriikonovalov.common.databinding.FragmentAddEditTransactionBinding
import com.yuriikonovalov.common.framework.common.extentions.*
import com.yuriikonovalov.common.framework.ui.addeditaccount.AddEditAccountFragment
import com.yuriikonovalov.common.framework.ui.addedittransaction.account.SelectAccountBottomSheetDialog
import com.yuriikonovalov.common.framework.ui.addedittransaction.category.SelectCategoryFragment
import com.yuriikonovalov.common.framework.ui.addedittransaction.date.DateBottomSheetDialog
import com.yuriikonovalov.common.framework.ui.addedittransaction.note.EnterNoteDialog
import com.yuriikonovalov.common.framework.ui.addedittransaction.photo.AddPhotoBottomSheetDialog
import com.yuriikonovalov.common.framework.ui.addedittransaction.tag.TagBottomSheetDialog
import com.yuriikonovalov.common.framework.ui.toStringResource
import com.yuriikonovalov.common.framework.utils.doOnApplyWindowInsetsCompat
import com.yuriikonovalov.common.framework.utils.money.MoneyFormat
import com.yuriikonovalov.common.framework.utils.money.MoneyTextWatcher
import com.yuriikonovalov.common.framework.utils.systemBars
import com.yuriikonovalov.common.presentation.addedittransaction.AddEditTransactionEvent
import com.yuriikonovalov.common.presentation.addedittransaction.AddEditTransactionIntent
import com.yuriikonovalov.common.presentation.model.AccountUi
import com.yuriikonovalov.common.presentation.model.CategoryUi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import java.time.OffsetDateTime
import javax.inject.Inject

@AndroidEntryPoint
class AddEditTransactionFragment : Fragment() {
    private lateinit var binding: FragmentAddEditTransactionBinding
    private val args: AddEditTransactionFragmentArgs by navArgs()

    @Inject
    lateinit var viewModelFactory: AddEditTransactionViewModel.Factory
    private val viewModel: AddEditTransactionViewModel by viewModels {
        AddEditTransactionViewModel.provideFactory(viewModelFactory, args.transactionId)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setFragmentResultListener(CATEGORY_REQUEST_KEY) { _, bundle ->
            val category =
                bundle.getParcelableCompat(
                    SelectCategoryFragment.CATEGORY_BUNDLE_KEY,
                    Category::class.java
                )!!
            viewModel.handleIntent(AddEditTransactionIntent.ChangeCategory(category))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentAddEditTransactionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.applyWindowInsets()
        binding.bindToolbar()
        binding.bindTypeSelector()
        binding.bindAccount()
        binding.bindAmount()
        binding.bindCategory()
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
                is AddEditTransactionEvent.ShowCategories -> onShowCategoriesEvent(event.type)
                is AddEditTransactionEvent.NoteButtonClick -> onNoteButtonClickEvent(event.note)
                is AddEditTransactionEvent.DateButtonClick -> onDateButtonClickEvent(
                    event.minDate, event.date
                )
                is AddEditTransactionEvent.PhotoButtonClick -> onPhotoButtonClickEvent(event.imageUri)
                is AddEditTransactionEvent.NavigateBack -> onNavigateBackEvent()
                is AddEditTransactionEvent.TagButtonClick -> onTagButtonClickEvent(event.tags)
                is AddEditTransactionEvent.EditModeInput -> onEditModeInputEvent(
                    event.type, event.amount
                )
                is AddEditTransactionEvent.ShowAccounts -> onShowAccountsEvent(event.accounts)
                is AddEditTransactionEvent.CreateAccount -> onCreateAccountEvent()
            }
        }
    }

    private fun onCreateAccountEvent() {
        findNavController().navigate(
            AddEditTransactionFragmentDirections.toAddEditAccountFragment(
                AddEditAccountFragment.ARGUMENT_NEW_ACCOUNT
            )
        )
    }

    private fun onShowAccountsEvent(accounts: List<Account>) {
        val dialog = SelectAccountBottomSheetDialog(accounts)
        dialog.setOnDialogPositiveClickListener { account ->
            viewModel.handleIntent(AddEditTransactionIntent.ChangeAccount(account))
        }
        dialog.show(childFragmentManager, SelectAccountBottomSheetDialog.TAG)
    }

    private fun onEditModeInputEvent(type: TransactionType, amount: Double) {
        clickType(type)
        inputAmount(amount)
    }

    // Invoked when the case is EDIT to set amount in the edit text.
    private fun inputAmount(amount: Double) {
        val amountString = MoneyFormat.getStringValue(amount)
        binding.amountView.amount.setText(amountString)
    }

    // Invoked when the case is EDIT to select a correct type.
    private fun clickType(type: TransactionType) {
        when (type) {
            TransactionType.INCOME -> binding.typeIncome.isChecked = true
            TransactionType.EXPENSE -> binding.typeExpense.isChecked = true
        }
    }

    private fun onShowCategoriesEvent(type: CategoryType) {
        findNavController().navigate(
            AddEditTransactionFragmentDirections.toSelectCategoryFragment(type)
        )
    }

    private fun onNoteButtonClickEvent(note: String?) {
        val enterNoteDialog = EnterNoteDialog(note = note)
        enterNoteDialog.setOnDialogPositiveClickListener { input ->
            viewModel.handleIntent(AddEditTransactionIntent.ChangeNote(note = input))
        }
        enterNoteDialog.show(childFragmentManager, EnterNoteDialog.TAG)
    }

    private fun onDateButtonClickEvent(
        minDate: OffsetDateTime?,
        date: OffsetDateTime?,
    ) {
        val dateModalBottomSheet = DateBottomSheetDialog(minDate, date)
        dateModalBottomSheet.setOnPositiveClickListener { selection ->
            viewModel.handleIntent(AddEditTransactionIntent.ChangeDate(selection))
        }
        dateModalBottomSheet.show(childFragmentManager, DateBottomSheetDialog.TAG)
    }

    private fun onPhotoButtonClickEvent(imageUri: Uri?) {
        val photoModalBottomSheet = AddPhotoBottomSheetDialog(uri = imageUri)
        photoModalBottomSheet.setOnDialogClickListener(object : AddPhotoBottomSheetDialog.Listener {
            override fun onDialogPositiveClick(imageUri: Uri) {
                viewModel.handleIntent(AddEditTransactionIntent.ChangeImage(imageUri))
            }

            override fun onDialogNegativeClick(imageUri: Uri?) {
                viewModel.handleIntent(AddEditTransactionIntent.ChangeImage(imageUri))
            }
        })
        photoModalBottomSheet.show(childFragmentManager, AddPhotoBottomSheetDialog.TAG)
    }

    private fun onNavigateBackEvent() {
        findNavController().navigateUp()
    }

    private fun onTagButtonClickEvent(tags: List<Tag>) {
        TagBottomSheetDialog.show(childFragmentManager, tags) {
            viewModel.handleIntent(AddEditTransactionIntent.ChangeTags(it))
        }
    }

    private fun FragmentAddEditTransactionBinding.bindAccount() {
        account.root.setOnClickListener {
            viewModel.handleIntent(AddEditTransactionIntent.ClickAccount)
        }
        launchSafely {
            viewModel.stateFlow
                .map { it.account }
                .distinctUntilChanged()
                .collect {
                    it?.let {
                        val ui = AccountUi.from(it)
                        account.title.text = ui.name
                        account.balance.text = ui.balance
                        account.currencyCode.text = ui.currencyCode
                        account.type.setText(ui.type.toStringResource())
                        account.background.setBackgroundColor(ui.color)
                        account.createButton.isVisible = false
                    }
                    account.createButton.isVisible = it == null
                }
        }
    }

    private fun FragmentAddEditTransactionBinding.bindTypeSelector() {
        type.setOnCheckedStateChangeListener { _, checkedIds ->
            when (checkedIds.first()) {
                typeExpense.id -> {
                    viewModel.handleIntent(AddEditTransactionIntent.ChangeType(TransactionType.EXPENSE))
                }
                typeIncome.id -> {
                    viewModel.handleIntent(AddEditTransactionIntent.ChangeType(TransactionType.INCOME))
                }
            }
        }
        type.check(binding.typeExpense.id)
        launchSafely {
            viewModel.stateFlow
                .map { Pair(it.chipIncomeVisible, it.chipExpenseVisible) }
                .distinctUntilChanged()
                .collect {
                    typeIncome.isVisible = it.first
                    typeExpense.isVisible = it.second
                }
        }
    }


    private fun FragmentAddEditTransactionBinding.bindAmount() {
        val moneyTextWatcher = MoneyTextWatcher(amountView.amount)
        amountView.amount.addTextChangedListener(moneyTextWatcher)
        amountView.amount.doOnTextChanged { text, _, _, _ ->
            text?.toString()?.let {
                val value = MoneyFormat.getDoubleValue(it)
                viewModel.handleIntent(AddEditTransactionIntent.ChangeAmount(value))
            }
        }

        launchSafely {
            viewModel.stateFlow
                .map { it.account }
                .filterNotNull()
                .distinctUntilChanged()
                .collect { account ->
                    amountView.amountContainer.suffixText = account.currency.code
                }
        }
        launchSafely {
            viewModel.stateFlow
                .map { it.type }
                .distinctUntilChanged()
                .collect { type ->
                    amountView.amountContainer.prefixText =
                        if (type == TransactionType.INCOME) "+" else "-"
                }
        }
    }

    private fun FragmentAddEditTransactionBinding.bindCategory() {
        category.root.setOnClickListener {
            viewModel.handleIntent(AddEditTransactionIntent.ClickCategory)
        }
        launchSafely {
            viewModel.stateFlow
                .map { it.category }
                .filterNotNull()
                .distinctUntilChanged()
                .collect { category ->
                    val ui = CategoryUi.from(requireContext(), category)
                    this.category.title.text = ui.name
                    this.category.icon.loadSvg(ui.iconPath)
                    this.category.icon.imageTintList = ColorStateList.valueOf(ui.colorIcon)
                    this.category.icon.setBackgroundColor(ui.colorBackground)
                }
        }
        launchSafely {
            viewModel.stateFlow
                .map { it.type }
                .distinctUntilChanged()
                .collect { type ->
                    categoryLabel.setText(type.toStringRes())
                }
        }
    }

    private fun FragmentAddEditTransactionBinding.bindNoteButton() {
        buttons.note.setOnClickListener {
            viewModel.handleIntent(AddEditTransactionIntent.ClickNoteButton)
        }
    }

    private fun FragmentAddEditTransactionBinding.bindDateButton() {
        buttons.date.setOnClickListener {
            viewModel.handleIntent(AddEditTransactionIntent.ClickDateButton)
        }
    }

    private fun FragmentAddEditTransactionBinding.bindPhotoButton() {
        // True if there's an activity to run the given intent.
        buttons.photo.isVisible = requireContext().resolveActivity(INTENT_GET_CONTENT_IMAGE)
                || requireContext().resolveActivity(INTENT_IMAGE_CAPTURE)
        buttons.photo.setOnClickListener {
            viewModel.handleIntent(AddEditTransactionIntent.ClickPhotoButton)
        }
    }

    private fun FragmentAddEditTransactionBinding.bindTagButton() {
        buttons.tag.setOnClickListener {
            viewModel.handleIntent(AddEditTransactionIntent.ClickTagButton)
        }
    }

    private fun FragmentAddEditTransactionBinding.bindToolbar() {
        toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun FragmentAddEditTransactionBinding.bindSaveButton() {
        buttons.saveButton.setOnClickListener {
            viewModel.handleIntent(AddEditTransactionIntent.ClickSaveButton)
        }
        launchSafely {
            viewModel.stateFlow
                .map { it.saveButtonEnabled }
                .distinctUntilChanged()
                .collect { visible ->
                    buttons.saveButton.isEnabled = visible
                }
        }
    }

    private fun FragmentAddEditTransactionBinding.applyWindowInsets() {
        appBar.doOnApplyWindowInsetsCompat { appBar, windowInsetsCompat, initialPadding ->
            appBar.updatePadding(top = initialPadding.top + windowInsetsCompat.systemBars.top)
        }
        buttons.root.doOnApplyWindowInsetsCompat { container, windowInsetsCompat, initialPadding ->
            container.updatePadding(bottom = initialPadding.bottom + windowInsetsCompat.systemBars.bottom)
        }
    }

    companion object {
        val INTENT_GET_CONTENT_IMAGE =
            Intent(Intent.ACTION_GET_CONTENT).addCategory(Intent.CATEGORY_OPENABLE)
                .setType("image/*")

        val INTENT_IMAGE_CAPTURE = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        const val CATEGORY_REQUEST_KEY = "CATEGORY_REQUEST_KEY"
        const val ARGUMENT_NEW_TRANSACTION = Long.MIN_VALUE
    }

    private fun TransactionType.toStringRes(): Int {
        return if (this == TransactionType.INCOME)
            R.string.category_label_income
        else
            R.string.category_label_expense
    }
}