package com.yuriikonovalov.common.framework.utils

import androidx.test.espresso.IdlingResource


interface EspressoIdlingResource {
    val instance: IdlingResource
    fun increment()
    fun decrement()
}