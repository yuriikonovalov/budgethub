package com.yuriikonovalov.common.framework.ui.common

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.yuriikonovalov.common.R
import com.yuriikonovalov.common.databinding.ItemColorAdapterBinding
import com.yuriikonovalov.common.presentation.model.ColorItem


class ColorAdapter(
    private val onItemClick: (color: Int) -> Unit,
    private val onButtonClick: () -> Unit
) : ListAdapter<ColorItem, ColorAdapter.ColorViewHolder>(diffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorViewHolder {
        return ColorViewHolder.from(parent, onItemClick = { position ->
            val itemAtPosition = currentList[position]
            if (itemAtPosition.button) {
                onButtonClick()
            } else {
                onItemClick(itemAtPosition.color)
            }
        })
    }

    override fun onBindViewHolder(holder: ColorViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ColorViewHolder private constructor(
        private val binding: ItemColorAdapterBinding,
        private val onItemClick: (position: Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.circle.setOnClickListener {
                onItemClick(bindingAdapterPosition)
            }
        }

        fun bind(item: ColorItem) {
            binding.circle.setBackgroundColor(item.color)
            binding.checkIcon.isVisible = item.checked
            binding.addIcon.isVisible = item.button
            binding.circle.contentDescription = when {
                item.button -> binding.root.context.getString(R.string.cd_add_color)
                item.checked -> binding.root.context.getString(R.string.cd_selected_color)
                else -> null
            }
        }

        companion object {
            fun from(parent: ViewGroup, onItemClick: (position: Int) -> Unit): ColorViewHolder {
                val binding = ItemColorAdapterBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return ColorViewHolder(binding, onItemClick)
            }
        }
    }
}

private val diffCallback = object : DiffUtil.ItemCallback<ColorItem>() {
    override fun areItemsTheSame(oldItem: ColorItem, newItem: ColorItem): Boolean {
        return oldItem.color == newItem.color
    }

    override fun areContentsTheSame(oldItem: ColorItem, newItem: ColorItem): Boolean {
        return oldItem == newItem
    }
}