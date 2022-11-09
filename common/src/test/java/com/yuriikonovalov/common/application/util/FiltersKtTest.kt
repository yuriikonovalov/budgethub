package com.yuriikonovalov.common.application.util

import com.yuriikonovalov.common.application.entities.account.Account
import com.yuriikonovalov.shared_test.model.account
import com.yuriikonovalov.shared_test.model.transaction
import com.yuriikonovalov.shared_test.model.transfer
import org.junit.Assert.assertEquals
import org.junit.Test

class FiltersKtTest {
    private var accounts = listOf(
        account.copy(id = 1, name = "A1"),
        account.copy(id = 2, name = "A2"),
        account.copy(id = 3, name = "A3"),
    )

    @Test
    fun `filtered transactions contain only accounts from a filter list`() {
        // BEFORE
        // List of transactions to filter.
        val transactions = listOf(
            transaction.copy(id = 1, accounts[0]),
            transaction.copy(id = 2, accounts[0]),
            transaction.copy(id = 3, accounts[1]),
            transaction.copy(id = 4, accounts[2]),
            transaction.copy(id = 5, accounts[2]),
        )

        // WHEN
        // Take the first two accounts as a filter list.
        val filter = accounts.take(2)
        val filteredTransactions = transactions.filterByAccounts(filter)

        // THEN
        val filteredAccounts = filteredTransactions.map { it.account }
        assertEquals(true, filter.containsAll(filteredAccounts))
    }

    @Test
    fun `filtering by an empty filter list returns all transactions`() {
        // BEFORE
        // List of transactions to filter.
        val transactions = listOf(
            transaction.copy(id = 1, accounts[0]),
            transaction.copy(id = 2, accounts[0]),
            transaction.copy(id = 3, accounts[1]),
            transaction.copy(id = 4, accounts[2]),
            transaction.copy(id = 5, accounts[2]),
        )
        // WHEN
        val filter = emptyList<Account>()
        // Filtered transactions.
        val filteredTransactions = transactions.filterByAccounts(filter)

        // THEN
        // Should return the original list when a filter list is empty.
        assertEquals(transactions, filteredTransactions)
    }

    @Test
    fun `filtering transactions by a filter list with accounts that don't relate to any transaction returns empty list`() {
        // BEFORE
        // List of transactions to filter.
        val transactions = listOf(
            transaction.copy(id = 1, accounts[0]),
            transaction.copy(id = 2, accounts[0]),
            transaction.copy(id = 3, accounts[1]),
            transaction.copy(id = 4, accounts[2]),
            transaction.copy(id = 5, accounts[2]),
        )
        // WHEN
        // Take a distinct account from the transactions' accounts.
        val filter = listOf(account.copy(id = 123))
        // Filtered transactions.
        val filteredTransactions = transactions.filterByAccounts(filter)

        // THEN
        // Should return an empty list as there's no any matching account
        // between the transactions' accounts and the filter accounts.
        assertEquals(true, filteredTransactions.isEmpty())
    }

    @Test
    fun `filtered transfers contain only transfers where accountFrom is one from a filter list`() {
        // BEFORE
        // List of transactions to filter.
        val transfers = listOf(
            transfer.copy(id = 1, accountFrom = accounts[0]),
            transfer.copy(id = 2, accountFrom = accounts[0]),
            transfer.copy(id = 3, accountFrom = accounts[1]),
            transfer.copy(id = 4, accountFrom = accounts[2]),
            transfer.copy(id = 5, accountFrom = accounts[2]),
        )

        // WHEN
        // Take the first two accounts as a filter list.
        val filter = accounts.take(2)
        val filteredTransfers = transfers.filterByAccountsFrom(filter)

        // THEN
        val filteredAccounts = filteredTransfers.map { it.accountFrom }
        assertEquals(true, filter.containsAll(filteredAccounts))
    }

    @Test
    fun `filtered transfers contain only transfers where accountTo is one from a filter list`() {
        // BEFORE
        // List of transactions to filter.
        val transfers = listOf(
            transfer.copy(id = 1, accountTo = accounts[0]),
            transfer.copy(id = 2, accountTo = accounts[0]),
            transfer.copy(id = 3, accountTo = accounts[1]),
            transfer.copy(id = 4, accountTo = accounts[2]),
            transfer.copy(id = 5, accountTo = accounts[2]),
        )

        // WHEN
        // Take the first two accounts as a filter list.
        val filter = accounts.take(2)
        val filteredTransfers = transfers.filterByAccountsTo(filter)

        // THEN
        val filteredAccounts = filteredTransfers.map { it.accountTo }
        assertEquals(true, filter.containsAll(filteredAccounts))
    }
}