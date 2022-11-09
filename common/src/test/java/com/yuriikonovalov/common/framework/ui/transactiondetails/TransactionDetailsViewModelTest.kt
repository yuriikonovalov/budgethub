package com.yuriikonovalov.common.framework.ui.transactiondetails

import com.google.common.truth.Truth.*
import com.yuriikonovalov.common.application.usecases.DeleteTransaction
import com.yuriikonovalov.common.application.usecases.GetTransaction
import com.yuriikonovalov.shared_test.fakes.usecase.FakeDeleteTransaction
import com.yuriikonovalov.shared_test.fakes.usecase.FakeGetTransaction
import com.yuriikonovalov.common.framework.utils.EspressoIdlingResource
import com.yuriikonovalov.common.presentation.transactiondetails.TransactionDetailsEvent
import com.yuriikonovalov.common.presentation.transactiondetails.TransactionDetailsIntent
import com.yuriikonovalov.shared_test.MainCoroutineRule
import com.yuriikonovalov.shared_test.model.transaction
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock

@OptIn(ExperimentalCoroutinesApi::class)
class TransactionDetailsViewModelTest {

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private val idlingResource: EspressoIdlingResource = mock()

    private fun initSUT(
        id: Long = -1,
        getTransaction: GetTransaction = FakeGetTransaction(transaction),
        deleteTransaction: DeleteTransaction = FakeDeleteTransaction()
    ): TransactionDetailsViewModel {
        return TransactionDetailsViewModel(id, getTransaction, deleteTransaction, idlingResource)
    }

    @Test
    fun `open transaction details - details are displayed`() = runTest {
        // BEFORE
        val transaction = transaction
        val sut = initSUT(id = transaction.id, getTransaction = FakeGetTransaction(transaction))

        // WHEN
        runCurrent()

        // THEN
        val actual = sut.stateFlow.value.transaction
        assertThat(actual).isEqualTo(transaction)
    }


    @Test
    fun `when transaction is deleted - navigate up`() = runTest {
        // BEFORE
        val sut = initSUT()

        // WHEN
        // loading the details of a transaction
        runCurrent()
        sut.handleIntent(TransactionDetailsIntent.DeleteTransaction)
        // deleting the transaction
        runCurrent()

        // THEN
        val actual = sut.eventFlow.value
        assertThat(actual).isEqualTo(TransactionDetailsEvent.NavigateUp)
    }

    @Test
    fun `when edit transaction pressed - event value is set to edit transaction`() = runTest {
        // BEFORE
        val sut = initSUT()

        // WHEN
        runCurrent()
        sut.handleIntent(TransactionDetailsIntent.EditTransaction)

        // THEN
        val actual = sut.eventFlow.value
        assertThat(actual).isInstanceOf(TransactionDetailsEvent.EditTransaction::class.java)
    }
}