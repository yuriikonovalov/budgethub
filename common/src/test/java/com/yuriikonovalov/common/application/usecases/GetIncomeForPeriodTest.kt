package com.yuriikonovalov.common.application.usecases

import com.google.common.truth.Truth.*
import com.yuriikonovalov.common.application.Resource
import com.yuriikonovalov.common.application.entities.transaction.TransactionType
import com.yuriikonovalov.common.application.util.beginningOfWeek
import com.yuriikonovalov.common.application.util.endOfWeek
import com.yuriikonovalov.shared_test.fakes.usecase.source.FakeGetIncomeForPeriodSource
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
class GetIncomeForPeriodTest {
    private val baseDate = OffsetDateTime.of(
        2022, 5, 15, 12, 30, 0, 0, ZoneOffset.UTC
    )
    private val accounts = listOf(
        account.copy(id = 1),
        account.copy(id = 2),
        account.copy(id = 3),
        account.copy(id = 4)
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
            amount = 177.0
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
            amountTo = 50.0,
            accountTo = accounts[0]
        ),
        transfer.copy(
            id = 3,
            date = baseDate,
            amountFrom = 50.0,
            accountFrom = accounts[1],
            amountTo = 50.0,
            accountTo = accounts[3]
        )
    )
    private lateinit var sut: GetIncomeForPeriodImpl

    @Before
    fun setUp() {
        val source = FakeGetIncomeForPeriodSource(transactions, transfers)
        sut = GetIncomeForPeriodImpl(source)
    }

    @Test
    fun `Get income for the current week`() = runTest {
        // BEFORE
        val startDate = baseDate.beginningOfWeek()
        val endDate = baseDate.endOfWeek()

        // WHEN
        val actual = (sut(startDate, endDate).first() as Resource.Success)
            .data.sumOf { it.amount }

        // THEN
        // Incomes(transfers) for this week: 50, 50.
        assertThat(actual).isEqualTo(50 + 50)
    }

    @Test
    fun `Get income for the current week filtered by accounts`() = runTest {
        // BEFORE
        val startDate = baseDate.beginningOfWeek()
        val endDate = baseDate.endOfWeek()
        val accountFilter = listOf(accounts[0])

        // WHEN
        val actual = (sut(startDate, endDate, accountFilter).first() as Resource.Success)
            .data.sumOf { it.amount }

        // THEN
        // Incomes (transfers) for accounts[0] for this week: 50.
        assertThat(actual).isEqualTo(50.0)
    }


    @Test
    fun `Get income for the current week filtered by accounts - returns Resource_Failure`() =
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