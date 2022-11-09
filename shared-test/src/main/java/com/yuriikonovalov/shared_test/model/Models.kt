package com.yuriikonovalov.shared_test.model

import android.graphics.Color
import com.yuriikonovalov.common.application.entities.Currency
import com.yuriikonovalov.common.application.entities.Icon
import com.yuriikonovalov.common.application.entities.Tag
import com.yuriikonovalov.common.application.entities.Transfer
import com.yuriikonovalov.common.application.entities.account.Account
import com.yuriikonovalov.common.application.entities.account.AccountType
import com.yuriikonovalov.common.application.entities.category.Category
import com.yuriikonovalov.common.application.entities.category.CategoryType
import com.yuriikonovalov.common.application.entities.transaction.Transaction
import com.yuriikonovalov.common.application.entities.transaction.TransactionType
import java.time.OffsetDateTime

object Id {
    private var id = 1
    fun get() = id++.toLong()
}

val category = Category(Id.get(), CategoryType.EXPENSE, "Category", false, "", Color.BLACK)
val currency: Currency = Currency("USD", "US Dollar", "US\$")
val account = Account(
    Id.get(),
    "Name",
    AccountType.CARD,
    currency,
    0.0,
    1000.0,
    OffsetDateTime.now(),
    Color.BLACK
)
val transaction = Transaction(
    Id.get(),
    account,
    category,
    TransactionType.EXPENSE,
    emptyList(),
    null,
    OffsetDateTime.now(),
    100.0,
    null
)
val transfer = Transfer(
    1,
    account,
    account,
    emptyList(),
    null,
    OffsetDateTime.now(),
    100.0,
    100.0
)

val tag = Tag(id = Id.get(), "TagName")
val icon = Icon("path")

fun icons(number: Int) = List(number) { icon.copy("path$it") }

fun tags(number: Int) = List(number) { tag.copy(name = "Tag$it") }

fun accounts(number: Int, type: AccountType = AccountType.CARD, cur: Currency = currency) =
    List(number) { index ->
        account.copy(type = type, currency = cur, name = "${type.name}${cur.code}$index")
    }

fun categories(number: Int, type: CategoryType = CategoryType.EXPENSE) =
    List(number) { index -> category.copy(type = type, name = "${type.name}$index") }
