package com.financetracker.app.ui.screens.transactions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.financetracker.app.data.model.TransactionCategory
import com.financetracker.app.data.model.TransactionType

@Composable
fun AddTransactionScreen(
    viewModel: TransactionViewModel,
    onSaved: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("") }
    var type by remember { mutableStateOf(TransactionType.EXPENSE) }
    var category by remember { mutableStateOf(TransactionCategory.OTHER) }
    var note by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Text(text = "Add Transaction", style = MaterialTheme.typography.headlineMedium)

        Row(modifier = Modifier.padding(top = 16.dp)) {
            FilterChip(
                selected = type == TransactionType.EXPENSE,
                onClick = { type = TransactionType.EXPENSE },
                label = { Text("Expense") },
                modifier = Modifier.padding(end = 8.dp)
            )
            FilterChip(
                selected = type == TransactionType.CREDIT,
                onClick = { type = TransactionType.CREDIT },
                label = { Text("Credit") }
            )
        }

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
        )

        OutlinedTextField(
            value = amountText,
            onValueChange = { amountText = it.filter { c -> c.isDigit() || c == '.' } },
            label = { Text("Amount") },
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
        )

        OutlinedTextField(
            value = note,
            onValueChange = { note = it },
            label = { Text("Notes (optional)") },
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
        )

        Text(
            text = "Category",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(top = 20.dp, bottom = 8.dp)
        )
        LazyRow {
            items(TransactionCategory.values()) { cat ->
                FilterChip(
                    selected = category == cat,
                    onClick = { category = cat },
                    label = { Text(cat.name.lowercase().replaceFirstChar { it.uppercase() }) },
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
        }

        Button(
            onClick = {
                val amount = amountText.toDoubleOrNull() ?: return@Button
                if (title.isBlank() || amount <= 0.0) return@Button
                viewModel.addManualTransaction(
                    title = title,
                    amount = amount,
                    type = type,
                    category = category,
                    cardId = null,
                    note = note.ifBlank { null }
                )
                onSaved()
            },
            modifier = Modifier.fillMaxWidth().padding(top = 32.dp)
        ) { Text("Save Transaction") }
    }
}
