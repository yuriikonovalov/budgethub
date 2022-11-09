package com.yuriikonovalov.common.presentation.newcategory


sealed interface NewCategoryEvent {
    object NavigateUp : NewCategoryEvent
    object ShowColorPicker : NewCategoryEvent
}