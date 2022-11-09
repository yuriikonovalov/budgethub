package com.yuriikonovalov.report.framework.ui.common

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.yuriikonovalov.common.application.entities.account.Account
import com.yuriikonovalov.common.presentation.model.AccountUi
import com.yuriikonovalov.report.databinding.ItemAccountFilterAdapterBinding
import com.yuriikonovalov.report.presentation.model.AccountItem

class AccountFilterAdapter(private val onItemClick: (account: Account) -> Unit) :
    ListAdapter<AccountItem, AccountFilterAdapter.AccountViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountViewHolder {
        return AccountViewHolder.from(parent, onItemClick = { position ->
            onItemClick(currentList[position].account)
        })
    }

    override fun onBindViewHolder(holder: AccountViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class AccountViewHolder private constructor(
        private val binding: ItemAccountFilterAdapterBinding,
        private val onItemClick: (position: Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                onItemClick(bindingAdapterPosition)
            }
        }

        fun bind(item: AccountItem) {
            val ui = AccountUi.from(item.account)
            binding.name.text = ui.name
            binding.checkbox.isChecked = item.checked
        }

        companion object {
            fun from(
                parent: ViewGroup,
                onItemClick: (position: Int) -> Unit
            ): AccountViewHolder {
                val binding = ItemAccountFilterAdapterBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return AccountViewHolder(binding, onItemClick)
            }
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<AccountItem>() {
        override fun areItemsTheSame(oldItem: AccountItem, newItem: AccountItem): Boolean {
            return oldItem.account.id == newItem.account.id
        }

        override fun areContentsTheSame(oldItem: AccountItem, newItem: AccountItem): Boolean {
            return oldItem == newItem
        }
    }
}