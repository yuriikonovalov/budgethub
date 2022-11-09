package com.yuriikonovalov.common.framework.data.local.database.model

import androidx.room.*
import com.yuriikonovalov.common.application.entities.account.AccountType
import java.time.OffsetDateTime

@Entity(tableName = "accounts")
data class AccountDb(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val type: AccountType,
    val currency: String,
    @ColumnInfo(name = "initial_balance")
    val initialBalance: Double,
    val balance: Double,
    @ColumnInfo(name = "date_of_creation")
    val dateOfCreation: OffsetDateTime,
    val color: Int
)
