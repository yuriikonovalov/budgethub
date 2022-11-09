package com.yuriikonovalov.common.presentation.transactiondetails

sealed class TransactionDetailsEvent {
    object NavigateUp : TransactionDetailsEvent()
    data class EditTransaction(val transactionId: Long) : TransactionDetailsEvent()
}
