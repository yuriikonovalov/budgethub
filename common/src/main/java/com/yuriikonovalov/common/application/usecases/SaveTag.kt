package com.yuriikonovalov.common.application.usecases

import com.yuriikonovalov.common.application.Resource
import com.yuriikonovalov.common.application.entities.Tag

interface SaveTag {
    suspend operator fun invoke(tag: Tag): Resource<Unit>
}