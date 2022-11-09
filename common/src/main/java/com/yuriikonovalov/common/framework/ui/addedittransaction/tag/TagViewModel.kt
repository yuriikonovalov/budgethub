package com.yuriikonovalov.common.framework.ui.addedittransaction.tag

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.yuriikonovalov.common.application.entities.Tag
import com.yuriikonovalov.common.application.usecases.DeleteTag
import com.yuriikonovalov.common.application.usecases.GetTags
import com.yuriikonovalov.common.application.usecases.SaveTag
import com.yuriikonovalov.common.presentation.addedittransaction.tag.TagEvent
import com.yuriikonovalov.common.presentation.addedittransaction.tag.TagIntent
import com.yuriikonovalov.common.presentation.addedittransaction.tag.TagState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class TagViewModel @AssistedInject constructor(
    @Assisted private val tags: List<Tag>,
    private val getTags: GetTags,
    private val saveTag: SaveTag,
    private val deleteTag: DeleteTag
) : ViewModel() {
    private val _stateFlow = MutableStateFlow(TagState())
    private val currentState get() = stateFlow.value
    private val _eventFlow = MutableStateFlow<TagEvent?>(null)
    val stateFlow get() = _stateFlow.asStateFlow()
    val eventFlow get() = _eventFlow.asStateFlow()
    val eventConsumer: () -> Unit = { _eventFlow.value = null }

    init {
        _stateFlow.update { it.copy(selectedTags = tags) }
        loadTags()
    }

    fun handleIntent(intent: TagIntent) {
        when (intent) {
            is TagIntent.ChangeName -> onChangeName(intent.input)
            is TagIntent.ClickAddButton -> onClickAddButton()
            is TagIntent.ClickTag -> onClickTag(intent.tag)
            is TagIntent.DeleteTag -> onDeleteTag(intent.tag)
        }
    }

    private fun onClickTag(tag: Tag) {
        _stateFlow.update { it.updateSelectedTags(tag) }
        _eventFlow.value = TagEvent.ReturnSelectedTags(currentState.selectedTags)
    }

    private fun onChangeName(input: String) {
        _stateFlow.update { it.updateName(input) }
    }

    private fun onClickAddButton() {
        viewModelScope.launch {
            saveTag(currentState.toTag())
            // Clear the name after saving.
            _stateFlow.update { it.updateName("") }
            _eventFlow.value = TagEvent.ClearInput
        }
    }

    private fun onDeleteTag(tag: Tag) {
        viewModelScope.launch {
            deleteTag(tag)
            _stateFlow.update { it.updateSelectedTagsWithDeletion(tag) }
            _eventFlow.value = TagEvent.ReturnSelectedTags(currentState.selectedTags)
        }
    }

    private fun loadTags() {
        viewModelScope.launch {
            getTags().collect { resource ->
                resource.onSuccess { list ->
                    _stateFlow.update { it.updateTags(list) }
                }
                resource.onFailure {
                    _stateFlow.update { it.updateTags(emptyList()) }
                }
            }
        }
    }

    private fun TagState.toTag(): Tag {
        return Tag(name = this.name.trim())
    }

    @AssistedFactory
    interface Factory {
        fun create(tags: List<Tag>): TagViewModel
    }

    companion object {
        @Suppress("UNCHECKED_CAST")
        fun provideFactory(
            assistedFactory: Factory,
            tags: List<Tag>
        ) = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(tags) as T
            }
        }
    }
}