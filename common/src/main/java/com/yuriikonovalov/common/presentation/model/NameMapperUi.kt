package com.yuriikonovalov.common.presentation.model

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources

object NameMapperUi {
    fun mapName(context: Context, name: String): String {
        // 'name' can contain either a custom name or a string resource id.
        // At first we consider it as a string resource id. If getString() returns null,
        // then we return the name as it is.
        return getString(context, name)
            ?: name
    }

    @SuppressLint("DiscouragedApi")
    private fun getString(context: Context, stringResourceId: String): String? {
        return try {
            val resourceId =
                context.resources.getIdentifier(stringResourceId, "string", context.packageName)
            context.resources.getString(resourceId)
        } catch (e: Resources.NotFoundException) {
            null
        }
    }
}
