package com.yuriikonovalov.common.application.entities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Tag(
    val id: Long = 0,
    val name: String
) : Parcelable