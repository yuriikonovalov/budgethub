package com.yuriikonovalov.common.application.usecases

import com.google.common.truth.Truth.*
import com.yuriikonovalov.common.application.Resource
import com.yuriikonovalov.shared_test.fakes.usecase.source.FakeCheckPasswordSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CheckPasswordTest {

    @Test
    fun `if the data source returns null - returns false`() = runTest {
        // BEFORE
        val source = FakeCheckPasswordSource()
        val sut = CheckPasswordImpl(source)

        // WHEN
        val actual = sut("password") as Resource.Success

        // THEN
        // Source returns null (there's no saved password).
        // For any given password the result should be false.
        assertThat(actual.data).isFalse()
    }

    @Test
    fun `if a password is correct - returns true`() = runTest {
        // BEFORE
        val password = "1234qwerty"
        val source = FakeCheckPasswordSource(password)
        val sut = CheckPasswordImpl(source)

        // WHEN
        val actual = sut(password) as Resource.Success

        // THEN
        assertThat(actual.data).isTrue()
    }

    @Test
    fun `if a password is incorrect - returns false`() = runTest {
        // BEFORE
        val password = "1234qwerty"
        val source = FakeCheckPasswordSource(password)
        val sut = CheckPasswordImpl(source)

        // WHEN
        val actual = sut("testpassword") as Resource.Success

        // THEN
        assertThat(actual.data).isFalse()
    }
}