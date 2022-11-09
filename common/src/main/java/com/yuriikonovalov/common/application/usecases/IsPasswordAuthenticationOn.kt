package com.yuriikonovalov.common.application.usecases

import kotlinx.coroutines.flow.Flow

interface IsPasswordAuthenticationOn {
    operator fun invoke(): Flow<Boolean>
}