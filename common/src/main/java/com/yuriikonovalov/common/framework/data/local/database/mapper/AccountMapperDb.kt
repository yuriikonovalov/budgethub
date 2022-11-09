package com.yuriikonovalov.common.framework.data.local.database.mapper

import com.yuriikonovalov.common.application.entities.account.Account
import com.yuriikonovalov.common.framework.data.local.database.model.AccountDb
import javax.inject.Inject

class AccountMapperDb @Inject constructor(
    private val currencyMapperDb: CurrencyMapperDb
) : MapperDb<AccountDb, Account> {
    override fun mapToDomain(db: AccountDb): Account {
        return Account(
            id = db.id,
            name = db.name,
            type = db.type,
            currency = currencyMapperDb.mapToDomain(db.currency),
            initialBalance = db.initialBalance,
            balance = db.balance,
            dateOfCreation = db.dateOfCreation,
            color = db.color
        )
    }

    override fun mapFromDomain(domain: Account): AccountDb {
        return AccountDb(
            id = domain.id,
            name = domain.name,
            type = domain.type,
            currency = currencyMapperDb.mapFromDomain(domain.currency),
            initialBalance = domain.initialBalance,
            balance = domain.balance,
            dateOfCreation = domain.dateOfCreation,
            color = domain.color
        )
    }
}