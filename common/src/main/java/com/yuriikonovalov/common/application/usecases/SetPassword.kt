package com.yuriikonovalov.common.application.usecases

import com.yuriikonovalov.common.application.Resource

interface SetPassword {
    suspend operator fun invoke(password: String): Resource<Boolean>
}