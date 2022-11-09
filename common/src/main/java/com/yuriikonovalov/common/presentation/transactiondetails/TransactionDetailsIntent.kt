package com.yuriikonovalov.common.presentation.transactiondetails

sealed class TransactionDetailsIntent {
    object DeleteTransaction : TransactionDetailsIntent()
    object EditTransaction : TransactionDetailsIntent()
}
