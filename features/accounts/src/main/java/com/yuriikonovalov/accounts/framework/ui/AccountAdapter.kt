package com.yuriikonovalov.accounts.framework.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.yuriikonovalov.common.application.entities.account.Account
import com.yuriikonovalov.common.databinding.ItemAccountCardAdapterBinding
import com.yuriikonovalov.common.framework.ui.toStringResource
import com.yuriikonovalov.common.presentation.model.AccountUi


class AccountAdapter(private val onMoreButtonClick: (accountId: Long, name: String) -> Unit) :
    ListAdapter<Account, AccountAdapter.AccountCardViewHolder>(DiffAccount) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountCardViewHolder {
        return AccountCardViewHolder.from(parent, onMoreButtonClick = { position ->
            val item = currentList[position]
            onMoreButtonClick(item.id, item.name)
        })
    }

    override fun onBindViewHolder(holder: AccountCardViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class AccountCardViewHolder private constructor(
        private val binding: ItemAccountCardAdapterBinding,
        private val onMoreButtonClick: (position: Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.moreButton.setOnClickListener {
                onMoreButtonClick(bindingAdapterPosition)
            }
        }

        fun bind(item: Account) {
            val ui = AccountUi.from(item)
            binding.card.currencyCode.text = ui.currencyCode
            binding.card.title.text = ui.name
            binding.card.balance.text = ui.balance
            binding.card.type.setText(ui.type.toStringResource())
            binding.card.background.setBackgroundColor(ui.color)
        }

        companion object {
            fun from(
                parent: ViewGroup, onMoreButtonClick: (position: Int) -> Unit
            ): AccountCardViewHolder {
                val binding = ItemAccountCardAdapterBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                return AccountCardViewHolder(binding, onMoreButtonClick)
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


