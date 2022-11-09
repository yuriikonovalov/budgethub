package com.yuriikonovalov.common.application.usecases

import com.yuriikonovalov.common.application.Resource
import com.yuriikonovalov.common.application.entities.Icon

interface GetCategoryIcons {
    operator fun invoke(): Resource<List<Icon>>
}