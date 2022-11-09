package com.yuriikonovalov.common.application.usecases

import android.net.Uri
import com.yuriikonovalov.common.application.Resource
import com.yuriikonovalov.common.application.entities.transaction.Transaction

interface SaveTransaction {
    suspend operator fun invoke(transaction: Transaction, imageUri: Uri?): Resource<Unit>
}