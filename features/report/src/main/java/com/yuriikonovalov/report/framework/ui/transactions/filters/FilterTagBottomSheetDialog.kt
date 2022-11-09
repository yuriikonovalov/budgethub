package com.yuriikonovalov.report.framework.ui.transactions.filters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.yuriikonovalov.common.application.entities.Tag
import com.yuriikonovalov.common.presentation.model.TagItem
import com.yuriikonovalov.report.databinding.BottomSheetDialogFilterTagBinding

class FilterTagBottomSheetDialog : BottomSheetDialogFragment() {
    private lateinit var binding: BottomSheetDialogFilterTagBinding
    private val args: FilterTagBottomSheetDialogArgs by navArgs()

    private val tagFilterAdapter = TagFilterAdapter { tag ->
        val newSelection = args.selection.toList().updateSelection(tag)
        setFragmentResult(newSelection)
        dismiss()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetDialogFilterTagBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.bindCloseButton()
        binding.bindClearFilterButton()
        binding.bindTags()
    }

    private fun BottomSheetDialogFilterTagBinding.bindTags() {
        tagList.layoutManager = LinearLayoutManager(requireContext())
        tagList.adapter = tagFilterAdapter

        val tagItems = args.tags.map {
            TagItem(tag = it, checked = args.selection.contains(it))
        }
        tagFilterAdapter.submitList(tagItems)
        emptyPlaceholder.isVisible = args.tags.isEmpty()
    }

    private fun BottomSheetDialogFilterTagBinding.bindClearFilterButton() {
        clearFilterButton.isGone = args.selection.isEmpty()
        clearFilterButton.setOnClickListener {
            setFragmentResult(emptyList())
            dismiss()
        }
    }

    private fun BottomSheetDialogFilterTagBinding.bindCloseButton() {
        closeButton.setOnClickListener {
            dismiss()
        }
    }

    private fun List<Tag>.updateSelection(tag: Tag): List<Tag> {
        return if (this.contains(tag)) {
            this.minusElement(tag)
        } else {
            this.plusElement(tag)
        }
    }

    private fun setFragmentResult(tags: List<Tag>) {
        setFragmentResult(
            REQUEST_KEY,
            bundleOf(
                BUNDLE_KEY to tags
            )
        )
    }

    companion object {
        const val REQUEST_KEY = "FILTER_TAG_REQUEST_KEY"
        const val BUNDLE_KEY = "FILTER_TAG_BUNDLE_KEY"
    }
}