package com.yuriikonovalov.common.application.entities.transaction

import com.yuriikonovalov.common.application.entities.Tag
import com.yuriikonovalov.common.application.entities.account.Account
import com.yuriikonovalov.common.application.entities.category.Category
import java.time.OffsetDateTime

data class Transaction(
    val id: Long,
    val account: Account,
    val category: Category?,
    val type: TransactionType,
    val tags: List<Tag>,
    val note: String?,
    val date: OffsetDateTime,
    val amount: Double,
    val imagePath: String?
)
