package com.yuriikonovalov.common.framework.ui.addedittransaction.tag

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.yuriikonovalov.common.application.entities.Tag
import com.yuriikonovalov.common.databinding.ItemTagBottomSheetAdapterBinding
import com.yuriikonovalov.common.presentation.model.TagItem

class TagBottomSheetAdapter(
    private val onItemClick: (tag: Tag) -> Unit,
    private val onItemLongClick: (tag: Tag) -> Unit
) : ListAdapter<TagItem, TagBottomSheetAdapter.ItemViewHolder>(tagDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder.from(parent,
            onItemClick = { position ->
                onItemClick(currentList[position].tag)
            },
            onItemLongClick = { position ->
                onItemLongClick(currentList[position].tag)
            })
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ItemViewHolder private constructor(
        private val binding: ItemTagBottomSheetAdapterBinding,
        private val onItemClick: (position: Int) -> Unit,
        private val onItemLongClick: (position: Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.chip.apply {
                setOnClickListener {
                    onItemClick(bindingAdapterPosition)
                }
                setOnLongClickListener {
                    onItemLongClick(bindingAdapterPosition)
                    true
                }
            }
        }

        fun bind(item: TagItem) {
            binding.chip.text = item.tag.name
            binding.chip.isChecked = item.checked
        }

        companion object {
            fun from(
                parent: ViewGroup,
                onItemClick: (position: Int) -> Unit,
                onItemLongClick: (position: Int) -> Unit
            ): ItemViewHolder {
                val binding = ItemTagBottomSheetAdapterBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return ItemViewHolder(binding, onItemClick, onItemLongClick)
            }
        }
    }

    companion object {
        private val tagDiffCallback = object : DiffUtil.ItemCallback<TagItem>() {
            override fun areItemsTheSame(oldItem: TagItem, newItem: TagItem): Boolean {
                return oldItem.tag.id == newItem.tag.id
            }

            override fun areContentsTheSame(oldItem: TagItem, newItem: TagItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}