package com.financetracker.app.ui.screens.cards

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.financetracker.app.ui.components.CardVisual

@Composable
fun CardsScreen(
    viewModel: CardViewModel,
    onAddCardClick: () -> Unit
) {
    val cards by viewModel.cards.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onAddCardClick) {
                Icon(Icons.Filled.Add, contentDescription = "Add card")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            item {
                Text(text = "Your Cards", style = MaterialTheme.typography.headlineMedium)
                Text(
                    text = "Add every debit or credit card you want tracked",
                    style = MaterialTheme.typography.bodyMedium,
                    color = androidx.compose.ui.graphics.Color.Gray
                )
            }
            items(cards, key = { it.id }) { card ->
                CardVisual(
                    card = card,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
    }
}
