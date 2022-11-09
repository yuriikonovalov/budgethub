package com.yuriikonovalov.common.presentation.addedittransaction.tag

import com.yuriikonovalov.common.application.entities.Tag

sealed class TagEvent {
    object ClearInput : TagEvent()
    data class ReturnSelectedTags(val tags: List<Tag>) : TagEvent()
}