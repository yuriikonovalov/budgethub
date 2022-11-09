package com.yuriikonovalov.report.framework.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.doOnLayout
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.yuriikonovalov.common.framework.common.extentions.loadSvg
import com.yuriikonovalov.common.framework.common.extentions.setTintColor
import com.yuriikonovalov.report.application.entities.CategoryWithNumberOfTransactions
import com.yuriikonovalov.report.databinding.ItemCategoryWithNumberOfTransactionsAdapterBinding
import com.yuriikonovalov.report.presentation.model.CategoryWithNumberOfTransactionsUi
import kotlin.math.roundToInt

class CategoryWithNumberOfTransactionsAdapter :
    ListAdapter<CategoryWithNumberOfTransactions, CategoryWithNumberOfTransactionsAdapter.ItemViewHolder>(
        CategoryWithNumberOfTransactionsDiff
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ItemViewHolder private constructor(
        private val binding: ItemCategoryWithNumberOfTransactionsAdapterBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CategoryWithNumberOfTransactions) {
            val ui = CategoryWithNumberOfTransactionsUi.from(binding.root.context, item)
            binding.name.text = ui.name
            binding.icon.loadSvg(ui.iconPath)
            binding.icon.setTintColor(ui.colorIcon)
            binding.icon.setBackgroundColor(ui.colorBackground)
            binding.numberOfTransactions.text = ui.numberOfTransactions
            binding.percent.text = ui.percentOfAllTransaction

            binding.indicator.setBackgroundColor(ui.colorIcon)
            // Reset width to match constraints so that for calculating the new width depending on the percent
            // will be used full width of the view.
            binding.indicator.updateLayoutParams<ConstraintLayout.LayoutParams> {
                width = ConstraintLayout.LayoutParams.MATCH_CONSTRAINT
            }

            binding.indicator.doOnLayout { indicator ->
                val fullWidth = indicator.width
                indicator.updateLayoutParams<ConstraintLayout.LayoutParams> {
                    width = (fullWidth * item.percentFractionOfAllTransaction / 100).roundToInt()
                }
            }
        }

        companion object {
            fun from(parent: ViewGroup): ItemViewHolder {
                val binding = ItemCategoryWithNumberOfTransactionsAdapterBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return ItemViewHolder(binding)
            }
        }
    }

    companion object {
        object CategoryWithNumberOfTransactionsDiff :
            DiffUtil.ItemCallback<CategoryWithNumberOfTransactions>() {
            override fun areItemsTheSame(
                oldItem: CategoryWithNumberOfTransactions,
                newItem: CategoryWithNumberOfTransactions
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: CategoryWithNumberOfTransactions,
                newItem: CategoryWithNumberOfTransactions
            ): Boolean {
                return oldItem == newItem
            }
        }
    }


}