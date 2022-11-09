package com.yuriikonovalov.home.framework.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.yuriikonovalov.common.R
import com.yuriikonovalov.common.application.entities.AvailableBalance
import com.yuriikonovalov.home.databinding.ItemAvailableBalanceAdapterBinding
import com.yuriikonovalov.home.presentation.model.AvailableBalanceUi

class AvailableBalanceAdapter(private val onPercentClickListener: () -> Unit) :
    ListAdapter<AvailableBalance, AvailableBalanceAdapter.AvailableBalanceViewHolder>(
        AvailableBalanceDiffCallback
    ) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AvailableBalanceViewHolder {
        return AvailableBalanceViewHolder.from(parent, onPercentClickListener)
    }

    override fun onBindViewHolder(holder: AvailableBalanceViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    data class AvailableBalanceViewHolder constructor(
        private val binding: ItemAvailableBalanceAdapterBinding,
        private val onPercentClickListener: () -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        private val context = binding.root.context

        init {
            binding.percent.setOnClickListener {
                onPercentClickListener()
            }
        }

        fun bind(item: AvailableBalance) {
            val ui = AvailableBalanceUi.from(item)
            binding.currencyCode.text = ui.currencyCode
            binding.balance.text = ui.balance
            binding.percent.text = ui.percent

            // Scrolling behavior
            binding.percent.isSelected = true
            binding.percent.setSingleLine()

            val percentTint = ui.percentBackground.color(context)
            binding.percent.background.setTint(percentTint)

        }

        private fun AvailableBalanceUi.PercentBackground.color(context: Context): Int {
            return when (this) {
                AvailableBalanceUi.PercentBackground.POSITIVE -> {
                    ContextCompat.getColor(context, R.color.green_material_200)
                }
                AvailableBalanceUi.PercentBackground.NEUTRAL -> {
                    ContextCompat.getColor(context, R.color.grey_light)
                }
                AvailableBalanceUi.PercentBackground.NEGATIVE -> {
                    ContextCompat.getColor(context, R.color.pink_material_100)
                }
            }
        }


        companion object {
            fun from(
                parent: ViewGroup,
                onPercentClickListener: () -> Unit
            ): AvailableBalanceViewHolder {
                val binding = ItemAvailableBalanceAdapterBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return AvailableBalanceViewHolder(binding, onPercentClickListener)
            }
        }
    }


    private object AvailableBalanceDiffCallback : DiffUtil.ItemCallback<AvailableBalance>() {
        override fun areItemsTheSame(
            oldItem: AvailableBalance,
            newItem: AvailableBalance
        ): Boolean {
            return oldItem.accountId == newItem.accountId
        }

        override fun areContentsTheSame(
            oldItem: AvailableBalance,
            newItem: AvailableBalance
        ): Boolean {
            return oldItem == newItem
        }
    }
}