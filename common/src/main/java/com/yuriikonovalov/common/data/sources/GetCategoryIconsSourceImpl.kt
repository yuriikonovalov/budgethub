package com.yuriikonovalov.common.data.sources

import com.yuriikonovalov.common.application.usecases.datasources.GetCategoryIconsSource
import com.yuriikonovalov.common.data.local.IconsLocalDataSource
import javax.inject.Inject

class GetCategoryIconsSourceImpl @Inject constructor(private val iconsSourceLocal: IconsLocalDataSource) :
    GetCategoryIconsSource {
    override fun getIconPathList(): List<String> {
        return iconsSourceLocal.getCategoryIconPathsList()
    }
}