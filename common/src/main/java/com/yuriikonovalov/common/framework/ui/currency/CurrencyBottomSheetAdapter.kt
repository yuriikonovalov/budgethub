package com.yuriikonovalov.common.framework.ui.currency

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.yuriikonovalov.common.application.entities.Currency
import com.yuriikonovalov.common.databinding.ItemCurrencyBottomSheetAdapterBinding

class CurrencyBottomSheetAdapter(private val onCurrencyClick: (currency: Currency) -> Unit) :
    ListAdapter<Currency, CurrencyBottomSheetAdapter.CurrencyItemViewHolder>(CurrencyDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyItemViewHolder {
        return CurrencyItemViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: CurrencyItemViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, onCurrencyClick)
    }

    class CurrencyItemViewHolder private constructor(private val binding: ItemCurrencyBottomSheetAdapterBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Currency, onCurrencyClick: (currency: Currency) -> Unit) {
            binding.code.text = item.code
            binding.symbol.text = item.symbol
            binding.name.text = item.name
            binding.itemLayout.setOnClickListener {
                onCurrencyClick(item)
            }
        }

        companion object {
            fun from(parent: ViewGroup): CurrencyItemViewHolder {
                val binding = ItemCurrencyBottomSheetAdapterBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return CurrencyItemViewHolder(binding)
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