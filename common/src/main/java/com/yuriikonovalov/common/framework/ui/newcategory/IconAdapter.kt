package com.yuriikonovalov.common.framework.ui.newcategory

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.color.MaterialColors
import com.yuriikonovalov.common.application.entities.Icon
import com.yuriikonovalov.common.databinding.ItemIconAdapterBinding
import com.yuriikonovalov.common.framework.common.extentions.dpToPx
import com.yuriikonovalov.common.framework.common.extentions.loadSvg
import com.yuriikonovalov.common.presentation.model.IconItem

class IconAdapter(private val onItemClick: (icon: Icon) -> Unit) :
    ListAdapter<IconItem, IconAdapter.IconViewHolder>(diffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IconViewHolder {
        return IconViewHolder.from(parent, onItemClick = { position ->
            val item = currentList[position]
            onItemClick(item.icon)
        })
    }

    override fun onBindViewHolder(holder: IconViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class IconViewHolder private constructor(
        private val binding: ItemIconAdapterBinding,
        private val onItemClick: (position: Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                onItemClick(bindingAdapterPosition)
            }
        }

        fun bind(item: IconItem) {
            binding.image.loadSvg(item.icon.path)
            binding.image.strokeWidth = if (item.checked) 1.dpToPx.toFloat() else 0.dpToPx.toFloat()
            binding.image.imageTintList = ColorStateList.valueOf(
                MaterialColors.getColor(
                    binding.image,
                    com.google.android.material.R.attr.colorOnBackground
                )
            )
        }

        companion object {
            fun from(parent: ViewGroup, onItemClick: (position: Int) -> Unit): IconViewHolder {
                val binding = ItemIconAdapterBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return IconViewHolder(binding, onItemClick)
            }
        }
    }
}

private val diffCallback = object : DiffUtil.ItemCallback<IconItem>() {
    override fun areItemsTheSame(oldItem: IconItem, newItem: IconItem): Boolean {
        return oldItem.icon.path == newItem.icon.path
    }

    override fun areContentsTheSame(oldItem: IconItem, newItem: IconItem): Boolean {
        return oldItem == newItem
    }
}