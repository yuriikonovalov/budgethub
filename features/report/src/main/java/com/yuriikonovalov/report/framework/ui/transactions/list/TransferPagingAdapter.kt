package com.yuriikonovalov.report.framework.ui.transactions.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.yuriikonovalov.common.application.entities.Transfer
import com.yuriikonovalov.common.databinding.ItemTransferTransactionAdapterBinding
import com.yuriikonovalov.common.framework.common.extentions.loadSvg
import com.yuriikonovalov.common.framework.common.extentions.setTintColor
import com.yuriikonovalov.common.presentation.model.TransferUi

class TransferPagingAdapter(
    private val onItemClick: ((id: Long) -> Unit)?,
) : PagingDataAdapter<Transfer, TransferPagingAdapter.TransferViewHolder>(
    TransferPagedDiffCallback
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransferViewHolder {
        return TransferViewHolder.from(parent, onItemClick = { position ->
            onItemClick?.invoke(getItem(position)!!.id)
        })
    }

    override fun onBindViewHolder(holder: TransferViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class TransferViewHolder private constructor(
        private val binding: ItemTransferTransactionAdapterBinding,
        private val onItemClick: (position: Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                onItemClick(bindingAdapterPosition)
            }
        }

        fun bind(item: Transfer?) {
            item?.let {
                val ui = TransferUi.from(it)
                binding.date.text = ui.date
                binding.amountFrom.text = ui.amountFrom
                binding.amountTo.text = ui.amountTo
                binding.currencyFromCode.text = ui.currencyFromCode
                binding.currencyToCode.text = ui.currencyToCode
                binding.icon.loadSvg(ui.icon)
                binding.icon.setBackgroundColor(ui.backgroundColor)
                binding.icon.setTintColor(ui.iconColor)
            }
        }

        companion object {
            fun from(
                parent: ViewGroup, onItemClick: (position: Int) -> Unit
            ): TransferViewHolder {
                val binding = ItemTransferTransactionAdapterBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return TransferViewHolder(binding, onItemClick)
            }
        }
    }


    private object TransferPagedDiffCallback : DiffUtil.ItemCallback<Transfer>() {
        override fun areItemsTheSame(
            oldItem: Transfer,
            newItem: Transfer
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: Transfer,
            newItem: Transfer
        ): Boolean {
            return oldItem == newItem
        }
    }

}