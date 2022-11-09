package com.yuriikonovalov.common.presentation.addeditaccount

import android.graphics.Color
import com.yuriikonovalov.common.application.entities.Currency
import com.yuriikonovalov.common.application.entities.account.Account
import com.yuriikonovalov.common.application.entities.account.AccountType
import com.yuriikonovalov.common.presentation.model.ColorItem
import java.time.OffsetDateTime

data class AddEditAccountState(
    val mode: Mode = Mode.ADD,
    val id: Long? = null,
    val name: String = "",
    val balance: Double = 0.0,
    val type: AccountType = AccountType.CARD,
    val currency: Currency? = null,
    val color: Int = Color.TRANSPARENT,
    val colors: List<ColorItem> = emptyList(),
    val dateOfCreation: OffsetDateTime = OffsetDateTime.now()
) {
    val card get() = Card(type, name, balance, currency, color)

    val saveButtonEnabled: Boolean get() = updateSaveButtonEnabled()
    val currencyGone: Boolean get() = mode == Mode.EDIT
    val balanceGone: Boolean get() = mode == Mode.EDIT

    private fun updateSaveButtonEnabled(): Boolean {
        return name.isNotBlank()
                && balance >= 0.0
                && currency != null
    }

    fun updateName(name: String): AddEditAccountState {
        return copy(name = name)
    }

    fun updateBalance(balance: Double): AddEditAccountState {
        return copy(balance = balance)
    }

    fun updateType(type: AccountType): AddEditAccountState {
        return copy(type = type)
    }

    fun updateCurrency(currency: Currency?): AddEditAccountState {
        return copy(currency = currency)
    }

    fun updateColor(newColor: Int): AddEditAccountState {
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

    fun addColor(newColor: Int): AddEditAccountState {
        val newColors = colors.toMutableList()
        // Uncheck the color that is already checked.
        val oldColorItem = colors.find { it.color == color }
        val oldColorIndex = colors.indexOf(oldColorItem)
        newColors[oldColorIndex] = newColors[oldColorIndex].copy(checked = false)

        val buttonColor = colors.find { it.button }
        val buttonColorIndex = colors.indexOf(buttonColor)
        val newColorItem = ColorItem(color = newColor, checked = true)
        // Add a new color item just before the add button.
        newColors.add(buttonColorIndex, newColorItem)
        return copy(color = newColor, colors = newColors)
    }

    fun updateAll(account: Account): AddEditAccountState {
        val newColors = colors.toMutableList()
        // Uncheck the color that is already checked.
        val oldColorItem = colors.find { it.color == color }
        val oldColorIndex = colors.indexOf(oldColorItem)
        newColors[oldColorIndex] = newColors[oldColorIndex].copy(checked = false)

        val newColorItem = colors.find { it.color == account.color }
        // True if the account color is in a default color list.
        val newColorItemExist = newColorItem != null
        if (newColorItemExist) {
            // If the color exists then we just check it.
            val newColorIndex = colors.indexOf(newColorItem)
            newColors[newColorIndex] = newColors[newColorIndex].copy(checked = true)
        } else {
            // If the color doesn't exist then we add one.
            newColors.add(colors.lastIndex, ColorItem(color = account.color, checked = true))
        }

        return copy(
            mode = Mode.EDIT,
            id = account.id,
            name = account.name,
            balance = account.balance,
            type = account.type,
            currency = account.currency,
            color = account.color,
            dateOfCreation = account.dateOfCreation,
            colors = newColors
        )
    }

    fun updateColors(colors: List<Int>): AddEditAccountState {
        val colorsItems = ColorItem.fromColors(colors)
        return copy(color = colorsItems.first().color, colors = colorsItems)
    }

    enum class Mode {
        ADD, EDIT
    }

    data class Card(
        val type: AccountType,
        val name: String,
        val balance: Double,
        val currency: Currency?,
        val color: Int
    )
}