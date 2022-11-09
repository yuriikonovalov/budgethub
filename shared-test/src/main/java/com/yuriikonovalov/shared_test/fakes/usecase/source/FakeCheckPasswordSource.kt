package com.yuriikonovalov.shared_test.fakes.usecase.source

import com.yuriikonovalov.common.application.usecases.datasources.CheckPasswordSource

class FakeCheckPasswordSource(private val password: String? = null) : CheckPasswordSource {

    override suspend fun getPassword(): String? {
        return password
    }

}