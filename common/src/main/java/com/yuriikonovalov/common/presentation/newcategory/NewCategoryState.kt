package com.yuriikonovalov.common.presentation.newcategory

import android.graphics.Color
import com.yuriikonovalov.common.application.entities.Icon
import com.yuriikonovalov.common.application.entities.category.CategoryType
import com.yuriikonovalov.common.presentation.model.ColorItem
import com.yuriikonovalov.common.presentation.model.IconItem

data class NewCategoryState(
    val name: String = "",
    val type: CategoryType = CategoryType.EXPENSE,
    val icon: Icon? = null,
    val icons: List<IconItem> = emptyList(),
    val color: Int = Color.TRANSPARENT,
    val colors: List<ColorItem> = emptyList()
) {
    val saveButtonEnabled: Boolean
        get() = name.isNotBlank() && icon != null

    fun updateName(name: String): NewCategoryState {
        return copy(name = name)
    }

    fun updateType(type: CategoryType): NewCategoryState {
        return copy(type = type)
    }

    fun updateIcon(newIcon: Icon): NewCategoryState {
        if (newIcon == icon) {
            return copy()
        }
        val oldIcon = this.icon
        val oldIconItem = icons.find { it.icon.path == oldIcon?.path }
        val newIconItem = icons.find { it.icon.path == newIcon.path }
        val oldIconIndex = icons.indexOf(oldIconItem)
        val newIconIndex = icons.indexOf(newIconItem)

        val newIcons = icons.toMutableList()
        if (this.icon == null) {
            // Happens when it is the first click on an item.
            // There's no selected icon by default so the oldIconIndex will be -1 in this case.
            newIcons[newIconIndex] = newIcons[newIconIndex].copy(checked = true)
        } else {
            newIcons[oldIconIndex] = newIcons[oldIconIndex].copy(checked = false)
            newIcons[newIconIndex] = newIcons[newIconIndex].copy(checked = true)
        }
        return copy(icon = newIcon, icons = newIcons)
    }

    fun updateColor(newColor: Int): NewCategoryState {
        if (newColor == color) {
            return copy()
        }
        val newColors = colors.toMutableList()
        // Uncheck the color that is already checked.
        val oldColorItem = colors.find { it.color == color }
        val oldColorIndex = colors.indexOf(oldColorItem)
        newColors[oldColorIndex] = newColors[oldColorIndex].copy(checked = false)

        // Check the new color.
        val newColorItem = colors.find { it.color == newColor }
        val newColorIndex = colors.indexOf(newColorItem)
        newColors[newColorIndex] = newColors[newColorIndex].copy(checked = true)

        return copy(color = newColor, colors = newColors)
    }

    fun addColor(newColor: Int): NewCategoryState {
        val buttonColor = colors.find { it.button }
        val buttonColorIndex = colors.indexOf(buttonColor)

        val oldColor = this.color
        val oldColorItem = colors.find { it.color == oldColor }
        val oldColorIndex = colors.indexOf(oldColorItem)

        val newColors = colors.toMutableList()
        newColors[oldColorIndex] = newColors[oldColorIndex].copy(checked = false)
        val newColorItem = ColorItem(color = newColor, checked = true)
        // Inset a new color item just before the add button.
        newColors.add(buttonColorIndex, newColorItem)
        return copy(color = newColor, colors = newColors)
    }

    fun updateIcons(icons: List<Icon>): NewCategoryState {
        val newIcons = icons.map { IconItem(it) }
        return copy(icons = newIcons)
    }

    fun updateColors(colors: List<Int>): NewCategoryState {
        val colorItems = ColorItem.fromColors(colors)
        return copy(color = colorItems.first().color, colors = colorItems)
    }
}