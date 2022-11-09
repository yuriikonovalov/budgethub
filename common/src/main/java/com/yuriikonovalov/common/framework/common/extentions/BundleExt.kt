package com.yuriikonovalov.common.framework.common.extentions

import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import java.io.Serializable

@Suppress("UNCHECKED_CAST")
fun <T : Serializable> Bundle.getSerializableCompat(key: String, clazz: Class<T>): T? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        getSerializable(key, clazz)
    } else {
        getSerializable(key)?.let { it as T }
    }
}

fun <T : Parcelable> Bundle.getParcelableCompat(key: String, clazz: Class<T>): T? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        getParcelable(key, clazz)
    } else {
        getParcelable<T>(key)
    }
}

fun <T : Parcelable> Bundle.getParcelableArrayListCompat(
    key: String,
    clazz: Class<T>
): ArrayList<T>? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        getParcelableArrayList(key, clazz)
    } else {
        getParcelableArrayList(key)
    }
}

