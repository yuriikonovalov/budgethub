package com.yuriikonovalov.common.framework.data.local.database.dao

import androidx.room.*
import com.yuriikonovalov.common.framework.data.local.database.model.AccountDb
import kotlinx.coroutines.flow.Flow

@Dao
interface AccountDao {
    @Insert
    suspend fun insert(account: AccountDb): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(account: AccountDb)

    @Query(
        "UPDATE accounts " +
                "SET balance = :newBalance " +
                "WHERE id=:accountId"
    )
    suspend fun updateAccountBalance(accountId: Long, newBalance: Double)

    @Query("SELECT * FROM accounts WHERE id=:id")
    suspend fun get(id: Long): AccountDb?

    @Query("SELECT initial_balance FROM accounts WHERE id=:id")
    suspend fun getInitialBalance(id: Long): Double?

    @Query("SELECT * FROM accounts")
    fun getAll(): Flow<List<AccountDb>>

    @Query("DELETE FROM accounts WHERE id = :accountId")
    suspend fun delete(accountId: Long)

    @Query("DELETE FROM accounts")
    suspend fun deleteAll()
}