package com.yuriikonovalov.shared_test.fakes.datasource

import androidx.paging.PagingData
import com.yuriikonovalov.common.application.entities.Transfer
import com.yuriikonovalov.common.data.local.TransfersLocalDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import java.time.OffsetDateTime

class FakeTransfersLocalDataSource : TransfersLocalDataSource {
    private val transfers = mutableListOf<Transfer>()
    private val transferFlow: MutableStateFlow<List<Transfer>> =
        MutableStateFlow(transfers)


    suspend fun setData(data: List<Transfer>) {
        this.transfers.clear()
        this.transfers.addAll(data)
        transferFlow.emit(transfers)
    }


    override suspend fun deleteAll() {
        transfers.clear()
        transferFlow.emit(transfers)
    }

    override suspend fun saveTransfer(transfer: Transfer): Long {
        val isAlreadyInCollection = transfers.map { it.id }.contains(transfer.id)
        if (isAlreadyInCollection) {
            transfers.remove(transfer)
        }
        transfers.add(transfer)
        transferFlow.emit(transfers)
        return transfer.id
    }

    override suspend fun delete(id: Long) {
        val deleted = transfers.removeIf { it.id == id }
        if (deleted) {
            transferFlow.emit(transfers)
        }
    }

    override suspend fun updateTransfer(transfer: Transfer) {
        transfers.removeIf { it.id == transfer.id }.also { success ->
            if (success) {
                transfers.add(transfer)
                transferFlow.emit(transfers)
            }
        }
    }

    override fun getTransferById(id: Long): Flow<Transfer?> {
        return transferFlow.map { list ->
            list.find { it.id == id }
        }
    }

    override fun getTransfersForPeriod(
        startPeriod: OffsetDateTime?,
        endPeriod: OffsetDateTime?
    ): Flow<List<Transfer>> {
        return when {
            startPeriod != null && endPeriod != null -> {
                transferFlow.map { list ->
                    list.filter {
                        it.date.isAfter(startPeriod) && it.date.isBefore(endPeriod)
                    }
                }
            }
            startPeriod == null && endPeriod != null -> {
                transferFlow.map { list ->
                    list.filter {
                        it.date.isBefore(endPeriod)
                    }
                }
            }
            startPeriod != null && endPeriod == null -> {
                transferFlow.map { list ->
                    list.filter {
                        it.date.isAfter(startPeriod)
                    }
                }
            }
            else -> transferFlow
        }
    }


    override suspend fun getTransferAmountForAccountBeforeDate(
        accountId: Long,
        endPeriod: OffsetDateTime
    ): Double {
        val expenses = transfers
            .filter { it.accountFrom.id == accountId && it.date.isBefore(endPeriod) }
            .sumOf { it.amountFrom }

        val incomes = transfers
            .filter { it.accountTo.id == accountId && it.date.isBefore(endPeriod) }
            .sumOf { it.amountTo }
        return incomes - expenses
    }

    override suspend fun getTransferAmountForAccount(accountId: Long): Double {
        val expenses = transfers
            .filter { it.accountFrom.id == accountId }
            .sumOf { it.amountFrom }

        val incomes = transfers
            .filter { it.accountTo.id == accountId }
            .sumOf { it.amountTo }
        return incomes - expenses
    }

    override fun getTransfersPaged(
        pageSize: Int,
        startPeriod: OffsetDateTime?,
        endPeriod: OffsetDateTime?
    ): Flow<PagingData<Transfer>> {
        TODO("Not yet implemented")
    }

}