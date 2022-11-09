package com.yuriikonovalov.common.application.usecases

import com.yuriikonovalov.common.application.Resource
import com.yuriikonovalov.common.application.entities.Icon
import com.yuriikonovalov.common.application.usecases.datasources.GetCategoryIconsSource
import javax.inject.Inject

class GetCategoryIconsImpl @Inject constructor(
    private val source: GetCategoryIconsSource
) : GetCategoryIcons {
    override operator fun invoke(): Resource<List<Icon>> {
        val icons = source.getIconPathList().map(::Icon)
        return Resource.successIfNotEmpty(icons)
    }
}

