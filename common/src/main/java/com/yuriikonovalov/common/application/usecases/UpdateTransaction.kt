package com.yuriikonovalov.common.application.usecases

import android.net.Uri
import com.yuriikonovalov.common.application.Resource
import com.yuriikonovalov.common.application.entities.transaction.Transaction

interface UpdateTransaction {
    suspend operator fun invoke(newTransaction: Transaction, newImageUri: Uri?): Resource<Unit>
}