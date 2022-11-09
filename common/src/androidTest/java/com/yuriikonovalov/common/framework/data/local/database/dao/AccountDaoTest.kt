package com.yuriikonovalov.common.framework.data.local.database.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.*
import com.yuriikonovalov.common.framework.data.local.database.AppDatabase
import com.yuriikonovalov.shared_test.model.accountDb
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalCoroutinesApi::class)
@SmallTest
@RunWith(AndroidJUnit4::class)
class AccountDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var dao: AccountDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()

        dao = database.accountDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun updateAccountBalance() = runTest {
        // BEFORE
        val account = accountDb
        val expected = 12345.0
        dao.insert(account)

        // WHEN
        dao.updateAccountBalance(account.id, expected)

        // THEN
        val actual = dao.get(account.id)?.balance
        assertThat(actual).isEqualTo(expected)
    }
}