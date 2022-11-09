package com.yuriikonovalov.common.application.usecases

import android.net.Uri
import androidx.core.net.toUri
import com.yuriikonovalov.common.application.Resource
import com.yuriikonovalov.common.application.entities.transaction.Transaction
import com.yuriikonovalov.common.application.usecases.datasources.UpdateTransactionSource
import kotlinx.coroutines.flow.first
import java.io.IOException
import javax.inject.Inject

class UpdateTransactionImpl @Inject constructor(
    private val source: UpdateTransactionSource,
    private val refreshAccountBalance: RefreshAccountBalance
) : UpdateTransaction {
    override suspend operator fun invoke(
        newTransaction: Transaction,
        newImageUri: Uri?
    ): Resource<Unit> {
        return try {
            // Can't be null as updating implies changing an existing transaction.
            val oldTransaction = source.getTransaction(newTransaction.id).first()!!

            // Save the new image if it's not null.
            val imagePath = newImageUri?.let { source.saveTransactionImage(it) }
            // Update a transaction with imagePath.
            val updatedTransaction = newTransaction.copy(imagePath = imagePath)
            // Delete the old image.
            oldTransaction.imagePath?.let { source.deleteTransactionImage(it.toUri()) }

            // Update a transaction itself.
            source.updateTransaction(updatedTransaction)
            // Update the balance column in the 'accounts' table.
            refreshAccountBalance(oldTransaction.account.id)
            refreshAccountBalance(newTransaction.account.id)
            Resource.unit()
        } catch (e: IOException) {
            Resource.Failure(e)
        }
    }
}
