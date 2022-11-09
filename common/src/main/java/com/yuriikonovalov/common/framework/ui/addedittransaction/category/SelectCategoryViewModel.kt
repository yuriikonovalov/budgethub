package com.yuriikonovalov.common.framework.ui.addedittransaction.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.yuriikonovalov.common.application.entities.category.Category
import com.yuriikonovalov.common.application.entities.category.CategoryType
import com.yuriikonovalov.common.application.usecases.*
import com.yuriikonovalov.common.presentation.addedittransaction.category.SelectCategoryEvent
import com.yuriikonovalov.common.presentation.addedittransaction.category.SelectCategoryIntent
import com.yuriikonovalov.common.presentation.addedittransaction.category.SelectCategoryState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SelectCategoryViewModel @AssistedInject constructor(
    @Assisted private val type: CategoryType,
    private val getIncomeCategories: GetIncomeCategories,
    private val getExpenseCategories: GetExpenseCategories,
    private val deleteCategory: DeleteCategory
) : ViewModel() {
    private val _stateFlow = MutableStateFlow(SelectCategoryState())
    private val _eventFlow = MutableStateFlow<SelectCategoryEvent?>(null)
    private val currentState get() = stateFlow.value
    val stateFlow get() = _stateFlow.asStateFlow()
    val eventFlow get() = _eventFlow.asStateFlow()
    val eventConsumer: () -> Unit = { _eventFlow.value = null }

    init {
        _stateFlow.update { it.updateType(type) }
        when (type) {
            CategoryType.INCOME -> loadIncomeCategories()
            CategoryType.EXPENSE -> loadExpenseCategories()
        }
    }

    fun handleIntent(intent: SelectCategoryIntent) {
        when (intent) {
            is SelectCategoryIntent.ClickCategory -> onClickCategory(intent.category)
            is SelectCategoryIntent.ClickNegativeButton -> onClickNegativeButton()
            is SelectCategoryIntent.ClickNewCategoryButton -> onClickNewCategoryButton()
            is SelectCategoryIntent.DeleteCategory -> onDeleteCategory(intent.id)
        }
    }

    private fun onDeleteCategory(id: Long) {
        viewModelScope.launch {
            deleteCategory(id)
        }
    }

    private fun onClickNewCategoryButton() {
        _eventFlow.value = SelectCategoryEvent.NewCategoryButtonClick(currentState.type)
    }

    private fun onClickNegativeButton() {
        _eventFlow.value = SelectCategoryEvent.NavigateBack
    }

    private fun onClickCategory(category: Category) {
        _eventFlow.value = SelectCategoryEvent.CategoryClick(category)
    }


    private fun loadExpenseCategories() {
        viewModelScope.launch {
            getExpenseCategories().collect { resource ->
                resource.onSuccess { categories ->
                    _stateFlow.update { it.updateCategories(categories) }
                }
                resource.onFailure {
                    _stateFlow.update { it.updateCategories(emptyList()) }
                }
            }
        }
    }

    private fun loadIncomeCategories() {
        viewModelScope.launch {
            getIncomeCategories().collect { resource ->
                resource.onSuccess { categories ->
                    _stateFlow.update { it.updateCategories(categories) }
                }
                resource.onFailure {
                    _stateFlow.update { it.updateCategories(emptyList()) }
                }
            }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(type: CategoryType): SelectCategoryViewModel
    }

    companion object {
        @Suppress("UNCHECKED_CAST")
        fun provideFactory(
            assistedFactory: Factory,
            type: CategoryType
        ) = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(type) as T
            }
        }
    }
}