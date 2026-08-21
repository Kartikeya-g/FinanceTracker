package com.financetracker.app.ui.screens.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.LazyRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.financetracker.app.ui.components.CardVisual
import com.financetracker.app.ui.components.StatCard
import com.financetracker.app.ui.components.TransactionRow

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onTransactionClick: (String) -> Unit,
    onSeeAllTransactions: () -> Unit,
    onSeeAllCards: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val currency = state.profile?.currencySymbol ?: "\u20B9"

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 16.dp)
    ) {
        item {
            Text(
                text = "Hi${state.profile?.name?.let { if (it.isNotBlank()) ", $it" else "" } ?: ""} \uD83D\uDC4B",
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                text = "Here's your financial overview",
                style = MaterialTheme.typography.bodyMedium,
                color = androidx.compose.ui.graphics.Color.Gray
            )
            Spacer(modifier = Modifier.height(20.dp))
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard(
                    label = "Total Expenses",
                    amount = "$currency${"%.2f".format(state.totalExpenses)}",
                    isPositive = false,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    label = "Total Credits",
                    amount = "$currency${"%.2f".format(state.totalCredits)}",
                    isPositive = true,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Your Cards", style = MaterialTheme.typography.titleMedium)
                Text(
                    text = "See all",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(4.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(state.cards) { card ->
                    CardVisual(card = card, modifier = Modifier.width(280.dp))
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Recent Transactions", style = MaterialTheme.typography.titleMedium)
                Text(
                    text = "See all",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(4.dp)
                )
            }
        }

        items(state.recentTransactions) { transaction ->
            TransactionRow(
                transaction = transaction,
                currencySymbol = currency,
                onClick = { onTransactionClick(transaction.id) }
            )
        }
    }
}
