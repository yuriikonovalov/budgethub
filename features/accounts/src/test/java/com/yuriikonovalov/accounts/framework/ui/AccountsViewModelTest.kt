package com.yuriikonovalov.accounts.framework.ui

import com.google.common.truth.Truth.*
import com.yuriikonovalov.accounts.presentation.AccountsIntent
import com.yuriikonovalov.accounts.util.CoroutineRule
import com.yuriikonovalov.accounts.util.account
import com.yuriikonovalov.accounts.util.accounts
import com.yuriikonovalov.common.application.Resource
import com.yuriikonovalov.common.application.usecases.DeleteAccount
import com.yuriikonovalov.common.application.usecases.GetAllAccounts
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.kotlin.*

@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
class AccountsViewModelTest {
    @get:Rule
    val coroutineRule = CoroutineRule()

    private val getAllAccounts: GetAllAccounts = mock()
    private val deleteAccount: DeleteAccount = mock()

    @Test
    fun `if there's no accounts - placeholder is visible`() {
        // BEFORE
        whenever(getAllAccounts.invoke()).doReturn(flowOf(Resource.Failure()))
        val sut = AccountsViewModel(
            getAllAccounts, deleteAccount, coroutineRule.testDispatcherProvider
        )

        // WHEN
        coroutineRule.testDispatcher.scheduler.runCurrent()

        // THEN
        val actual = sut.stateFlow.value.placeholderVisible
        assertThat(actual).isTrue()
    }

    @Test
    fun `if there's accounts - placeholder is not visible`() {
        // BEFORE
        whenever(getAllAccounts.invoke()).doReturn(flowOf(Resource.Success(accounts(2))))
        val sut = AccountsViewModel(
            getAllAccounts, deleteAccount, coroutineRule.testDispatcherProvider
        )

        // WHEN
        coroutineRule.testDispatcher.scheduler.runCurrent()

        // THEN
        val actual = sut.stateFlow.value.placeholderVisible
        assertThat(actual).isFalse()
    }

    @Test
    fun `if there's at least one account - a create button is visible`() {
        // BEFORE
        whenever(getAllAccounts.invoke()).doReturn(flowOf(Resource.Success(accounts(1))))
        val sut = AccountsViewModel(
            getAllAccounts, deleteAccount, coroutineRule.testDispatcherProvider
        )

        // WHEN
        coroutineRule.testDispatcher.scheduler.runCurrent()

        // THEN
        val actual = sut.stateFlow.value.createButtonVisible
        assertThat(actual).isTrue()
    }


    @Test
    fun `if there's only one account - a transfer button is not visible`() {
        // BEFORE
        whenever(getAllAccounts.invoke()).doReturn(flowOf(Resource.Success(accounts(1))))
        val sut = AccountsViewModel(
            getAllAccounts, deleteAccount, coroutineRule.testDispatcherProvider
        )

        // WHEN
        coroutineRule.testDispatcher.scheduler.runCurrent()

        // THEN
        val actual = sut.stateFlow.value.transferButtonVisible
        assertThat(actual).isFalse()
    }


    @Test
    fun `if there's more than one account - a transfer button is visible`() {
        // BEFORE
        whenever(getAllAccounts.invoke()).doReturn(flowOf(Resource.Success(accounts(2))))
        val sut = AccountsViewModel(
            getAllAccounts, deleteAccount, coroutineRule.testDispatcherProvider
        )

        // WHEN
        coroutineRule.testDispatcher.scheduler.runCurrent()

        // THEN
        val actual = sut.stateFlow.value.transferButtonVisible
        assertThat(actual).isTrue()
    }


    @Test
    fun `if delete account - delete account use case invoked with a correct id`() = runTest {
        // BEFORE
        val account = account
        whenever(getAllAccounts.invoke()).doReturn(flowOf(Resource.Success(listOf(account))))
        val sut = AccountsViewModel(
            getAllAccounts, deleteAccount, coroutineRule.testDispatcherProvider
        )

        // WHEN
        runCurrent()
        sut.handleIntent(AccountsIntent.DeleteAccount(account.id))
        advanceUntilIdle()

        // THEN
        verify(deleteAccount, times(1)).invoke(account.id)
    }
}