package com.yuriikonovalov.report.framework.ui.transactions.list

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class ListFragmentAdapter constructor(
    childFragmentManager: FragmentManager, lifecycle: Lifecycle
) : FragmentStateAdapter(childFragmentManager, lifecycle) {
    override fun getItemCount() = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ListFragment.newInstance(ListFragment.Type.TRANSACTION)
            1 -> ListFragment.newInstance(ListFragment.Type.TRANSFER)
            else -> {
                throw IllegalArgumentException("No fragment for such position.")
            }
        }
    }
}