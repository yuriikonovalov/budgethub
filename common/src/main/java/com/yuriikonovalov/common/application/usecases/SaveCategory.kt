package com.yuriikonovalov.common.application.usecases

import com.yuriikonovalov.common.application.Resource
import com.yuriikonovalov.common.application.entities.category.Category

interface SaveCategory {
    suspend operator fun invoke(category: Category): Resource<Unit>
}