package com.yuriikonovalov.common.application.usecases

interface UpdatePasswordAuthenticationOn {
    suspend operator fun invoke(on: Boolean)
}