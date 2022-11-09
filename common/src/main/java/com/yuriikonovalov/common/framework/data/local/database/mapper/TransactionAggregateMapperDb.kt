package com.yuriikonovalov.common.framework.data.local.database.mapper

import com.yuriikonovalov.common.application.entities.Tag
import com.yuriikonovalov.common.application.entities.account.Account
import com.yuriikonovalov.common.application.entities.category.Category
import com.yuriikonovalov.common.application.entities.transaction.Transaction
import com.yuriikonovalov.common.framework.data.local.database.model.AccountDb
import com.yuriikonovalov.common.framework.data.local.database.model.CategoryDb
import com.yuriikonovalov.common.framework.data.local.database.model.TagDb
import com.yuriikonovalov.common.framework.data.local.database.model.TransactionDb
import com.yuriikonovalov.common.framework.data.local.database.model.relations.TransactionAggregateDb
import javax.inject.Inject

class TransactionAggregateMapperDb @Inject constructor(
    private val categoryMapperDb: CategoryMapperDb,
    private val accountMapperDb: AccountMapperDb,
    private val tagMapperDb: TagMapperDb
) :
    MapperDb<TransactionAggregateDb, Transaction> {
    override fun mapToDomain(db: TransactionAggregateDb): Transaction {
        return Transaction(
            id = db.transactionDb.id,
            account = db.account(),
            type = db.transactionDb.type,
            category = db.category(),
            tags = db.tags(),
            note = db.transactionDb.note,
            date = db.transactionDb.date,
            amount = db.transactionDb.amount,
            imagePath = db.transactionDb.imagePath
        )
    }

    private fun TransactionAggregateDb.tags(): List<Tag> {
        return this.tagsDb.map { tagMapperDb.mapToDomain(it) }
    }

    private fun TransactionAggregateDb.account(): Account {
        return accountMapperDb.mapToDomain(this.accountDb)
    }

    private fun TransactionAggregateDb.category(): Category? {
        return categoryDb?.let { categoryMapperDb.mapToDomain(it) }
    }

    override fun mapFromDomain(domain: Transaction): TransactionAggregateDb {
        return TransactionAggregateDb(
            transactionDb = domain.transactionDb(),
            accountDb = domain.accountDb(),
            categoryDb = domain.categoryDb(),
            tagsDb = domain.tagsDb()
        )
    }

    private fun Transaction.accountDb(): AccountDb {
        return accountMapperDb.mapFromDomain(this.account)
    }

    private fun Transaction.categoryDb(): CategoryDb? {
        return this.category?.let { categoryMapperDb.mapFromDomain(it) }
    }

    private fun Transaction.tagsDb(): List<TagDb> {
        return this.tags.map { tagMapperDb.mapFromDomain(it) }
    }

    private fun Transaction.transactionDb(): TransactionDb {
        return TransactionDb(
            id = this.id,
            categoryId = this.category?.id,
            accountId = this.account.id,
            type = this.type,
            note = this.note,
            date = this.date,
            amount = this.amount,
            imagePath = this.imagePath
        )
    }
}