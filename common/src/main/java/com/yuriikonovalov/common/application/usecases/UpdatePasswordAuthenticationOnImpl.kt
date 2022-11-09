package com.yuriikonovalov.common.application.usecases

import com.yuriikonovalov.common.application.usecases.datasources.UpdatePasswordAuthenticationOnSource
import javax.inject.Inject

class UpdatePasswordAuthenticationOnImpl @Inject constructor(
    private val source: UpdatePasswordAuthenticationOnSource
) : UpdatePasswordAuthenticationOn {
    override suspend operator fun invoke(on: Boolean) {
        source.updatePasswordAuthenticationOn(on)
    }
}