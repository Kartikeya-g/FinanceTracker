package com.financetracker.app.ui.screens.cards

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.financetracker.app.data.model.Card
import com.financetracker.app.data.model.CardNetwork
import com.financetracker.app.data.model.CardType
import com.financetracker.app.data.repository.FinanceRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CardViewModel(private val repository: FinanceRepository) : ViewModel() {

    val cards: StateFlow<List<Card>> = repository.getAllCards()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addCard(
        bankName: String,
        nickname: String,
        cardType: CardType,
        network: CardNetwork,
        lastFourDigits: String,
        colorHex: String,
        creditLimit: Double?
    ) {
        viewModelScope.launch {
            repository.addCard(
                Card(
                    bankName = bankName,
                    nickname = nickname,
                    cardType = cardType,
                    network = network,
                    lastFourDigits = lastFourDigits,
                    colorHex = colorHex,
                    creditLimit = creditLimit
                )
            )
        }
    }

    fun deleteCard(card: Card) {
        viewModelScope.launch { repository.deleteCard(card) }
    }

    class Factory(private val repository: FinanceRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            CardViewModel(repository) as T
    }
}
