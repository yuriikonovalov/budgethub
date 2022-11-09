package com.yuriikonovalov.common.application.usecases

import com.google.common.truth.Truth.*
import com.yuriikonovalov.common.application.Resource
import com.yuriikonovalov.common.application.entities.transaction.TransactionType
import com.yuriikonovalov.common.application.util.beginningOfMonth
import com.yuriikonovalov.common.application.util.endOfMonth
import com.yuriikonovalov.shared_test.fakes.usecase.source.FakeGetAvailableBalancesSource
import com.yuriikonovalov.shared_test.model.account
import com.yuriikonovalov.shared_test.model.transaction
import com.yuriikonovalov.shared_test.model.transfer
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test
import java.time.OffsetDateTime
import java.time.ZoneOffset

@OptIn(ExperimentalCoroutinesApi::class)
class GetAvailableBalancesUseCaseTest {
    private val baseDate = OffsetDateTime.of(
        2022, 5, 15, 12, 30, 0, 0, ZoneOffset.UTC
    )
    private val accounts = listOf(
        account.copy(id = 1, initialBalance = 1000.0),
        account.copy(id = 2, initialBalance = 0.0)
    )
    private val transfers = listOf(
        transfer.copy(
            id = 1,
            accountFrom = accounts[0],
            accountTo = accounts[1],
            date = baseDate,
            amountFrom = 33.0,
            amountTo = 33.0
        )
    )
    private val transactions = listOf(
        transaction.copy(
            id = 1,
            account = accounts[0],
            type = TransactionType.EXPENSE,
            date = baseDate.minusMonths(1).minusDays(1),
            amount = 100.0
        ),

        transaction.copy(
            id = 2,
            account = accounts[0],
            type = TransactionType.INCOME,
            date = baseDate,
            amount = 500.0
        )
    )

    @Test
    fun `Get available balances for a month`() = runTest {
        // BEFORE
        val source = FakeGetAvailableBalancesSource(accounts, transfers, transactions)
        val sut = GetAvailableBalancesImpl(source)

        // WHEN
        val availableBalances =
            sut(baseDate.beginningOfMonth(), baseDate.endOfMonth()).first() as Resource.Success

        // THEN
        // There's only 2 transactions for accounts[0] for the given period:
        //   1 expense (transfer);
        //   1 income (transaction).
        // Available balance is consisted of +1000 (initial balance), -33 (transfer),
        // -100 (transaction), +500 (transaction).
        val account0Balance = availableBalances.data.find { it.accountId == accounts[0].id }!!
        assertThat(account0Balance.endBalance).isEqualTo(1000.0 - 33.0 - 100.0 + 500.0)

        // There's only 1 transaction for accounts[1] for the given period:
        //   1 income (transfer).
        // Available balance is consisted of +0 (initial balance), +33 (transfer).
        val account1Balance = availableBalances.data.find { it.accountId == accounts[1].id }!!
        assertThat(account1Balance.endBalance).isEqualTo(0.0 + 33.0)
    }

    @Test
    fun `Get available balance for a month - returns Resource_Failure`() = runTest {
        // BEFORE
        val source = FakeGetAvailableBalancesSource(emptyList(), emptyList(), emptyList())
        val sut = GetAvailableBalancesImpl(source)

        // WHEN
        val actual = sut(baseDate.beginningOfMonth(), baseDate.endOfMonth()).first()

        // THEN
        assertThat(actual).isInstanceOf(Resource.Failure::class.java)
    }
}