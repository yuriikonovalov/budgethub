package com.yuriikonovalov.common.framework.ui.addedittransaction.category

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.yuriikonovalov.common.databinding.ItemCategoryAdapterBinding
import com.yuriikonovalov.common.application.entities.category.Category
import com.yuriikonovalov.common.presentation.model.CategoryUi
import com.yuriikonovalov.common.framework.common.extentions.loadSvg

class CategoryAdapter(
    private val onItemClick: (category: Category) -> Unit,
    private val onItemLongClick: (id: Long) -> Unit
) : ListAdapter<Category, CategoryAdapter.CategoryViewHolder>(CategoryDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        return CategoryViewHolder.from(
            parent,
            onItemClick = { position ->
                val item = currentList[position]
                onItemClick(item)
            },
            onItemLongClick = { position ->
                val item = currentList[position]
                if (item.isCustom) {
                    onItemLongClick(item.id)
                }
            }
        )
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class CategoryViewHolder private constructor(
        private val binding: ItemCategoryAdapterBinding,
        private val onItemClick: (position: Int) -> Unit,
        private val onItemLongClick: (position: Int) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.apply {
                setOnClickListener { onItemClick(bindingAdapterPosition) }
                setOnLongClickListener {
                    onItemLongClick(bindingAdapterPosition)
                    true
                }
            }
        }

        fun bind(item: Category) {
            val ui = CategoryUi.from(binding.root.context, item)
            binding.icon.loadSvg(ui.iconPath)
            binding.icon.imageTintList = ColorStateList.valueOf(ui.colorIcon)
            binding.iconBackground.setBackgroundColor(ui.colorBackground)
            binding.name.text = ui.name
            binding.iconBackground.contentDescription = ui.name
            binding.icon.contentDescription = ui.name
        }

        companion object {
            fun from(
                parent: ViewGroup,
                onItemClick: (position: Int) -> Unit,
                onItemLongClick: (position: Int) -> Unit,
            ): CategoryViewHolder {
                val binding = ItemCategoryAdapterBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )

                return CategoryViewHolder(binding, onItemClick, onItemLongClick)
            }
        }
    }

    private object CategoryDiffCallback : DiffUtil.ItemCallback<Category>() {
        override fun areItemsTheSame(oldItem: Category, newItem: Category): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Category, newItem: Category): Boolean {
            return oldItem == newItem
        }
    }
}