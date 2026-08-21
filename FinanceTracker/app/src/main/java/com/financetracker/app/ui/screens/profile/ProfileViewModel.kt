package com.financetracker.app.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.financetracker.app.data.model.UserProfile
import com.financetracker.app.data.repository.FinanceRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProfileViewModel(private val repository: FinanceRepository) : ViewModel() {

    val profile: StateFlow<UserProfile?> = repository.observeProfile()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun saveProfile(
        name: String,
        email: String,
        currencySymbol: String,
        monthlyBudget: Double?,
        smsAutoDetectEnabled: Boolean,
        notificationsEnabled: Boolean
    ) {
        viewModelScope.launch {
            repository.saveProfile(
                UserProfile(
                    name = name,
                    email = email,
                    currencySymbol = currencySymbol,
                    monthlyBudget = monthlyBudget,
                    smsAutoDetectEnabled = smsAutoDetectEnabled,
                    notificationsEnabled = notificationsEnabled
                )
            )
        }
    }

    class Factory(private val repository: FinanceRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            ProfileViewModel(repository) as T
    }
}
