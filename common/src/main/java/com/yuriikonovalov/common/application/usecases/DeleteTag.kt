package com.yuriikonovalov.common.application.usecases

import com.yuriikonovalov.common.application.entities.Tag

interface DeleteTag {
    suspend operator fun invoke(tag: Tag)
}