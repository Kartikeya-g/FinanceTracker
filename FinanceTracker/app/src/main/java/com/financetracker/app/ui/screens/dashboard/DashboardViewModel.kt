package com.financetracker.app.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.financetracker.app.data.model.Card
import com.financetracker.app.data.model.Transaction
import com.financetracker.app.data.model.UserProfile
import com.financetracker.app.data.repository.FinanceRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class DashboardUiState(
    val totalExpenses: Double = 0.0,
    val totalCredits: Double = 0.0,
    val recentTransactions: List<Transaction> = emptyList(),
    val cards: List<Card> = emptyList(),
    val profile: UserProfile? = null
)

class DashboardViewModel(private val repository: FinanceRepository) : ViewModel() {

    val uiState: StateFlow<DashboardUiState> = combine(
        repository.getTotalExpenses(),
        repository.getTotalCredits(),
        repository.getRecentTransactions(8),
        repository.getAllCards(),
        repository.observeProfile()
    ) { expenses, credits, recent, cards, profile ->
        DashboardUiState(expenses, credits, recent, cards, profile)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DashboardUiState())

    class Factory(private val repository: FinanceRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            DashboardViewModel(repository) as T
    }
}
