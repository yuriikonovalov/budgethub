package com.yuriikonovalov.shared_test.fakes.usecase

import android.graphics.Color
import com.yuriikonovalov.common.application.usecases.GetColors

class FakeGetColors(
    private val colors: List<Int> = listOf(
        Color.BLACK,
        Color.YELLOW,
        Color.GRAY
    )
) : GetColors {
    override fun invoke() = colors
}