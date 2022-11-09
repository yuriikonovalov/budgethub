package com.yuriikonovalov.common.application.usecases

import com.yuriikonovalov.common.application.usecases.datasources.DeleteAccountSource
import javax.inject.Inject

class DeleteAccountImpl @Inject constructor(
    private val source: DeleteAccountSource
) : DeleteAccount {
    override suspend operator fun invoke(id: Long) {
        source.deleteAccount(id)
    }
}