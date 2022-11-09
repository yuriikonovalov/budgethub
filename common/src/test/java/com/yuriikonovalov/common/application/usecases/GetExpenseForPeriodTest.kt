package com.yuriikonovalov.common.application.usecases

import com.google.common.truth.Truth.*
import com.yuriikonovalov.common.application.Resource
import com.yuriikonovalov.common.application.entities.transaction.TransactionType
import com.yuriikonovalov.common.application.util.beginningOfWeek
import com.yuriikonovalov.common.application.util.endOfWeek
import com.yuriikonovalov.shared_test.fakes.usecase.source.FakeGetExpenseForPeriodSource
import com.yuriikonovalov.shared_test.model.account
import com.yuriikonovalov.shared_test.model.transaction
import com.yuriikonovalov.shared_test.model.transfer
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.time.OffsetDateTime
import java.time.ZoneOffset

@OptIn(ExperimentalCoroutinesApi::class)
class GetExpenseForPeriodTest {
    private val baseDate = OffsetDateTime.of(
        2022, 5, 15, 12, 30, 0, 0, ZoneOffset.UTC
    )
    private val accounts = listOf(
        account.copy(id = 1),
        account.copy(id = 2),
        account.copy(id = 3)
    )
    private val transactions = listOf(
        transaction.copy(
            id = 1,
            type = TransactionType.EXPENSE,
            date = baseDate,
            account = accounts[0],
            amount = 100.0
        ),
        transaction.copy(
            id = 2,
            type = TransactionType.INCOME,
            account = accounts[0],
            date = baseDate.minusMonths(1),
            amount = 100.0
        ),
        transaction.copy(
            id = 3,
            type = TransactionType.INCOME,
            account = accounts[0],
            date = baseDate.plusDays(2),
            amount = 100.0
        ),
        transaction.copy(
            id = 4,
            type = TransactionType.EXPENSE,
            account = accounts[1],
            date = baseDate.minusDays(1),
            amount = 100.0
        ),
    )
    private val transfers = listOf(
        transfer.copy(
            id = 1,
            date = baseDate.plusMonths(2),
            amountFrom = 50.0,
            accountFrom = accounts[0],
            amountTo = 65.0
        ),
        transfer.copy(
            id = 2,
            date = baseDate,
            amountFrom = 150.0,
            accountFrom = accounts[1],
            amountTo = 50.0
        ),
        transfer.copy(
            id = 3,
            date = baseDate,
            amountFrom = 50.0,
            accountFrom = accounts[1],
            amountTo = 50.0
        )
    )
    private lateinit var sut: GetExpenseForPeriodImpl

    @Before
    fun setUp() {
        val source = FakeGetExpenseForPeriodSource(transactions, transfers)
        sut = GetExpenseForPeriodImpl(source)
    }

    @Test
    fun `Get expense for the current week`() = runTest {
        // BEFORE
        val startDate = baseDate.beginningOfWeek()
        val endDate = baseDate.endOfWeek()

        // WHEN
        val actual = (sut(startDate, endDate).first() as Resource.Success)
            .data.sumOf { it.amount }

        // THEN
        // There's expenses(transfers) for this week: 150, 50.
        // Transactions (expense) for this week: 100, 100.
        assertThat(actual).isEqualTo(150.0 + 50 + 100.0 + 100.0)
    }

    @Test
    fun `Get expense for the current week filtered by accounts`() = runTest {
        // BEFORE
        val startDate = baseDate.beginningOfWeek()
        val endDate = baseDate.endOfWeek()
        val accountFilter = listOf(accounts[0])

        // WHEN
        val actual = (sut(startDate, endDate, accountFilter).first() as Resource.Success)
            .data.sumOf { it.amount }

        // THEN
        // There's no expenses(transfers) for accounts[0] for this week.
        // Transactions (expense) for accounts[0] for this week: 100.
        assertThat(actual).isEqualTo(100)
    }


    @Test
    fun `Get expense for the current week filtered by accounts - returns Resource_Failure`() =
        runTest {
            // BEFORE
            val startDate = baseDate.beginningOfWeek()
            val endDate = baseDate.endOfWeek()
            // There's no transactions nor transfers for accounts[2]
            val accountFilter = listOf(accounts[2])

            // WHEN
            val actual = sut(startDate, endDate, accountFilter).first()

            // THEN
            assertThat(actual).isInstanceOf(Resource.Failure::class.java)
        }
}