package com.yuriikonovalov.common.application.usecases

interface DeleteAccount {
    suspend operator fun invoke(id: Long)
}