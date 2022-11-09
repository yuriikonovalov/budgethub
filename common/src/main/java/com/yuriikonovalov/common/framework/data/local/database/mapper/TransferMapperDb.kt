package com.yuriikonovalov.common.framework.data.local.database.mapper

import com.yuriikonovalov.common.application.entities.Tag
import com.yuriikonovalov.common.application.entities.Transfer
import com.yuriikonovalov.common.application.entities.account.Account
import com.yuriikonovalov.common.framework.data.local.database.model.AccountDb
import com.yuriikonovalov.common.framework.data.local.database.model.TagDb
import com.yuriikonovalov.common.framework.data.local.database.model.TransferDb
import com.yuriikonovalov.common.framework.data.local.database.model.relations.TransferAggregateDb
import javax.inject.Inject

class TransferMapperDb @Inject constructor(
    private val accountMapperDb: AccountMapperDb,
    private val tagMapperDb: TagMapperDb
) : MapperDb<TransferAggregateDb, Transfer> {

    override fun mapFromDomain(domain: Transfer): TransferAggregateDb {
        return TransferAggregateDb(
            transfer = domain.transferDb(),
            accountFrom = domain.accountFromDb(),
            accountTo = domain.accountToDb(),
            tags = domain.tagToDb()
        )
    }

    private fun Transfer.transferDb(): TransferDb {
        return TransferDb(
            id = this.id,
            accountFromId = this.accountFrom.id,
            accountToId = this.accountTo.id,
            note = this.note,
            date = this.date,
            amountFrom = this.amountFrom,
            amountTo = this.amountTo
        )
    }

    private fun Transfer.accountFromDb(): AccountDb {
        return accountMapperDb.mapFromDomain(this.accountFrom)
    }

    private fun Transfer.accountToDb(): AccountDb {
        return accountMapperDb.mapFromDomain(this.accountTo)
    }

    private fun Transfer.tagToDb(): List<TagDb> {
        return tags.map { tagMapperDb.mapFromDomain(it) }
    }

    override fun mapToDomain(db: TransferAggregateDb): Transfer {
        return Transfer(
            id = db.transfer.id,
            accountFrom = db.accountFrom(),
            accountTo = db.accountTo(),
            tags = db.tags(),
            note = db.transfer.note,
            date = db.transfer.date,
            amountFrom = db.transfer.amountFrom,
            amountTo = db.transfer.amountTo
        )
    }

    private fun TransferAggregateDb.tags(): List<Tag> {
        return this.tags.map { tagMapperDb.mapToDomain(it) }
    }

    private fun TransferAggregateDb.accountFrom(): Account {
        return accountMapperDb.mapToDomain(this.accountFrom)
    }

    private fun TransferAggregateDb.accountTo(): Account {
        return accountMapperDb.mapToDomain(this.accountTo)
    }
}


