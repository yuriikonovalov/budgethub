package com.yuriikonovalov.common.framework.data.local.database.datasources

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.google.common.truth.Truth.*
import com.yuriikonovalov.common.data.local.AccountsLocalDataSource
import com.yuriikonovalov.common.framework.data.local.database.AppDatabase
import com.yuriikonovalov.common.framework.data.local.database.dao.*
import com.yuriikonovalov.common.framework.data.local.database.mapper.AccountMapperDb
import com.yuriikonovalov.common.framework.data.local.database.mapper.CurrencyMapperDb
import com.yuriikonovalov.shared_test.model.account
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalCoroutinesApi::class)
@MediumTest
@RunWith(AndroidJUnit4::class)
class AccountsLocalDataSourceTest {

    private lateinit var database: AppDatabase
    private lateinit var dataSource: AccountsLocalDataSource

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()

        dataSource = AccountsLocalDataSourceImpl(
            database.accountDao(),
            AccountMapperDb((CurrencyMapperDb()))
        )
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun canSaveAccount() = runTest {
        // BEFORE
        val expected = account.copy(id = 1)

        // WHEN
        dataSource.saveAccount(expected)

        // THEN
        val actual = dataSource.getAccountById(expected.id)
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun canUpdateAccount() = runTest {
        // BEFORE
        val id = 1L
        val account = account.copy(id = id, name = "Old Account")
        dataSource.saveAccount(account)

        // WHEN
        val expected = account.copy(name = "New Account")
        dataSource.updateAccount(expected)

        // THEN
        val actual = dataSource.getAccountById(id)
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun canUpdateAccountBalance() = runTest {
        // BEFORE
        val id = 1L
        val account = account.copy(id = id, balance = 0.0)
        dataSource.saveAccount(account)

        // WHEN
        val expected = 1234.50
        dataSource.updateAccountBalance(id, expected)

        // THEN
        val actual = dataSource.getAccountById(id)?.balance
        assertThat(actual).isEqualTo(expected)
    }
}