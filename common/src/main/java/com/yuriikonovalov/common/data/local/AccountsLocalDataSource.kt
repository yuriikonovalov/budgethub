package com.yuriikonovalov.common.data.local

import com.yuriikonovalov.common.application.entities.account.Account
import kotlinx.coroutines.flow.Flow

interface AccountsLocalDataSource {
    suspend fun deleteAll()
    suspend fun saveAccount(account: Account): Long
    suspend fun deleteAccount(accountId: Long)
    suspend fun updateAccount(account: Account)
    suspend fun updateAccountBalance(accountId: Long, newBalance: Double)
    suspend fun getAccountById(id: Long): Account?
    fun getAllAccounts(): Flow<List<Account>>
    suspend fun getInitialBalance(accountId: Long): Double
}