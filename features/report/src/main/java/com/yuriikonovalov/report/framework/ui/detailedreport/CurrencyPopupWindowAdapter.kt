package com.yuriikonovalov.report.framework.ui.detailedreport

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.yuriikonovalov.common.application.entities.Currency
import com.yuriikonovalov.report.databinding.ItemCurrencyPopupWindowAdapterBinding


class CurrencyPopupWindowAdapter(private val onItemClick: (currency: Currency) -> Unit) :
    ListAdapter<Currency, CurrencyPopupWindowAdapter.CurrencyViewHolder>(CurrencyDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyViewHolder {
        return CurrencyViewHolder.from(parent, onItemClick = { position ->
            onItemClick(currentList[position])
        })
    }

    override fun onBindViewHolder(holder: CurrencyViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class CurrencyViewHolder private constructor(
        private val binding: ItemCurrencyPopupWindowAdapterBinding,
        private val onItemClick: (position: Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                onItemClick(bindingAdapterPosition)
            }
        }

        fun bind(item: Currency) {
            binding.code.text = item.code
            binding.name.text = item.name
        }

        companion object {
            fun from(
                parent: ViewGroup,
                onItemClick: (position: Int) -> Unit
            ): CurrencyViewHolder {
                val binding = ItemCurrencyPopupWindowAdapterBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return CurrencyViewHolder(binding, onItemClick)
            }
        }
    }

    private object CurrencyDiffCallback : DiffUtil.ItemCallback<Currency>() {
        override fun areItemsTheSame(oldItem: Currency, newItem: Currency): Boolean {
            return oldItem.code == newItem.code
        }

        override fun areContentsTheSame(oldItem: Currency, newItem: Currency): Boolean {
            return oldItem == newItem
        }
    }


}