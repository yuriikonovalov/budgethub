package com.yuriikonovalov.home.presentation.model

import com.yuriikonovalov.common.application.entities.AvailableBalance
import com.yuriikonovalov.common.framework.common.extentions.roundDecimals
import com.yuriikonovalov.common.framework.utils.money.MoneyFormat
import kotlin.math.abs

data class AvailableBalanceUi(
    val currencyCode: String,
    val balance: String,
    val percent: String,
    val percentBackground: PercentBackground
) {
    enum class PercentBackground {
        POSITIVE, NEGATIVE, NEUTRAL
    }

    companion object {
        fun from(domain: AvailableBalance): AvailableBalanceUi {
            return AvailableBalanceUi(
                currencyCode = domain.currency.code,
                balance = domain.balanceUi(),
                percent = domain.percentUi(),
                percentBackground = domain.percentBackgroundUi()
            )
        }

        private fun AvailableBalance.percentBackgroundUi(): PercentBackground {
            val percent = this.percent()
            return when {
                percent > 0 -> PercentBackground.POSITIVE
                percent < 0 -> PercentBackground.NEGATIVE
                else -> PercentBackground.NEUTRAL
            }
        }

        private fun AvailableBalance.percent(): Double {
            val percent = when {
                startBalance == 0.0 && endBalance == 0.0 -> 0.0
                //Change 0 to 1, so don't subtract 100
                startBalance == 0.0 -> (endBalance.safety() * 100) / startBalance.safety()
                else -> (endBalance.safety() * 100) / startBalance.safety() - 100
            }
            return percent.roundDecimals(2)
        }

        private fun Double.safety(): Double {
            // a case when the initial balance of the account is 0.
            return if (this == 0.0) {
                1.0
            } else {
                this
            }
        }

        private fun AvailableBalance.percentUi(): String {
            val percent = this.percent()
            return when {
                percent > 0.0 -> "+ $percent %"
                percent < 0.0 -> "- ${abs(percent)} %"
                else -> "$percent %"
            }
        }

        private fun AvailableBalance.balanceUi(): String {
            return MoneyFormat.getStringValue(this.endBalance)
        }
    }

}

