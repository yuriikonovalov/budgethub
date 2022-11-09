package com.yuriikonovalov.common.framework.ui.addedittransaction.date

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.yuriikonovalov.common.databinding.BottomSheetDialogDateBinding
import com.yuriikonovalov.common.framework.common.extentions.collectEvent
import com.yuriikonovalov.common.framework.common.extentions.launchSafely
import com.yuriikonovalov.common.framework.utils.date.DateFormat
import com.yuriikonovalov.common.framework.utils.date.DateFormat.toCalendarViewDateLong
import com.yuriikonovalov.common.presentation.addedittransaction.date.DateEvent
import com.yuriikonovalov.common.presentation.addedittransaction.date.DateIntent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import java.time.OffsetDateTime
import javax.inject.Inject

@AndroidEntryPoint
class DateBottomSheetDialog(
    private val minDate: OffsetDateTime? = null,
    private val date: OffsetDateTime? = null
) : BottomSheetDialogFragment() {
    private lateinit var binding: BottomSheetDialogDateBinding
    private var listener: Listener? = null

    @Inject
    lateinit var viewModelFactory: DateViewModel.Factory
    private val viewModel: DateViewModel by viewModels {
        DateViewModel.provideFactory(viewModelFactory, minDate, date)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetDialogDateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.bindCalendar()
        binding.bindPositiveButton()

        collectEvent(viewModel.eventFlow, viewModel.eventConsumer) { event ->
            when (event) {
                is DateEvent.PositiveButtonClick -> onPositiveButtonClickEvent(event.date)
            }
        }
    }

    private fun onPositiveButtonClickEvent(date: OffsetDateTime) {
        listener?.onPositiveButtonClick(date)
        dialog?.dismiss()
    }

    private fun BottomSheetDialogDateBinding.bindCalendar() {
        calendar.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val newDate = DateFormat.offsetDateTimeFromCalendarViewDate(year, month, dayOfMonth)
            viewModel.handleIntent(DateIntent.ChangeDate(newDate))
        }
        launchSafely {
            viewModel.stateFlow
                .map { it.minDate }
                .filterNotNull()
                .distinctUntilChanged()
                .collect { date ->
                    calendar.minDate = date.toCalendarViewDateLong()
                }
        }
        launchSafely {
            viewModel.stateFlow
                .map { it.maxDate }
                .distinctUntilChanged()
                .collect { date ->
                    calendar.maxDate = date.toCalendarViewDateLong()
                }
        }

        launchSafely {
            viewModel.stateFlow
                .map { it.date }
                .distinctUntilChanged()
                .collect { date ->
                    try {
                        calendar.date = date.toCalendarViewDateLong()
                    } catch (e: IllegalArgumentException) {
                        // catches if longDate is before the minimal or after the maximal date
                        val today = OffsetDateTime.now()
                        calendar.date = today.toCalendarViewDateLong()
                    }
                }
        }
    }

    private fun BottomSheetDialogDateBinding.bindPositiveButton() {
        positiveButton.setOnClickListener {
            viewModel.handleIntent(DateIntent.ClickPositiveButton)
        }
    }

    fun setOnPositiveClickListener(listener: Listener) {
        this.listener = listener
    }

    fun interface Listener {
        fun onPositiveButtonClick(date: OffsetDateTime)
    }

    companion object {
        const val TAG = "DateBottomSheetDialog"
    }
}