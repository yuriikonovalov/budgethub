package com.yuriikonovalov.common.presentation.model

import com.yuriikonovalov.common.application.entities.Tag

data class TagItem(
    val tag: Tag,
    val checked: Boolean = false
)
