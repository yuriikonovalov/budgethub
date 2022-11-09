package com.yuriikonovalov.shared_test.fakes.datasource

import com.yuriikonovalov.common.application.entities.account.Account
import com.yuriikonovalov.common.data.local.AccountsLocalDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeAccountsLocalDataSource : AccountsLocalDataSource {
    private val accounts = mutableListOf<Account>()
    private val accountFlow: MutableStateFlow<List<Account>> = MutableStateFlow(accounts)

    suspend fun setData(data: List<Account>) {
        accounts.clear()
        accounts.addAll(data)
        accountFlow.emit(accounts)
    }

    override suspend fun deleteAll() {
        accounts.clear()
        accountFlow.emit(accounts)
    }

    override suspend fun saveAccount(account: Account): Long {
        if (accounts.contains(account)) {
            accounts.remove(account)
        }
        accounts.add(account)
        accountFlow.emit(accounts)
        return account.id
    }

    override suspend fun deleteAccount(accountId: Long) {
        val deleted = accounts.removeIf { it.id == accountId }
        if (deleted) {
            accountFlow.emit(accounts)
        }
    }

    override suspend fun updateAccount(account: Account) {
        accounts.removeIf { it.id == account.id }.also { success ->
            if (success) {
                accounts.add(account)
                accountFlow.emit(accounts)
            }
        }
    }

    override suspend fun updateAccountBalance(accountId: Long, newBalance: Double) {
        accounts.find { it.id == accountId }?.let {
            val updatedAccount = it.copy(balance = newBalance)
            accounts.remove(it)
            accounts.add(updatedAccount)
            accountFlow.emit(accounts)
        }
    }

    override suspend fun getAccountById(id: Long): Account? {
        return accounts.find { it.id == id }
    }

    override fun getAllAccounts(): Flow<List<Account>> {
        return accountFlow
    }

    override suspend fun getInitialBalance(accountId: Long): Double {
        return accounts.find { it.id == accountId }?.initialBalance ?: 0.0
    }
}