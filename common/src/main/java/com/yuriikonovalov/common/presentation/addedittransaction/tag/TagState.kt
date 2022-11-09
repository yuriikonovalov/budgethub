package com.yuriikonovalov.common.presentation.addedittransaction.tag

import com.yuriikonovalov.common.application.entities.Tag
import com.yuriikonovalov.common.presentation.model.TagItem

data class TagState(
    val name: String = "",
    val tagItems: List<TagItem> = emptyList(),
    val selectedTags: List<Tag> = emptyList()
) {
    val addButtonEnabled get() = name.isNotBlank()
    val emptyPlaceholderVisible get() = tagItems.isEmpty()

    fun updateTags(tags: List<Tag>): TagState {
        val newTagItems = tags
            .map { TagItem(it) }
            .map {
                val selected = selectedTags.contains(it.tag)
                it.copy(checked = selected)
            }
        return copy(tagItems = newTagItems)
    }

    fun updateName(name: String): TagState {
        return copy(name = name)
    }

    fun updateSelectedTagsWithDeletion(tag: Tag): TagState {
        val newSelectedTags = if (selectedTags.contains(tag)) {
            selectedTags.minusElement(tag)
        } else {
            selectedTags
        }
        val newTagItems = tagItems.map {
            val selected = newSelectedTags.contains(it.tag)
            it.copy(checked = selected)
        }
        return copy(tagItems = newTagItems, selectedTags = newSelectedTags)
    }

    fun updateSelectedTags(tag: Tag): TagState {
        val newSelectedTags = if (selectedTags.contains(tag)) {
            selectedTags.minusElement(tag)
        } else {
            selectedTags.plusElement(tag)
        }
        val newTagItems = tagItems.map {
            val selected = newSelectedTags.contains(it.tag)
            it.copy(checked = selected)
        }
        return copy(tagItems = newTagItems, selectedTags = newSelectedTags)
    }
}

