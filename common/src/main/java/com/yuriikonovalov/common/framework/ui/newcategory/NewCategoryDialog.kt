package com.yuriikonovalov.common.framework.ui.newcategory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.updatePadding
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.yuriikonovalov.common.R
import com.yuriikonovalov.common.application.entities.category.CategoryType
import com.yuriikonovalov.common.databinding.DialogNewCategoryBinding
import com.yuriikonovalov.common.framework.common.extentions.collectEvent
import com.yuriikonovalov.common.framework.common.extentions.launchSafely
import com.yuriikonovalov.common.framework.ui.common.ColorAdapter
import com.yuriikonovalov.common.framework.ui.dialogs.ColorPickerDialog
import com.yuriikonovalov.common.framework.utils.doOnApplyWindowInsetsCompat
import com.yuriikonovalov.common.framework.utils.systemBars
import com.yuriikonovalov.common.presentation.newcategory.NewCategoryEvent
import com.yuriikonovalov.common.presentation.newcategory.NewCategoryIntent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@AndroidEntryPoint
class NewCategoryDialog : DialogFragment() {
    private lateinit var binding: DialogNewCategoryBinding
    private val args: NewCategoryDialogArgs by navArgs()

    @Inject
    lateinit var viewModelFactory: NewCategoryViewModel.Factory
    private val viewModel: NewCategoryViewModel by viewModels {
        NewCategoryViewModel.provideFactory(viewModelFactory, args.type)
    }

    private val colorAdapter: ColorAdapter by lazy {
        ColorAdapter(
            onItemClick = { color ->
                viewModel.handleIntent(NewCategoryIntent.ChangeColor(color))
            },
            onButtonClick = {
                viewModel.handleIntent(NewCategoryIntent.ClickColorButton)
            })
    }

    private val iconAdapter: IconAdapter by lazy {
        IconAdapter { icon ->
            viewModel.handleIntent(NewCategoryIntent.ChangeIcon(icon))
        }
    }

    override fun getTheme(): Int {
        return R.style.Theme_DialogFragment
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogNewCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.applyWindowInsets()
        binding.bindToolbar()
        binding.bindType()
        binding.bindNameInput()
        binding.bindColors()
        binding.bindIcons()
        binding.bindSaveButton()
        collectEvents()
    }

    private fun collectEvents() {
        collectEvent(viewModel.eventFlow, viewModel.eventConsumer) { event ->
            when (event) {
                is NewCategoryEvent.NavigateUp -> onNavigateUpEvent()
                is NewCategoryEvent.ShowColorPicker -> onShowColorPickerEvent()
            }
        }
    }

    private fun onNavigateUpEvent() {
        findNavController().navigateUp()
    }

    private fun onShowColorPickerEvent() {
        ColorPickerDialog.show(childFragmentManager) { selectedColor ->
            viewModel.handleIntent(NewCategoryIntent.AddColor(selectedColor))
        }
    }

    private fun DialogNewCategoryBinding.bindType() {
        typeGroup.setOnCheckedStateChangeListener { _, checkedId ->
            when (checkedId.first()) {
                typeExpense.id -> {
                    viewModel.handleIntent(NewCategoryIntent.ChangeType(CategoryType.EXPENSE))
                }
                typeIncome.id -> {
                    viewModel.handleIntent(NewCategoryIntent.ChangeType(CategoryType.INCOME))
                }
            }
        }

        launchSafely {
            viewModel.stateFlow.map { it.type }
                .distinctUntilChanged()
                .collect { type ->
                    when (type) {
                        CategoryType.EXPENSE -> typeGroup.check(typeExpense.id)
                        CategoryType.INCOME -> typeGroup.check(typeIncome.id)
                    }
                }
        }
    }

    private fun DialogNewCategoryBinding.bindNameInput() {
        name.doOnTextChanged { text, _, _, _ ->
            text?.toString()?.let { input ->
                viewModel.handleIntent(NewCategoryIntent.ChangeName(input))
            }
        }
        launchSafely {
            viewModel.stateFlow.map { it.type }
                .distinctUntilChanged()
                .collect { type ->
                    name.setHint(type.toNameInputHintRes())
                }
        }
    }

    private fun CategoryType.toNameInputHintRes(): Int {
        return when (this) {
            CategoryType.INCOME -> R.string.hint_category_name_income
            CategoryType.EXPENSE -> R.string.hint_category_name_expense
        }
    }

    private fun DialogNewCategoryBinding.bindIcons() {
        icons.adapter = iconAdapter
        launchSafely {
            viewModel.stateFlow.map { it.icons }
                .distinctUntilChanged()
                .collect(iconAdapter::submitList)
        }
    }

    private fun DialogNewCategoryBinding.bindColors() {
        colors.adapter = colorAdapter
        colors.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        launchSafely {
            viewModel.stateFlow.map { it.colors }
                .distinctUntilChanged()
                .collect(colorAdapter::submitList)
        }
    }

    private fun DialogNewCategoryBinding.bindToolbar() {
        toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun DialogNewCategoryBinding.bindSaveButton() {
        saveButton.setOnClickListener {
            viewModel.handleIntent(NewCategoryIntent.ClickSaveButton)
        }
        launchSafely {
            viewModel.stateFlow.map { it.saveButtonEnabled }
                .distinctUntilChanged()
                .collect { enabled ->
                    saveButton.isEnabled = enabled
                }
        }
    }

    private fun DialogNewCategoryBinding.applyWindowInsets() {
        toolbar.doOnApplyWindowInsetsCompat { toolbar, windowInsetsCompat, initialPadding ->
            toolbar.updatePadding(top = initialPadding.top + windowInsetsCompat.systemBars.top)
        }
        root.doOnApplyWindowInsetsCompat { root, windowInsetsCompat, initialPadding ->
            root.updatePadding(bottom = initialPadding.bottom + windowInsetsCompat.systemBars.bottom)
        }
    }
}