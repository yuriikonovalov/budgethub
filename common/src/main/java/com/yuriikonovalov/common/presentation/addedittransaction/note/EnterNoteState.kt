package com.yuriikonovalov.common.presentation.addedittransaction.note

data class EnterNoteState(
    val input: String? = null
) {
    fun updateInput(input: String?): EnterNoteState {
        val formattedInput = input?.ifBlank { null }
        return copy(input = formattedInput)
    }
}