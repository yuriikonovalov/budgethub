package com.yuriikonovalov.common.framework.ui.addedittransaction.account

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.yuriikonovalov.common.application.entities.account.Account
import com.yuriikonovalov.common.databinding.ViewAccountCardBinding
import com.yuriikonovalov.common.framework.ui.toStringResource
import com.yuriikonovalov.common.presentation.model.AccountUi

class AccountCardAdapter(
    private val onItemClick: ((account: Account) -> Unit)
) : ListAdapter<Account, AccountCardAdapter.AccountCardViewHolder>(DiffAccount) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountCardViewHolder {
        return AccountCardViewHolder.from(
            parent,
            onItemClick = { position ->
                onItemClick(currentList[position])
            }
        )
    }

    override fun onBindViewHolder(holder: AccountCardViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class AccountCardViewHolder private constructor(
        private val binding: ViewAccountCardBinding,
        private val onItemClick: ((position: Int) -> Unit)
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                onItemClick(bindingAdapterPosition)
            }
        }

        fun bind(item: Account) {
            val ui = AccountUi.from(item)
            binding.title.text = ui.name
            binding.balance.text = ui.balance
            binding.currencyCode.text = ui.currencyCode
            binding.type.setText(ui.type.toStringResource())
            binding.background.setBackgroundColor(ui.color)
        }

        companion object {
            fun from(
                parent: ViewGroup, onItemClick: ((position: Int) -> Unit)
            ): AccountCardViewHolder {
                val binding = ViewAccountCardBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                return AccountCardViewHolder(binding, onItemClick)
            }
        }
    }

    private object DiffAccount : DiffUtil.ItemCallback<Account>() {
        override fun areItemsTheSame(oldItem: Account, newItem: Account): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Account, newItem: Account): Boolean {
            return oldItem == newItem
        }
    }
}