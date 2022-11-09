package com.yuriikonovalov.common.application.usecases.datasources

interface GetCategoryIconsSource {
    fun getIconPathList(): List<String>
}