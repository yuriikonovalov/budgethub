package com.yuriikonovalov.common.framework.ui.addedittransaction.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.yuriikonovalov.common.R
import com.yuriikonovalov.common.application.entities.category.Category
import com.yuriikonovalov.common.application.entities.category.CategoryType
import com.yuriikonovalov.common.databinding.FragmentSelectCategoryBinding
import com.yuriikonovalov.common.framework.common.extentions.collectEvent
import com.yuriikonovalov.common.framework.common.extentions.launchSafely
import com.yuriikonovalov.common.framework.ui.addedittransaction.AddEditTransactionFragment
import com.yuriikonovalov.common.framework.utils.doOnApplyWindowInsetsCompat
import com.yuriikonovalov.common.framework.utils.systemBars
import com.yuriikonovalov.common.presentation.addedittransaction.category.SelectCategoryEvent
import com.yuriikonovalov.common.presentation.addedittransaction.category.SelectCategoryIntent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@AndroidEntryPoint
class SelectCategoryFragment : Fragment() {
    private lateinit var binding: FragmentSelectCategoryBinding
    private val args: SelectCategoryFragmentArgs by navArgs()

    @Inject
    lateinit var viewModelFactory: SelectCategoryViewModel.Factory
    private val viewModel: SelectCategoryViewModel by viewModels {
        SelectCategoryViewModel.provideFactory(viewModelFactory, args.type)
    }
    private val categoryAdapter: CategoryAdapter by lazy {
        CategoryAdapter(
            onItemClick = { category ->
                viewModel.handleIntent(SelectCategoryIntent.ClickCategory(category))
            },
            onItemLongClick = { id ->
                viewModel.handleIntent(SelectCategoryIntent.DeleteCategory(id))
            }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSelectCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.applyWindowInsets()
        binding.bindNavigationButton()
        binding.bindNewCategoryButton()
        binding.bindCategories()

        collectEvent(viewModel.eventFlow, viewModel.eventConsumer) { event ->
            when (event) {
                is SelectCategoryEvent.NavigateBack -> onNavigateBackEvent()
                is SelectCategoryEvent.CategoryClick -> onCategoryClickEvent(event.category)
                is SelectCategoryEvent.NewCategoryButtonClick -> onNewCategoryButtonClickEvent(event.type)
            }
        }
    }

    private fun onNewCategoryButtonClickEvent(type: CategoryType) {
        findNavController().navigate(SelectCategoryFragmentDirections.toNewCategoryDialog(type))
    }

    private fun onCategoryClickEvent(category: Category) {
        setFragmentResult(
            AddEditTransactionFragment.CATEGORY_REQUEST_KEY,
            bundleOf(CATEGORY_BUNDLE_KEY to category)
        )
        findNavController().navigateUp()
    }

    private fun onNavigateBackEvent() {
        findNavController().navigateUp()
    }

    private fun FragmentSelectCategoryBinding.bindCategories() {
        categories.layoutManager = GridLayoutManager(requireContext(), 3)
        categories.adapter = categoryAdapter
        launchSafely {
            viewModel.stateFlow.map { it.categories }
                .distinctUntilChanged()
                .collect(categoryAdapter::submitList)
        }
    }

    private fun FragmentSelectCategoryBinding.bindNewCategoryButton() {
        toolbar.menu.findItem(R.id.menu_action_new_category).setOnMenuItemClickListener {
            viewModel.handleIntent(SelectCategoryIntent.ClickNewCategoryButton)
            true
        }
    }

    private fun FragmentSelectCategoryBinding.bindNavigationButton() {
        toolbar.setNavigationOnClickListener {
            viewModel.handleIntent(SelectCategoryIntent.ClickNegativeButton)
        }
    }

    private fun FragmentSelectCategoryBinding.applyWindowInsets() {
        toolbar.doOnApplyWindowInsetsCompat { toolbar, windowInsetsCompat, initialPadding ->
            toolbar.updatePadding(top = initialPadding.top + windowInsetsCompat.systemBars.top)
        }
        categories.doOnApplyWindowInsetsCompat { list, windowInsetsCompat, initialPadding ->
            list.updatePadding(bottom = initialPadding.bottom + windowInsetsCompat.systemBars.bottom)
        }
    }

    companion object {
        const val TAG = "SelectCategoryFragment"
        const val CATEGORY_BUNDLE_KEY = "CATEGORY_BUNDLE_KEY"
    }
}