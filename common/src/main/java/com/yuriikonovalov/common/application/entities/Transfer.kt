package com.yuriikonovalov.common.application.entities

import com.yuriikonovalov.common.application.entities.account.Account
import java.time.OffsetDateTime

data class Transfer(
    val id: Long,
    val accountFrom: Account,
    val accountTo: Account,
    val tags: List<Tag>,
    val note: String?,
    val date: OffsetDateTime,
    val amountFrom: Double,
    val amountTo: Double
)
