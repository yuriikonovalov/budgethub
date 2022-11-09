package com.yuriikonovalov.common.presentation.addedittransaction.note

sealed class EnterNoteIntent {
    data class ChangeInput(val input: String) : EnterNoteIntent()
    object ClickPositiveButton : EnterNoteIntent()
    object ClickNegativeButton : EnterNoteIntent()
}


