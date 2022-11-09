package com.yuriikonovalov.common.presentation.transferdetails

sealed interface TransferDetailsIntent {
    object DeleteTransfer : TransferDetailsIntent
    object EditTransfer : TransferDetailsIntent
}
