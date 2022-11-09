package com.yuriikonovalov.common.framework.data.local.database.datasources

import com.yuriikonovalov.common.application.entities.account.Account
import com.yuriikonovalov.common.data.local.AccountsLocalDataSource
import com.yuriikonovalov.common.framework.data.local.database.dao.AccountDao
import com.yuriikonovalov.common.framework.data.local.database.mapper.AccountMapperDb
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AccountsLocalDataSourceImpl @Inject constructor(
    private val accountDao: AccountDao,
    private val accountMapperDb: AccountMapperDb
) : AccountsLocalDataSource {

    override suspend fun deleteAll() {
        accountDao.deleteAll()
    }

    override suspend fun saveAccount(account: Account): Long {
        val accountDb = accountMapperDb.mapFromDomain(account)
        return accountDao.insert(accountDb)
    }

    override suspend fun deleteAccount(accountId: Long) {
        accountDao.delete(accountId)
    }

    override suspend fun updateAccount(account: Account) {
        val accountDb = accountMapperDb.mapFromDomain(account)
        accountDao.update(accountDb)
    }

    override suspend fun updateAccountBalance(accountId: Long, newBalance: Double) {
        accountDao.updateAccountBalance(accountId, newBalance)
    }

    override suspend fun getAccountById(id: Long): Account? {
        return accountDao.get(id)?.let {
            accountMapperDb.mapToDomain(it)
        }
    }

    override fun getAllAccounts(): Flow<List<Account>> {
        return accountDao.getAll().map { accountsDb ->
            accountsDb.map { accountDb ->
                accountMapperDb.mapToDomain(accountDb)
            }
        }
    }

    override suspend fun getInitialBalance(accountId: Long): Double {
        return accountDao.getInitialBalance(accountId) ?: 0.0
    }
}