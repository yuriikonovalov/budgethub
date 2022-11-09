package com.yuriikonovalov.report.framework.ui.transactions.filters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.yuriikonovalov.common.application.entities.Tag
import com.yuriikonovalov.common.presentation.model.TagItem
import com.yuriikonovalov.report.databinding.ItemTagFilterAdapterBinding

class TagFilterAdapter(private val onItemClick: (tag: Tag) -> Unit) :
    ListAdapter<TagItem, TagFilterAdapter.TagViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagViewHolder {
        return TagViewHolder.from(parent, onItemClick = { position ->
            onItemClick(currentList[position].tag)
        })
    }

    override fun onBindViewHolder(holder: TagViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class TagViewHolder private constructor(
        private val binding: ItemTagFilterAdapterBinding,
        private val onItemClick: (position: Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                onItemClick(bindingAdapterPosition)
            }
        }

        fun bind(item: TagItem) {
            binding.name.text = item.tag.name
            binding.checkbox.isChecked = item.checked
        }

        companion object {
            fun from(
                parent: ViewGroup,
                onItemClick: (position: Int) -> Unit
            ): TagViewHolder {
                val binding = ItemTagFilterAdapterBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return TagViewHolder(binding, onItemClick)
            }
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<TagItem>() {
        override fun areItemsTheSame(oldItem: TagItem, newItem: TagItem): Boolean {
            return oldItem.tag.id == newItem.tag.id
        }

        override fun areContentsTheSame(oldItem: TagItem, newItem: TagItem): Boolean {
            return oldItem == newItem
        }
    }
}