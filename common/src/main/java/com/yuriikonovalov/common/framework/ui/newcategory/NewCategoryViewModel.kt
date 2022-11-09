package com.yuriikonovalov.common.framework.ui.newcategory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.yuriikonovalov.common.application.entities.Icon
import com.yuriikonovalov.common.application.entities.category.Category
import com.yuriikonovalov.common.application.entities.category.CategoryType
import com.yuriikonovalov.common.application.usecases.GetCategoryIcons
import com.yuriikonovalov.common.application.usecases.GetColors
import com.yuriikonovalov.common.application.usecases.SaveCategory
import com.yuriikonovalov.common.presentation.newcategory.NewCategoryEvent
import com.yuriikonovalov.common.presentation.newcategory.NewCategoryIntent
import com.yuriikonovalov.common.presentation.newcategory.NewCategoryState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class NewCategoryViewModel @AssistedInject constructor(
    @Assisted private val type: CategoryType,
    private val saveCategory: SaveCategory,
    private val getCategoryIcons: GetCategoryIcons,
    private val getColors: GetColors
) : ViewModel() {
    private val _stateFlow = MutableStateFlow(NewCategoryState())
    private val _eventFlow = MutableStateFlow<NewCategoryEvent?>(null)
    private val currentState get() = stateFlow.value
    val stateFlow get() = _stateFlow.asStateFlow()
    val eventFlow get() = _eventFlow.asStateFlow()
    val eventConsumer: () -> Unit = { _eventFlow.value = null }

    init {
        _stateFlow.update { it.updateType(type) }
        _stateFlow.update { it.updateColors(getColors()) }

        viewModelScope.launch {
            getCategoryIcons().onSuccess { icons ->
                _stateFlow.update { it.updateIcons(icons) }
            }
        }
    }

    fun handleIntent(intent: NewCategoryIntent) {
        when (intent) {
            is NewCategoryIntent.ChangeType -> onChangeType(intent.type)
            is NewCategoryIntent.ChangeName -> onChangeName(intent.input)
            is NewCategoryIntent.ChangeIcon -> onChangeIcon(intent.icon)
            is NewCategoryIntent.ChangeColor -> onChangeColor(intent.color)
            is NewCategoryIntent.ClickSaveButton -> onClickSaveButton()
            is NewCategoryIntent.ClickColorButton -> onClickColorButton()
            is NewCategoryIntent.AddColor -> onAddColor(intent.color)
        }
    }

    private fun onAddColor(color: Int) {
        _stateFlow.update { it.addColor(color) }
    }

    private fun onChangeType(type: CategoryType) {
        _stateFlow.update { it.updateType(type) }
    }

    private fun onChangeName(name: String) {
        _stateFlow.update { it.updateName(name) }
    }

    private fun onChangeIcon(icon: Icon) {
        _stateFlow.update { it.updateIcon(icon) }
    }

    private fun onChangeColor(color: Int) {
        _stateFlow.update { it.updateColor(color) }
    }

    private fun onClickColorButton() {
        _eventFlow.value = NewCategoryEvent.ShowColorPicker
    }

    private fun onClickSaveButton() {
        viewModelScope.launch {
            val resource = saveCategory(currentState.toCategory())
            resource.onSuccess {
                _eventFlow.value = NewCategoryEvent.NavigateUp
            }
        }
    }

    private fun NewCategoryState.toCategory(): Category {
        return Category(
            id = 0,
            type = this.type,
            name = this.name.trim(),
            iconPath = icon!!.path,
            color = this.color,
            isCustom = true,
        )
    }

    @AssistedFactory
    interface Factory {
        fun create(type: CategoryType): NewCategoryViewModel
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