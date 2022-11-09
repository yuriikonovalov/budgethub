package com.yuriikonovalov.common.presentation.model

import com.yuriikonovalov.common.application.entities.account.Account
import com.yuriikonovalov.common.application.entities.account.AccountType
import com.yuriikonovalov.common.framework.utils.money.MoneyFormat

data class AccountUi(
    val name: String,
    val type: AccountType,
    val currencyCode: String,
    val currencySymbol: String,
    val balance: String,
    val color: Int,
) {
    companion object {
        fun from(domain: Account): AccountUi {
            return AccountUi(
                name = domain.name,
                type = domain.type,
                currencyCode = domain.currency.code,
                currencySymbol = domain.currency.symbol,
                balance = domain.balanceUi(),
                color = domain.color,
            )
        }

        private fun Account.balanceUi(): String {
            return MoneyFormat.getStringValue(balance)
        }
    }
}
