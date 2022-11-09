package com.yuriikonovalov.common.application.usecases

import android.net.Uri
import com.yuriikonovalov.common.application.Resource
import com.yuriikonovalov.common.application.entities.transaction.Transaction
import com.yuriikonovalov.common.application.usecases.datasources.SaveTransactionSource
import java.io.IOException
import javax.inject.Inject

class SaveTransactionImpl @Inject constructor(
    private val source: SaveTransactionSource,
    private val refreshAccountBalance: RefreshAccountBalance
) : SaveTransaction {
    override suspend operator fun invoke(transaction: Transaction, imageUri: Uri?): Resource<Unit> {
        return try {
            // Save an image if it's not null.
            val imagePath = imageUri?.let { source.saveTransactionImage(it) }
            // Update a transaction with imagePath.
            val transactionToSave = transaction.copy(imagePath = imagePath)
            // Receive a transaction id as it is generated by Room. The id is needed for an AccountRecord.
            source.saveTransaction(transactionToSave)
            // Update the account after saving transaction
            refreshAccountBalance(transaction.account.id)
            Resource.unit()
        } catch (e: IOException) {
            Resource.Failure(e)
        }
    }
}