package com.yuriikonovalov.common.framework.common.extentions

import android.content.res.ColorStateList
import android.content.res.Resources
import android.widget.ImageView
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.math.round


fun ImageView.setTintResource(@ColorRes id: Int) {
    val color = ContextCompat.getColor(context, id)
    setTintColor(color)
}

fun ImageView.setTintColor(@ColorInt color: Int) {
    imageTintList = ColorStateList.valueOf(color)
}

inline fun AppCompatActivity.launchSafely(crossinline body: suspend () -> Unit) {
    lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            body()
        }
    }
}

inline fun Fragment.launchSafely(crossinline body: suspend () -> Unit) {
    viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            body()
        }
    }
}

inline fun <T : Any> Fragment.collectEvent(
    eventFlow: StateFlow<T?>,
    crossinline eventConsumer: () -> Unit,
    crossinline onCollect: (value: T) -> Unit,
) {
    viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            eventFlow.filterNotNull()
                .collect {
                    onCollect(it)
                    eventConsumer.invoke()
                }
        }
    }
}

/**
 * Converts Pixel to DP.
 */
@Suppress("unused")
val Int.pxToDp: Int
    get() = (this / Resources.getSystem().displayMetrics.density).toInt()

/**
 * Converts DP to Pixel.
 */
@Suppress("unused")
val Int.dpToPx: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()


fun Double.roundDecimals(decimals: Int): Double {
    var multiplier = 1.0
    repeat(decimals) { multiplier *= 10 }
    return round(this * multiplier) / multiplier
}