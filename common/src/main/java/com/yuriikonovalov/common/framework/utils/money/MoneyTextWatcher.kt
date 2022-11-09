package com.yuriikonovalov.common.framework.utils.money

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import java.text.ParseException
import kotlin.math.min

class MoneyTextWatcher(private val editText: EditText) : TextWatcher {
    private val wholeNumberDecimalFormat = MoneyFormat.Formatter.wholeNumberDecimalFormat
    private val fractionDecimalFormat = MoneyFormat.Formatter.fractionDecimalFormat
    private val numberOfDecimalDigits = MoneyFormat.Formatter.numberOfDecimalDigits
    private val decimalSeparator = MoneyFormat.Formatter.decimalSeparator
    private val groupingSeparator = MoneyFormat.Formatter.groupingSeparator

    private var hasDecimalSeparator = false

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
        fractionDecimalFormat.isDecimalSeparatorAlwaysShown = true
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        hasDecimalSeparator = s.toString().contains(decimalSeparator)
    }

    override fun afterTextChanged(editableInput: Editable) {
        val input = editableInput.toString()
        if (input.isParsable().not()) {
            return
        }

        editText.removeTextChangedListener(this)
        try {
            val startLength = editText.text.length
            val inputWithoutGroupSeparator = input.eliminateGroupSeparator()
            val inputWithoutGroupSeparatorTrunkedDecimals =
                inputWithoutGroupSeparator.trunkDecimalDigits()

            val selectionStartIndex = editText.selectionStart
            val parsedNumber =
                fractionDecimalFormat.parse(inputWithoutGroupSeparatorTrunkedDecimals)

            if (hasDecimalSeparator) {
                val numberOfDecimalDigits =
                    inputWithoutGroupSeparatorTrunkedDecimals.countDecimalDigits()
                val formatterPattern = getFractionFormatterPattern(numberOfDecimalDigits)
                fractionDecimalFormat.applyPattern(formatterPattern)
                editText.setText(fractionDecimalFormat.format(parsedNumber))
            } else {
                editText.setText(wholeNumberDecimalFormat.format(parsedNumber))
            }

            val endLength = editText.text.length
            val selection = selectionStartIndex + (endLength - startLength)

            if (selection > 0 && selection <= editText.text.length) {
                editText.setSelection(selection)
            } else {
                editText.setSelection(editText.text.length - 1)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        editText.addTextChangedListener(this)
    }


    private fun String.isParsable(): Boolean {
        return try {
            fractionDecimalFormat.parse(this)
            true
        } catch (e: ParseException) {
            false
        }
    }

    private fun String.eliminateGroupSeparator(): String {
        return this.replace(groupingSeparator, "")
    }

    private fun String.trunkDecimalDigits(
        separator: String = decimalSeparator,
        numberOfDigits: Int = numberOfDecimalDigits,
    ): String {
        val number = this.split(separator)
            .toMutableList()

        if (number.size != 2) {
            return this
        }

        number[1] = number[1].take(numberOfDigits)
        return number.joinToString(separator = separator)
    }


    private fun String.countDecimalDigits(): Int {
        val indexOfSeparator = this.indexOf(decimalSeparator)
        return this.lastIndex - indexOfSeparator
    }

    private fun getFractionFormatterPattern(numberOfCharactersAfterSeparator: Int): String {
        val decimalCharacters =
            "0".repeat(min(numberOfCharactersAfterSeparator, numberOfDecimalDigits))
        return MoneyFormat.Formatter.FRACTION_FORMAT_PATTERN + decimalCharacters
    }
}