package com.yuriikonovalov.shared_test.model

import android.graphics.Color
import com.yuriikonovalov.common.application.entities.account.AccountType
import com.yuriikonovalov.common.application.entities.transaction.TransactionType
import com.yuriikonovalov.common.framework.data.local.database.model.AccountDb
import com.yuriikonovalov.common.framework.data.local.database.model.TagDb
import com.yuriikonovalov.common.framework.data.local.database.model.TransactionDb
import java.time.OffsetDateTime


val accountDb = AccountDb(
    Id.get(),
    "Name",
    AccountType.CARD,
    currency.code,
    0.0,
    1000.0,
    OffsetDateTime.now(),
    Color.BLACK
)
val transactionDb = TransactionDb(
    Id.get(),
    null,
    accountDb.id,
    TransactionType.EXPENSE,
    null,
    OffsetDateTime.now(),
    100.0,
    null
)

val tagDb = TagDb(id = Id.get(), "TagName")

fun tagsDb(number: Int) = List(number) { tagDb.copy(id = Id.get(), name = "Tag$it") }

fun accountsDb(number: Int, type: AccountType = AccountType.CARD, cur: String = currency.code) =
    List(number) { index ->
        accountDb.copy(
            id = Id.get(),
            type = type,
            currency = cur,
            name = "${type.name}${cur}$index"
        )
    }