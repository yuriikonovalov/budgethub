package com.yuriikonovalov.common.framework.ui.common

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.yuriikonovalov.common.application.entities.TransactionAmount
import com.yuriikonovalov.common.databinding.ItemExpenseIncomeAdapterBinding
import com.yuriikonovalov.common.presentation.model.TransactionAmountUi

class ExpenseIncomeAdapter :
    ListAdapter<TransactionAmount, ExpenseIncomeAdapter.ItemViewHolder>(diffCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    data class ItemViewHolder(val binding: ItemExpenseIncomeAdapterBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: TransactionAmount) {
            val ui = TransactionAmountUi.from(item)
            binding.currencyCode.text = ui.currencyCode
            binding.amount.text = ui.amount
        }

        companion object {
            fun from(parent: ViewGroup): ItemViewHolder {
                val binding = ItemExpenseIncomeAdapterBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return ItemViewHolder(binding)
            }
        }
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<TransactionAmount>() {
            override fun areItemsTheSame(
                oldItem: TransactionAmount,
                newItem: TransactionAmount,
            ): Boolean {
                return oldItem.currency.code == newItem.currency.code
            }

            override fun areContentsTheSame(
                oldItem: TransactionAmount,
                newItem: TransactionAmount,
            ): Boolean {
                return oldItem == newItem
            }
        }
    }


}