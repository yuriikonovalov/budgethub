package com.yuriikonovalov.common.application.entities.account

import android.os.Parcelable
import com.yuriikonovalov.common.application.entities.Currency
import kotlinx.parcelize.Parcelize
import java.time.OffsetDateTime

@Parcelize
data class Account(
    val id: Long,
    val name: String,
    val type: AccountType,
    val currency: Currency,
    val initialBalance: Double,
    val balance: Double,
    val dateOfCreation: OffsetDateTime,
    val color: Int
) : Parcelable