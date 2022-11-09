package com.yuriikonovalov.report.framework.ui.transactions.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.navGraphViewModels
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.yuriikonovalov.common.framework.common.extentions.getSerializableCompat
import com.yuriikonovalov.common.framework.common.extentions.launchSafely
import com.yuriikonovalov.report.R
import com.yuriikonovalov.report.databinding.FragmentListBinding
import com.yuriikonovalov.report.framework.ui.transactions.TransactionsViewModel
import com.yuriikonovalov.report.presentation.transactions.TransactionsIntent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest

@ExperimentalCoroutinesApi
class ListFragment : Fragment() {
    private val viewModel: TransactionsViewModel by navGraphViewModels(R.id.transactions_fragment)
    private lateinit var binding: FragmentListBinding
    private val transactionAdapter by lazy {
        TransactionPagingAdapter { id ->
            viewModel.handleIntent(TransactionsIntent.OpenTransactionDetails(id))
        }
    }
    private val transferAdapter by lazy {
        TransferPagingAdapter { id ->
            viewModel.handleIntent(TransactionsIntent.OpenTransferDetails(id))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val type = requireArguments().getSerializableCompat(TYPE_KEY, Type::class.java)!!
        binding.bindList(type)
        binding.bindPlaceholder(type)
    }

    private fun FragmentListBinding.bindList(type: Type) {
        list.layoutManager = LinearLayoutManager(requireContext())
        list.adapter = type.toAdapter()

        when (type) {
            Type.TRANSACTION -> {
                launchSafely {
                    viewModel.transactionPagingData.collectLatest { data ->
                        transactionAdapter.submitData(data)
                    }
                }
            }

            Type.TRANSFER -> {
                launchSafely {
                    viewModel.transferPagingData.collectLatest { data ->
                        transferAdapter.submitData(data)
                    }
                }
            }
        }
    }

    private fun FragmentListBinding.bindPlaceholder(type: Type) {
        launchSafely {
            type.toAdapter().loadStateFlow.collect {
                placeholderViews.isVisible = type.toAdapter().itemCount == 0
            }
        }
    }

    private fun Type.toAdapter(): PagingDataAdapter<*, *> {
        return when (this) {
            Type.TRANSACTION -> transactionAdapter
            Type.TRANSFER -> transferAdapter
        }
    }

    enum class Type {
        TRANSACTION, TRANSFER
    }

    companion object {
        private const val TYPE_KEY = "KEY_TYPE"

        fun newInstance(type: Type): ListFragment {
            return ListFragment().apply {
                arguments = bundleOf(TYPE_KEY to type)
            }
        }
    }
}