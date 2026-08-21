package com.financetracker.app.ui.screens.transactions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.financetracker.app.data.model.TransactionCategory

@Composable
fun EditTransactionScreen(
    viewModel: TransactionViewModel,
    transactionId: String,
    currencySymbol: String,
    onSaved: () -> Unit,
    onDeleted: () -> Unit
) {
    androidx.compose.runtime.LaunchedEffect(transactionId) {
        viewModel.loadTransaction(transactionId)
    }
    val transaction by viewModel.selectedTransaction.collectAsState()

    transaction?.let { txn ->
        var title by remember(txn.id) { mutableStateOf(txn.title) }
        var note by remember(txn.id) { mutableStateOf(txn.note ?: "") }
        var category by remember(txn.id) { mutableStateOf(txn.category) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            Text(text = "Edit Transaction", style = MaterialTheme.typography.headlineMedium)
            Text(
                text = "${if (txn.type == com.financetracker.app.data.model.TransactionType.EXPENSE) "-" else "+"}$currencySymbol${"%.2f".format(txn.amount)}",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            if (txn.rawSmsBody != null) {
                Text(
                    text = "Auto-detected from bank SMS",
                    style = MaterialTheme.typography.labelSmall,
                    color = androidx.compose.ui.graphics.Color.Gray
                )
            }

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Expense / Credit name") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            )

            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Notes (optional)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
            )

            Text(
                text = "Category",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 20.dp, bottom = 8.dp)
            )
            LazyRow {
                items(TransactionCategory.values()) { cat ->
                    AssistChip(
                        onClick = { category = cat },
                        label = { Text(cat.name.lowercase().replaceFirstChar { it.uppercase() }) },
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        viewModel.deleteTransaction(txn)
                        onDeleted()
                    },
                    modifier = Modifier.weight(1f)
                ) { Text("Delete") }

                Button(
                    onClick = {
                        viewModel.updateTransaction(
                            transaction = txn,
                            newTitle = title.ifBlank { txn.title },
                            newCategory = category,
                            newNote = note.ifBlank { null },
                            newCardId = txn.cardId
                        )
                        onSaved()
                    },
                    modifier = Modifier.weight(1f)
                ) { Text("Save") }
            }
        }
    }
}
