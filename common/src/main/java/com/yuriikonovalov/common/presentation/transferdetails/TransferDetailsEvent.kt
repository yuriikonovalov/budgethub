package com.yuriikonovalov.common.presentation.transferdetails

sealed class TransferDetailsEvent {
    object NavigateUp : TransferDetailsEvent()
    data class EditTransfer(val transferId: Long) : TransferDetailsEvent()
}
