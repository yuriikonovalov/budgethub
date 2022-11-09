package com.yuriikonovalov.common.framework.ui.common

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.yuriikonovalov.common.application.entities.TransactionItem
import com.yuriikonovalov.common.application.util.TRANSFER_ICON
import com.yuriikonovalov.common.databinding.ItemTransactionAdapterBinding
import com.yuriikonovalov.common.databinding.ItemTransferTransactionAdapterBinding
import com.yuriikonovalov.common.framework.common.extentions.loadSvg
import com.yuriikonovalov.common.presentation.model.TransactionUi
import com.yuriikonovalov.common.presentation.model.TransferUi

class TransactionItemAdapter(
    private val onTransactionClick: (id: Long) -> Unit,
    private val onTransferClick: (id: Long) -> Unit,
) : ListAdapter<TransactionItem, RecyclerView.ViewHolder>(TransactionItemDiffCallback) {

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is TransactionItem.ExpenseIncome -> VIEW_TYPE_EXPENSE_INCOME_TRANSACTION
            is TransactionItem.Transfer -> VIEW_TYPE_TRANSFER_TRANSACTION
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_EXPENSE_INCOME_TRANSACTION -> ExpenseIncomeTransactionViewHolder.from(
                parent,
                onItemClick = { position ->
                    onTransactionClick(currentList[position].id)
                }
            )
            VIEW_TYPE_TRANSFER_TRANSACTION -> TransferTransactionViewHolder.from(
                parent,
                onItemClick = { position ->
                    // In this case we need to get id value from the transfer value because the
                    // 'currentList[position].id' expression will return id with the minus sign here.
                    val item = currentList[position] as TransactionItem.Transfer
                    onTransferClick(item.transfer.id)
                }
            )
            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ExpenseIncomeTransactionViewHolder -> {
                val item = getItem(position) as TransactionItem.ExpenseIncome
                holder.bind(item)
            }
            is TransferTransactionViewHolder -> {
                val item = getItem(position) as TransactionItem.Transfer
                holder.bind(item)
            }
        }
    }

    class ExpenseIncomeTransactionViewHolder private constructor(
        private val binding: ItemTransactionAdapterBinding,
        private val onItemClick: (position: Int) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                onItemClick(bindingAdapterPosition)
            }
        }

        fun bind(item: TransactionItem.ExpenseIncome) {
            val ui = TransactionUi.from(binding.root.context, item.transaction)
            binding.name.text = ui.categoryName
            binding.date.text = ui.date
            binding.amount.text = ui.amount
            binding.currencyCode.text = ui.currencyCode
            binding.icon.loadSvg(ui.categoryIcon)
            binding.icon.setBackgroundColor(ui.categoryBackgroundColor)
            binding.icon.imageTintList = ColorStateList.valueOf(ui.categoryIconColor)
        }

        companion object {
            fun from(
                parent: ViewGroup,
                onItemClick: (position: Int) -> Unit,
            ): ExpenseIncomeTransactionViewHolder {
                val binding = ItemTransactionAdapterBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return ExpenseIncomeTransactionViewHolder(binding, onItemClick)
            }
        }
    }

    class TransferTransactionViewHolder private constructor(
        private val binding: ItemTransferTransactionAdapterBinding,
        private val onItemClick: (position: Int) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                onItemClick(bindingAdapterPosition)
            }
        }

        fun bind(item: TransactionItem.Transfer) {
            val ui = TransferUi.from(item.transfer)
            binding.date.text = ui.date
            binding.amountFrom.text = ui.amountFrom
            binding.amountTo.text = ui.amountTo
            binding.currencyFromCode.text = ui.currencyFromCode
            binding.currencyToCode.text = ui.currencyToCode
            binding.icon.loadSvg(TRANSFER_ICON)
            binding.icon.setBackgroundColor(ui.backgroundColor)
            binding.icon.imageTintList = ColorStateList.valueOf(ui.iconColor)
        }

        companion object {
            fun from(
                parent: ViewGroup,
                onItemClick: (position: Int) -> Unit,
            ): TransferTransactionViewHolder {
                val binding = ItemTransferTransactionAdapterBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return TransferTransactionViewHolder(binding, onItemClick)
            }
        }
    }


    private object TransactionItemDiffCallback : DiffUtil.ItemCallback<TransactionItem>() {
        override fun areItemsTheSame(
            oldItem: TransactionItem,
            newItem: TransactionItem,
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: TransactionItem,
            newItem: TransactionItem,
        ): Boolean {
            return oldItem == newItem
        }
    }

    private companion object {
        const val VIEW_TYPE_EXPENSE_INCOME_TRANSACTION = 0
        const val VIEW_TYPE_TRANSFER_TRANSACTION = 1
    }
}