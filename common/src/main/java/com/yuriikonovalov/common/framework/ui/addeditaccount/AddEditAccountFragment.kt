package com.yuriikonovalov.common.framework.ui.addeditaccount

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.updatePadding
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.yuriikonovalov.common.NavGraphDirections
import com.yuriikonovalov.common.R
import com.yuriikonovalov.common.application.entities.account.AccountType
import com.yuriikonovalov.common.databinding.FragmentAddEditAccountBinding
import com.yuriikonovalov.common.framework.common.extentions.collectEvent
import com.yuriikonovalov.common.framework.common.extentions.launchSafely
import com.yuriikonovalov.common.framework.ui.common.ColorAdapter
import com.yuriikonovalov.common.framework.ui.currency.CurrencyBottomSheetDialog
import com.yuriikonovalov.common.framework.ui.dialogs.ColorPickerDialog
import com.yuriikonovalov.common.framework.ui.toStringResource
import com.yuriikonovalov.common.framework.utils.doOnApplyWindowInsetsCompat
import com.yuriikonovalov.common.framework.utils.money.MoneyFormat
import com.yuriikonovalov.common.framework.utils.money.MoneyTextWatcher
import com.yuriikonovalov.common.framework.utils.systemBars
import com.yuriikonovalov.common.presentation.addeditaccount.AddEditAccountEvent
import com.yuriikonovalov.common.presentation.addeditaccount.AddEditAccountIntent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@AndroidEntryPoint
class AddEditAccountFragment : Fragment() {
    private lateinit var binding: FragmentAddEditAccountBinding
    private val args: AddEditAccountFragmentArgs by navArgs()

    @Inject
    lateinit var viewModelFactory: AddEditAccountViewModel.Factory
    private val viewModel: AddEditAccountViewModel by viewModels {
        AddEditAccountViewModel.provideFactory(viewModelFactory, args.accountId)
    }

    private val colorAdapter: ColorAdapter by lazy {
        ColorAdapter(
            onItemClick = { color ->
                viewModel.handleIntent(AddEditAccountIntent.ChangeColor(color))
            },
            onButtonClick = {
                viewModel.handleIntent(AddEditAccountIntent.ClickColorButton)
            })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentAddEditAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.applyWindowInsets()
        binding.bindToolbar()
        binding.bindType()
        binding.bindCard()
        binding.bindNameInput()
        binding.bindBalanceInput()
        binding.bindCurrency()
        binding.bindColors()
        binding.bindSaveButton()
        collectEvents()
    }

    private fun collectEvents() {
        collectEvent(viewModel.eventFlow, viewModel.eventConsumer) { event ->
            when (event) {
                is AddEditAccountEvent.NavigateUp -> onNavigateUpEvent()
                is AddEditAccountEvent.CurrencyButtonClick -> onCurrencyButtonClickEvent()
                is AddEditAccountEvent.ColorButtonClick -> onColorButtonClickEvent()
                is AddEditAccountEvent.InputName -> onInputNameEvent(event.name)
            }
        }
    }

    private fun onInputNameEvent(name: String) {
        binding.nameInput.setText(name)
    }

    private fun onNavigateUpEvent() {
        if (args.accountId == ARGUMENT_ONBOARDING) {
            findNavController().navigate(NavGraphDirections.toHome())
        } else {
            findNavController().navigateUp()
        }
    }

    private fun onCurrencyButtonClickEvent() {
        CurrencyBottomSheetDialog.show(childFragmentManager) {
            viewModel.handleIntent(AddEditAccountIntent.ChangeCurrency(it))
        }
    }

    private fun onColorButtonClickEvent() {
        ColorPickerDialog.show(childFragmentManager) { color ->
            viewModel.handleIntent(AddEditAccountIntent.AddColor(color))
        }
    }

    private fun FragmentAddEditAccountBinding.bindCard() {
        launchSafely {
            viewModel.stateFlow.map { it.card }
                .distinctUntilChanged()
                .collect { data ->
                    card.title.text = data.name.ifBlank { getString(R.string.account_name) }
                    card.type.setText(data.type.toStringResource())
                    card.balance.text = MoneyFormat.getStringValue(data.balance)
                    card.currencyCode.text = data.currency?.code
                    card.background.setBackgroundColor(data.color)
                }
        }
    }


