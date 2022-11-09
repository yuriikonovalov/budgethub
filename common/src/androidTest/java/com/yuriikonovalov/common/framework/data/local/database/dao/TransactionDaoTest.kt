package com.yuriikonovalov.common.framework.data.local.database.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.*
import com.yuriikonovalov.common.application.entities.transaction.TransactionType
import com.yuriikonovalov.common.framework.data.local.database.AppDatabase
import com.yuriikonovalov.shared_test.model.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.OffsetDateTime
import java.time.ZoneOffset

@OptIn(ExperimentalCoroutinesApi::class)
@SmallTest
@RunWith(AndroidJUnit4::class)
class TransactionDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var dao: TransactionDao
    private lateinit var accountDao: AccountDao
    private lateinit var tagDao: TagDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()

        dao = database.transactionDao()
        accountDao = database.accountDao()
        tagDao = database.tagDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun getTransactionsFromDate() = runTest {
        // BEFORE
        val now = OffsetDateTime.now()
        val account = accountDb
        val expected = listOf(
            transactionDb.copy(id = 1, accountId = account.id, date = now),
            transactionDb.copy(id = 2, accountId = account.id, date = now.plusDays(3))
        )
        val notExpected = listOf(
            transactionDb.copy(id = 3, accountId = account.id, date = now.minusDays(10)),
            transactionDb.copy(id = 4, accountId = account.id, date = now.minusMonths(1))
        )
        accountDao.insert(account)
        (notExpected + expected).forEach { dao.insert(it) }

        // WHEN
        val actual = dao.getTransactionsFromDate(now).first().map { it.transactionDb }

        // THEN
        assertThat(actual).containsExactlyElementsIn(expected)
    }

    @Test
    fun getTransactionAmountForAccount() = runTest {
        // BEFORE
        val accounts = accountsDb(2)
        val transactions = listOf(
            transactionDb.copy(
                id = 1, type = TransactionType.INCOME, accountId = accounts[0].id, amount = 100.0
            ),
            transactionDb.copy(
                id = 2, type = TransactionType.EXPENSE, accountId = accounts[0].id, amount = 33.0
            ),
            transactionDb.copy(
                id = 3, type = TransactionType.EXPENSE, accountId = accounts[0].id, amount = 50.0
            ),
            transactionDb.copy(
                id = 4, type = TransactionType.EXPENSE, accountId = accounts[1].id, amount = 500.0
            )
        )
        val expected = transactions.filter { it.accountId == accounts[0].id }
            .filter { it.type == TransactionType.EXPENSE }
            .sumOf { it.amount }

        accounts.forEach { accountDao.insert(it) }
        transactions.forEach { dao.insert(it) }

        // WHEN
        val actual = dao.getTransactionAmountForAccount(accounts[0].id, TransactionType.EXPENSE)

        // THEN
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun getTransactionsForPeriodByType_startAndEndPeriodsNotNull() = runTest {
        // BEFORE
        val date = OffsetDateTime.of(
            2022, 5, 15, 12, 30, 0, 0, ZoneOffset.UTC
        )
        val startPeriod = date.minusWeeks(1)
        val endPeriod = date.plusWeeks(1)
        val type = TransactionType.EXPENSE
        val account = accountDb.copy(id = 1)
        val expected = listOf(
            transactionDb.copy(
                id = 1,
                type = TransactionType.EXPENSE,
                accountId = account.id,
                date = date
            ),
            transactionDb.copy(
                id = 4,
                type = TransactionType.EXPENSE,
                accountId = account.id,
                date = date.minusDays(1)
            )
        )
        val notExpected = listOf(
            transactionDb.copy(
                id = 2,
                type = TransactionType.EXPENSE,
                accountId = account.id,
                date = date.minusMonths(1)
            ),
            transactionDb.copy(
                id = 3,
                type = TransactionType.EXPENSE,
                accountId = account.id,
                date = date.plusMonths(1)
            ),
            transactionDb.copy(
                id = 5,
                type = TransactionType.INCOME,
                accountId = account.id,
                date = date.minusDays(1)
            )
        )
        accountDao.insert(account)
        (notExpected + expected).forEach { dao.insert(it) }

        // WHEN
        val actual = dao.getTransactionsForPeriodByType(startPeriod, endPeriod, type).first()
            .map { it.transactionDb }

        // THEN
        assertThat(actual).containsExactlyElementsIn(expected)
        assertThat(actual).containsNoneIn(notExpected)
    }

    @Test
    fun getTransactionsForPeriodByType_endPeriodNull() = runTest {
        // BEFORE
        val date = OffsetDateTime.of(
            2022, 5, 15, 12, 30, 0, 0, ZoneOffset.UTC
        )
        val startPeriod = date.minusWeeks(1)
        val endPeriod = null
        val type = TransactionType.EXPENSE
        val account = accountDb.copy(id = 1)
        val expected = listOf(
            transactionDb.copy(
                id = 1,
                type = TransactionType.EXPENSE,
                accountId = account.id,
                date = date
            ),
            transactionDb.copy(
                id = 3,
                type = TransactionType.EXPENSE,
                accountId = account.id,
                date = date.plusMonths(1)
            ),
            transactionDb.copy(
                id = 4,
                type = TransactionType.EXPENSE,
                accountId = account.id,
                date = date.minusDays(1)
            )
        )
        val notExpected = listOf(
            transactionDb.copy(
                id = 2,
                type = TransactionType.EXPENSE,
                accountId = account.id,
                date = date.minusMonths(1)
            ),
            transactionDb.copy(
                id = 5,
                type = TransactionType.INCOME,
                accountId = account.id,
                date = date.minusDays(1)
            )
        )
        accountDao.insert(account)
        (expected + notExpected).forEach { dao.insert(it) }

        // WHEN
        val actual = dao.getTransactionsForPeriodByType(startPeriod, endPeriod, type).first()
            .map { it.transactionDb }

        // THEN
        assertThat(actual).containsExactlyElementsIn(expected)
        assertThat(actual).containsNoneIn(notExpected)
    }

    @Test
    fun getTransactionsForPeriodByType_startAndEndPeriodsNull() = runTest {
        // BEFORE
        val date = OffsetDateTime.of(
            2022, 5, 15, 12, 30, 0, 0, ZoneOffset.UTC
        )
        val startPeriod = null
        val endPeriod = null
        val type = TransactionType.EXPENSE
        val account = accountDb.copy(id = 1)
        val notExpected = listOf(
            transactionDb.copy(
                id = 5,
                type = TransactionType.INCOME,
                accountId = account.id,
                date = date.minusDays(1)
            )
        )
        val expected = listOf(
            transactionDb.copy(
                id = 1,
                type = TransactionType.EXPENSE,
                accountId = account.id,
                date = date
            ),
            transactionDb.copy(
                id = 2,
                type = TransactionType.EXPENSE,
                accountId = account.id,
                date = date.minusMonths(1)
            ),
            transactionDb.copy(
                id = 3,
                type = TransactionType.EXPENSE,
                accountId = account.id,
                date = date.plusMonths(1)
            ),
            transactionDb.copy(
                id = 4,
                type = TransactionType.EXPENSE,
                accountId = account.id,
                date = date.minusDays(1)
            )
        )
        accountDao.insert(account)
        (expected + notExpected).forEach { dao.insert(it) }

        // WHEN
        val actual = dao.getTransactionsForPeriodByType(startPeriod, endPeriod, type).first()
            .map { it.transactionDb }

        // THEN
        assertThat(actual).containsExactlyElementsIn(expected)
        assertThat(actual).containsNoneIn(notExpected)
    }

    @Test(expected = IllegalArgumentException::class)
    fun getTransactionsForPeriodByType_startPeriodNull_shouldThrowException() = runTest {
        // BEFORE
        val date = OffsetDateTime.of(
            2022, 5, 15, 12, 30, 0, 0, ZoneOffset.UTC
        )
        val startPeriod = null
        val endPeriod = date.plusDays(1)
        val type = TransactionType.EXPENSE
        val account = accountDb.copy(id = 1)
        val transactions = listOf(
            transactionDb.copy(
                id = 1,
                type = TransactionType.EXPENSE,
                accountId = account.id,
                date = date
            ),
            transactionDb.copy(
                id = 2,
                type = TransactionType.EXPENSE,
                accountId = account.id,
                date = date.minusMonths(1)
            ),
            transactionDb.copy(
                id = 3,
                type = TransactionType.EXPENSE,
                accountId = account.id,
                date = date.plusMonths(1)
            ),
            transactionDb.copy(
                id = 4,
                type = TransactionType.EXPENSE,
                accountId = account.id,
                date = date.minusDays(1)
            ),
            transactionDb.copy(
                id = 5,
                type = TransactionType.INCOME,
                accountId = account.id,
                date = date.minusDays(1)
            )
        )
        accountDao.insert(account)
        transactions.forEach { dao.insert(it) }

        // WHEN
        dao.getTransactionsForPeriodByType(startPeriod, endPeriod, type)
    }

    @Test
    fun insertWithTag_returnCorrectTagsWithTransaction() = runTest {
        // BEFORE
        val expected = tagsDb(2)
        val notExpected = tagsDb(3)
        val account = accountDb
        val transaction = transactionDb.copy(type = TransactionType.EXPENSE, accountId = account.id)
        (notExpected + expected).forEach { tagDao.insert(it) }
        accountDao.insert(account)

        // WHEN
        dao.insertWithTag(transaction, expected)
        val actual = dao.get(transaction.id).map { it?.tagsDb }.first()

        // THEN
        assertThat(actual).containsExactlyElementsIn(expected)
    }
}