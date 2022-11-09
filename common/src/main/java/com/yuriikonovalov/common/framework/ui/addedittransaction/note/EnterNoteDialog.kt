package com.yuriikonovalov.common.framework.ui.addedittransaction.note

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.yuriikonovalov.common.R
import com.yuriikonovalov.common.databinding.DialogEnterNoteBinding
import com.yuriikonovalov.common.framework.common.extentions.collectEvent
import com.yuriikonovalov.common.presentation.addedittransaction.note.EnterNoteEvent
import com.yuriikonovalov.common.presentation.addedittransaction.note.EnterNoteIntent
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class EnterNoteDialog(private val note: String? = null) : DialogFragment() {
    private lateinit var binding: DialogEnterNoteBinding
    private var listener: Listener? = null

    @Inject
    lateinit var viewModelFactory: EnterNoteViewModel.Factory
    private val viewModel: EnterNoteViewModel by viewModels {
        EnterNoteViewModel.provideFactory(viewModelFactory, note)
    }

    override fun getTheme(): Int {
        return R.style.Theme_DialogFragment
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogEnterNoteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.bindNegativeButton()
        binding.bindPositiveButton()
        binding.bindInput()
        collectEvents()
    }

    override fun onStart() {
        super.onStart()
        softInputModeToVisible()
    }

    private fun collectEvents() {
        collectEvent(viewModel.eventFlow, viewModel.eventConsumer) { event ->
            when (event) {
                is EnterNoteEvent.PositiveButtonClick -> onPositiveButtonClickEvent(event.input)
                is EnterNoteEvent.NegativeButtonClick -> onNegativeButtonClickEvent()
                is EnterNoteEvent.SetText -> onSetTextEvent(event.input)
            }
        }
    }

    private fun onSetTextEvent(receivedInput: String?) {
        receivedInput?.let {
            binding.input.setText(it)
            binding.input.setSelection(it.length)
        }
    }

    private fun onPositiveButtonClickEvent(input: String?) {
        listener?.onDialogPositiveClick(input)
        dialog?.dismiss()
    }

    private fun onNegativeButtonClickEvent() {
        dialog?.dismiss()
    }

    private fun DialogEnterNoteBinding.bindNegativeButton() {
        toolbar.setNavigationOnClickListener {
            viewModel.handleIntent(EnterNoteIntent.ClickNegativeButton)
        }
    }

    private fun DialogEnterNoteBinding.bindInput() {
        input.doOnTextChanged { text, _, _, _ ->
            text?.toString()?.let {
                viewModel.handleIntent(EnterNoteIntent.ChangeInput(it))
            }
        }
    }

    private fun DialogEnterNoteBinding.bindPositiveButton() {
        positiveButton.setOnClickListener {
            viewModel.handleIntent(EnterNoteIntent.ClickPositiveButton)
        }
    }

    private fun softInputModeToVisible() {
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        binding.input.requestFocus()
    }

    fun setOnDialogPositiveClickListener(listener: Listener) {
        this.listener = listener
    }

    fun interface Listener {
        fun onDialogPositiveClick(input: String?)
    }

    companion object {
        const val TAG = "EnterNoteDialog"
    }

}