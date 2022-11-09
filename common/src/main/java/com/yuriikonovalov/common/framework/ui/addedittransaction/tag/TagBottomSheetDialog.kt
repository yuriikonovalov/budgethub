package com.yuriikonovalov.common.framework.ui.addedittransaction.tag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.yuriikonovalov.common.application.entities.Tag
import com.yuriikonovalov.common.databinding.BottomSheetDialogTagBinding
import com.yuriikonovalov.common.framework.common.extentions.collectEvent
import com.yuriikonovalov.common.framework.common.extentions.launchSafely
import com.yuriikonovalov.common.presentation.addedittransaction.tag.TagEvent
import com.yuriikonovalov.common.presentation.addedittransaction.tag.TagIntent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@AndroidEntryPoint
class TagBottomSheetDialog(private val tags: List<Tag>) : BottomSheetDialogFragment() {
    private lateinit var binding: BottomSheetDialogTagBinding

    @Inject
    lateinit var viewModelFactory: TagViewModel.Factory
    private val viewModel: TagViewModel by viewModels {
        TagViewModel.provideFactory(viewModelFactory, tags)
    }
    private var listener: OnPositiveButtonClickListener? = null
    private val tagAdapter: TagBottomSheetAdapter by lazy {
        TagBottomSheetAdapter(
            onItemClick = {
                viewModel.handleIntent(TagIntent.ClickTag(it))
            },
            onItemLongClick = {
                viewModel.handleIntent(TagIntent.DeleteTag(it))
            })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetDialogTagBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.bindTags()
        binding.bindNameInput()
        binding.bindAddButton()
        binding.bindEmptyPlaceholder()

        collectEvent(viewModel.eventFlow, viewModel.eventConsumer) { event ->
            when (event) {
                is TagEvent.ClearInput -> onClearInputEvent()
                is TagEvent.ReturnSelectedTags -> onReturnSelectedTagsEvent(event.tags)
            }
        }
    }

    private fun onReturnSelectedTagsEvent(tags: List<Tag>) {
        listener?.onPositiveButtonClick(tags)
    }

    private fun onClearInputEvent() {
        binding.nameInput.text = null
    }

    private fun BottomSheetDialogTagBinding.bindEmptyPlaceholder() {
        launchSafely {
            viewModel.stateFlow.map { it.emptyPlaceholderVisible }
                .distinctUntilChanged()
                .collect { visible ->
                    emptyPlaceholder.isVisible = visible
                }
        }
    }


    private fun BottomSheetDialogTagBinding.bindNameInput() {
        nameInput.doOnTextChanged { text, _, _, _ ->
            text?.toString()?.let { input ->
                viewModel.handleIntent(TagIntent.ChangeName(input))
            }
        }
    }

    private fun BottomSheetDialogTagBinding.bindAddButton() {
        addButton.setOnClickListener {
            viewModel.handleIntent(TagIntent.ClickAddButton)
        }
        launchSafely {
            viewModel.stateFlow.map { it.addButtonEnabled }
                .distinctUntilChanged()
                .collect { enabled ->
                    addButton.isEnabled = enabled
                }
        }
    }

    private fun BottomSheetDialogTagBinding.bindTags() {
        tags.layoutManager = FlexboxLayoutManager(requireContext(), FlexDirection.ROW)
        tags.adapter = tagAdapter
        launchSafely {
            viewModel.stateFlow.map { it.tagItems }
                .distinctUntilChanged()
                .collect(tagAdapter::submitList)
        }
    }

    fun setOnPositiveButtonClickListener(listener: OnPositiveButtonClickListener) {
        this.listener = listener
    }

    fun interface OnPositiveButtonClickListener {
        fun onPositiveButtonClick(tags: List<Tag>)
    }

    companion object {
        const val TAG = "TagBottomSheetDialog"

        fun show(
            fragmentManager: FragmentManager,
            tags: List<Tag>,
            onPositiveButtonClickListener: OnPositiveButtonClickListener
        ) {
            val dialog = TagBottomSheetDialog(tags)
            dialog.setOnPositiveButtonClickListener(onPositiveButtonClickListener)
            dialog.show(fragmentManager, TAG)
        }
    }
}