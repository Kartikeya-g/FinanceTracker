package com.financetracker.app.ui.screens.transactions

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.financetracker.app.ui.components.TransactionRow

@Composable
fun TransactionListScreen(
    viewModel: TransactionViewModel,
    currencySymbol: String,
    onTransactionClick: (String) -> Unit,
    onAddClick: () -> Unit
) {
    val transactions by viewModel.allTransactions.collectAsState()
    var query by remember { mutableStateOf("") }

    val filtered = remember(transactions, query) {
        if (query.isBlank()) transactions
        else transactions.filter {
            it.title.contains(query, ignoreCase = true) ||
                (it.merchant?.contains(query, ignoreCase = true) == true)
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClick) {
                Icon(Icons.Filled.Add, contentDescription = "Add transaction")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 12.dp)
        ) {
            item {
                Text(text = "All Transactions", style = MaterialTheme.typography.headlineMedium)
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    label = { Text("Search by name or merchant") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                )
            }
            items(filtered, key = { it.id }) { transaction ->
                TransactionRow(
                    transaction = transaction,
                    currencySymbol = currencySymbol,
                    onClick = { onTransactionClick(transaction.id) }
                )
            }
        }
    }
}
