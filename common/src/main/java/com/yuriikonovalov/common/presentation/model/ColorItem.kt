package com.yuriikonovalov.common.presentation.model

data class ColorItem(
    val color: Int,
    val checked: Boolean = false,
    // Flag to set a special click listener. It's used for the last item in the list.
    val button: Boolean = false
) {
    companion object {
        fun fromColors(colors: List<Int>): List<ColorItem> {
            return colors.mapIndexed { index, color ->
                var colorItem = ColorItem(color = color)
                // Default color - should be checked.
                if (index == 0) {
                    colorItem = colorItem.copy(checked = true)
                }
                // Button color - should be checked as button.
                if (index == colors.lastIndex) {
                    colorItem = colorItem.copy(button = true)
                }
                colorItem
            }
        }
    }
}
