package com.yuriikonovalov.report.framework.ui.detailedreport

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.yuriikonovalov.common.framework.common.extentions.loadSvg
import com.yuriikonovalov.report.application.entities.AggregateCategoryFlatByCurrency
import com.yuriikonovalov.report.databinding.ItemAggregateCategoryFlatByCurrencyAdapterBinding
import com.yuriikonovalov.report.presentation.model.AggregateCategoryFlatByCurrencyUi

class AggregateCategoryFlatByCurrencyAdapter :
    ListAdapter<AggregateCategoryFlatByCurrency, AggregateCategoryFlatByCurrencyAdapter.ItemViewHolder>(
        AggregateCategoryFlatByCurrencyDiff
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    class ItemViewHolder private constructor(val binding: ItemAggregateCategoryFlatByCurrencyAdapterBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: AggregateCategoryFlatByCurrency) {
            val ui = AggregateCategoryFlatByCurrencyUi.from(binding.root.context, item)
            binding.name.text = ui.name
            binding.icon.loadSvg(ui.iconPath)
            binding.icon.imageTintList = ColorStateList.valueOf(ui.colorIcon)
            binding.icon.setBackgroundColor(ui.colorBackground)
            binding.numberOfTransactions.text = ui.numberOfTransactionsString
            binding.amount.text = ui.amountString
        }

        companion object {
            fun from(parent: ViewGroup): ItemViewHolder {
                val binding = ItemAggregateCategoryFlatByCurrencyAdapterBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return ItemViewHolder(binding)
            }
        }
    }

    private object AggregateCategoryFlatByCurrencyDiff :
        DiffUtil.ItemCallback<AggregateCategoryFlatByCurrency>() {
        override fun areItemsTheSame(
            oldItem: AggregateCategoryFlatByCurrency,
            newItem: AggregateCategoryFlatByCurrency
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: AggregateCategoryFlatByCurrency,
            newItem: AggregateCategoryFlatByCurrency
        ): Boolean {
            return oldItem == newItem
        }
    }

}