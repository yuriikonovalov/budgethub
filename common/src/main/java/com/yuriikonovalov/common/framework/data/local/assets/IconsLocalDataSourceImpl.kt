package com.yuriikonovalov.common.framework.data.local.assets

import android.content.Context
import com.yuriikonovalov.common.data.local.IconsLocalDataSource
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class IconsLocalDataSourceImpl @Inject constructor(
    @ApplicationContext val context: Context
) : IconsLocalDataSource {

    override fun getCategoryIconPathsList(): List<String> {
        val assets = getAssets()
        return assets.toFullPathList()
    }

    private fun getAssets(): Array<String> {
        return try {
            context.assets.list(DIRECTORY_CATEGORY)!!
        } catch (e: Exception) {
            emptyArray()
        }
    }

    private fun Array<String>.toFullPathList(): List<String> {
        return this.map { asset ->
            ASSETS_HEADER + DIRECTORY_CATEGORY + asset
        }
    }

    companion object Directory {
        const val DIRECTORY_CATEGORY = "icons/category/"
        private const val ASSETS_HEADER = "file:///android_asset/"
    }
}