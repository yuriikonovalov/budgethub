package com.yuriikonovalov.common.application.usecases

import com.google.common.truth.Truth.*
import com.yuriikonovalov.common.application.Resource
import com.yuriikonovalov.shared_test.fakes.usecase.source.FakeGetAccountSource
import com.yuriikonovalov.shared_test.model.account
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class GetAccountTest {

    @Test
    fun `not existing account id returns Error`() = runTest {
        // BEFORE
        val source = FakeGetAccountSource(account)
        val sut = GetAccountImpl(source)

        // WHEN
        val actual = sut(1345)

        // THEN
        assertThat(actual).isInstanceOf(Resource.Failure::class.java)
    }

    @Test
    fun `existing account id returns Success `() = runTest {
        // BEFORE
        val source = FakeGetAccountSource(account)
        val sut = GetAccountImpl(source)

        // WHEN
        val actual = sut(account.id)

        // THEN
        assertThat(actual).isInstanceOf(Resource.Success::class.java)
    }

    @Test
    fun `IO exception returns Error with a type DatabaseException`() = runTest {
        // BEFORE
        val source = FakeGetAccountSource(account, IOException())
        val sut = GetAccountImpl(source)

        // WHEN
        val actual = (sut(account.id) as Resource.Failure).error

        // THEN
        assertThat(actual).isInstanceOf(IOException::class.java)
    }

}