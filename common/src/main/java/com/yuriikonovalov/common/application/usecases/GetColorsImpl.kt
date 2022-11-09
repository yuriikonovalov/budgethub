package com.yuriikonovalov.common.application.usecases

import android.graphics.Color
import javax.inject.Inject

class GetColorsImpl @Inject constructor() : GetColors {
    override fun invoke() = listOf(
        Color.parseColor("#EDFB76"),
        Color.parseColor("#B1345C"),
        Color.parseColor("#8234CB"),
        Color.parseColor("#CEE5D7"),
        Color.parseColor("#A11CAB"),
        Color.parseColor("#FDBD2C"),
        Color.parseColor("#2F468D"),
        Color.parseColor("#707D4E"),
        Color.TRANSPARENT //Button color.
    )
}