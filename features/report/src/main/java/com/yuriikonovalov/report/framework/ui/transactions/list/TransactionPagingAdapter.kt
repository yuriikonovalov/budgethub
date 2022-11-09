package com.yuriikonovalov.report.framework.ui.transactions.list

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.yuriikonovalov.common.application.entities.transaction.Transaction
import com.yuriikonovalov.common.databinding.ItemTransactionAdapterBinding
import com.yuriikonovalov.common.framework.common.extentions.loadSvg
import com.yuriikonovalov.common.presentation.model.TransactionUi

class TransactionPagingAdapter(
    private val onItemClick: ((id: Long) -> Unit)?,
) : PagingDataAdapter<Transaction, TransactionPagingAdapter.TransactionViewHolder>(
    TransactionPagedDiffCallback
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        return TransactionViewHolder.from(parent, onItemClick = { position ->
            onItemClick?.invoke(getItem(position)!!.id)
        })
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class TransactionViewHolder private constructor(
        private val binding: ItemTransactionAdapterBinding,
        private val onItemClick: (position: Int) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                onItemClick(bindingAdapterPosition)
            }
        }

        fun bind(item: Transaction?) {
            item?.let {
                val ui = TransactionUi.from(binding.root.context, it)
                binding.name.text = ui.categoryName
                binding.date.text = ui.date
                binding.amount.text = ui.amount
                binding.currencyCode.text = ui.currencyCode
                binding.icon.loadSvg(ui.categoryIcon)
                binding.icon.setBackgroundColor(ui.categoryBackgroundColor)
                binding.icon.imageTintList = ColorStateList.valueOf(ui.categoryIconColor)
            }
        }

        companion object {
            fun from(
                parent: ViewGroup,
                onItemClick: (position: Int) -> Unit,
            ): TransactionViewHolder {
                val binding = ItemTransactionAdapterBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return TransactionViewHolder(binding, onItemClick)
            }
        }
    }

    private object TransactionPagedDiffCallback : DiffUtil.ItemCallback<Transaction>() {
        override fun areItemsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
            return oldItem == newItem
        }
    }
}