package com.yuriikonovalov.common.application.usecases

interface DeleteCategory {
    suspend operator fun invoke(id: Long)
}