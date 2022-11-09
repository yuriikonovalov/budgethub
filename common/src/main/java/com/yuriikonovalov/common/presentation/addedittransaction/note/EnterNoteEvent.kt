package com.yuriikonovalov.common.presentation.addedittransaction.note

sealed class EnterNoteEvent {
    data class PositiveButtonClick(val input: String?) : EnterNoteEvent()
    data class SetText(val input: String?) : EnterNoteEvent()
    object NegativeButtonClick : EnterNoteEvent()
}