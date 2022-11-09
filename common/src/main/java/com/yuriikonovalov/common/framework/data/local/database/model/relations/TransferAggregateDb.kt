package com.yuriikonovalov.common.framework.data.local.database.model.relations

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.yuriikonovalov.common.framework.data.local.database.model.AccountDb
import com.yuriikonovalov.common.framework.data.local.database.model.TagDb
import com.yuriikonovalov.common.framework.data.local.database.model.TransferTagCrossRefDb
import com.yuriikonovalov.common.framework.data.local.database.model.TransferDb

data class TransferAggregateDb(
    @Embedded val transfer: TransferDb,
    @Relation(
        entity = AccountDb::class,
        parentColumn = "sender_id",
        entityColumn = "id"
    )
    val accountFrom: AccountDb,

    @Relation(
        entity = AccountDb::class,
        parentColumn = "recipient_id",
        entityColumn = "id"
    )
    val accountTo: AccountDb,

    @Relation(
        parentColumn = "id",
        entityColumn = "tag_id",
        associateBy = Junction(TransferTagCrossRefDb::class)
    )
    val tags: List<TagDb>
)
