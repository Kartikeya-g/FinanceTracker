package com.financetracker.app.ui.screens.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.financetracker.app.data.model.Transaction
import com.financetracker.app.data.model.TransactionCategory
import com.financetracker.app.data.model.TransactionType
import com.financetracker.app.data.repository.FinanceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

class TransactionViewModel(private val repository: FinanceRepository) : ViewModel() {

    val allTransactions: StateFlow<List<Transaction>> = repository.getAllTransactions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedTransaction = MutableStateFlow<Transaction?>(null)
    val selectedTransaction: StateFlow<Transaction?> = _selectedTransaction

    fun loadTransaction(id: String) {
        viewModelScope.launch {
            _selectedTransaction.value = repository.getTransactionById(id)
        }
    }

    fun addManualTransaction(
        title: String,
        amount: Double,
        type: TransactionType,
        category: TransactionCategory,
        cardId: String?,
        note: String?
    ) {
        viewModelScope.launch {
            repository.addTransaction(
                Transaction(
                    id = UUID.randomUUID().toString(),
                    title = title,
                    amount = amount,
                    type = type,
                    category = category,
                    cardId = cardId,
                    note = note,
                    source = com.financetracker.app.data.model.TransactionSource.MANUAL
                )
            )
        }
    }

    /** Lets the user rename the auto-detected expense and adjust category/notes/card. */
    fun updateTransaction(
        transaction: Transaction,
        newTitle: String,
        newCategory: TransactionCategory,
        newNote: String?,
        newCardId: String?
    ) {
        viewModelScope.launch {
            repository.updateTransaction(
                transaction.copy(
                    title = newTitle,
                    category = newCategory,
                    note = newNote,
                    cardId = newCardId
                )
            )
        }
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch { repository.deleteTransaction(transaction) }
    }

    class Factory(private val repository: FinanceRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            TransactionViewModel(repository) as T
    }
}
