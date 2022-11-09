package com.yuriikonovalov.common.presentation.addedittransaction.tag

import com.yuriikonovalov.common.application.entities.Tag

sealed class TagIntent {
    data class ChangeName(val input: String) : TagIntent()
    data class ClickTag(val tag: Tag) : TagIntent()
    data class DeleteTag(val tag: Tag) : TagIntent()
    object ClickAddButton : TagIntent()
}
