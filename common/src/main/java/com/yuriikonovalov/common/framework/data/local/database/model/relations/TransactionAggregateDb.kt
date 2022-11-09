package com.yuriikonovalov.common.framework.data.local.database.model.relations

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.yuriikonovalov.common.framework.data.local.database.model.*

data class TransactionAggregateDb(
    @Embedded val transactionDb: TransactionDb,
    @Relation(
        entity = AccountDb::class,
        parentColumn = "account_id",
        entityColumn = "id"
    )
    val accountDb: AccountDb,
    @Relation(
        entity = CategoryDb::class,
        parentColumn = "category_id",
        entityColumn = "id"
    )
    val categoryDb: CategoryDb?,
    @Relation(
        parentColumn = "id",
        entityColumn = "tag_id",
        associateBy = Junction(TransactionTagCrossRefDb::class)
    )
    val tagsDb: List<TagDb>
)
