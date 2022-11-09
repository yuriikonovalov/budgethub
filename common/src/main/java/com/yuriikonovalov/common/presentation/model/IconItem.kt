package com.yuriikonovalov.common.presentation.model

import com.yuriikonovalov.common.application.entities.Icon

data class IconItem(
    val icon: Icon,
    val checked: Boolean = false
)
