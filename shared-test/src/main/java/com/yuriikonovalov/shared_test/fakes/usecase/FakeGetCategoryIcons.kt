package com.yuriikonovalov.shared_test.fakes.usecase

import com.yuriikonovalov.common.application.Resource
import com.yuriikonovalov.common.application.entities.Icon
import com.yuriikonovalov.common.application.usecases.GetCategoryIcons
import com.yuriikonovalov.shared_test.model.icons

class FakeGetCategoryIcons(
    private val icons: List<Icon> = icons(5)
) : GetCategoryIcons {
    override fun invoke(): Resource<List<Icon>> {
        return Resource.Success(icons)
    }
}