    private fun FragmentAddEditAccountBinding.bindNameInput() {
        nameInput.doOnTextChanged { text, _, _, _ ->
            text?.toString()?.let {
                viewModel.handleIntent(AddEditAccountIntent.ChangeName(it))
            }
        }
    }

    private fun FragmentAddEditAccountBinding.bindType() {
        typeGroup.setOnCheckedStateChangeListener { _, checkedIds ->
            when (checkedIds.first()) {
                typeCard.id -> {
                    viewModel.handleIntent(AddEditAccountIntent.ChangeType(AccountType.CARD))
                }
                typeCash.id -> {
                    viewModel.handleIntent(AddEditAccountIntent.ChangeType(AccountType.CASH))
                }
                typeDeposit.id -> {
                    viewModel.handleIntent(AddEditAccountIntent.ChangeType(AccountType.DEPOSIT))
                }
            }
        }
        launchSafely {
            viewModel.stateFlow.map { it.type }
                .distinctUntilChanged()
                .collect { type ->
                    when (type) {
                        AccountType.CARD -> typeGroup.check(typeCard.id)
                        AccountType.CASH -> typeGroup.check(typeCash.id)
                        AccountType.DEPOSIT -> typeGroup.check(typeDeposit.id)
                    }
                }
        }
    }

    private fun FragmentAddEditAccountBinding.bindBalanceInput() {
        val moneyTextWatcher = MoneyTextWatcher(balanceInput)
        balanceInput.addTextChangedListener(moneyTextWatcher)

        balanceInput.doOnTextChanged { text, _, _, _ ->
            text?.toString()?.let {
                viewModel.handleIntent(AddEditAccountIntent.ChangeBalance(it))
            }
        }

        launchSafely {
            viewModel.stateFlow.map { it.balanceGone }
                .distinctUntilChanged()
                .collect { gone ->
                    balanceContainer.isGone = gone
                }
        }
    }

    private fun FragmentAddEditAccountBinding.bindCurrency() {
        currency.setOnClickListener {
            viewModel.handleIntent(AddEditAccountIntent.ClickCurrencyButton)
        }
        launchSafely {
            viewModel.stateFlow.map { it.currency }
                .distinctUntilChanged()
                .collect {
                    currency.text = it?.code
                }
        }
        launchSafely {
            viewModel.stateFlow.map { it.currencyGone }
                .distinctUntilChanged()
                .collect { gone ->
                    currency.isGone = gone
                }
        }
    }

    private fun FragmentAddEditAccountBinding.bindColors() {
        colors.adapter = colorAdapter
        colors.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        launchSafely {
            viewModel.stateFlow.map { it.colors }
                .distinctUntilChanged()
                .collect(colorAdapter::submitList)
        }
    }

    private fun FragmentAddEditAccountBinding.bindToolbar() {
        if (args.accountId == ARGUMENT_ONBOARDING) {
            toolbar.navigationIcon = null
        } else {
            toolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }
        }
    }

    private fun FragmentAddEditAccountBinding.bindSaveButton() {
        saveButton.setOnClickListener {
            viewModel.handleIntent(AddEditAccountIntent.ClickSaveButton)
        }
        launchSafely {
            viewModel.stateFlow.map { it.saveButtonEnabled }
                .distinctUntilChanged()
                .collect { enabled ->
                    saveButton.isEnabled = enabled
                }
        }
    }

    private fun FragmentAddEditAccountBinding.applyWindowInsets() {
        appBar.doOnApplyWindowInsetsCompat { appBar, windowInsetsCompat, initialPadding ->
            appBar.updatePadding(top = initialPadding.top + windowInsetsCompat.systemBars.top)
        }

        root.doOnApplyWindowInsetsCompat { root, windowInsetsCompat, initialPadding ->
            root.updatePadding(bottom = initialPadding.bottom + windowInsetsCompat.systemBars.bottom)
        }
    }

    companion object {
        const val ARGUMENT_NEW_ACCOUNT = -1L
        const val ARGUMENT_ONBOARDING = -2L
    }
}