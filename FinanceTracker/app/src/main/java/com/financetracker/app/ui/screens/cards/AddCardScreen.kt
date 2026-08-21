package com.financetracker.app.ui.screens.cards

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.financetracker.app.data.model.CardNetwork
import com.financetracker.app.data.model.CardType

@Composable
fun AddCardScreen(
    viewModel: CardViewModel,
    onSaved: () -> Unit
) {
    var bankName by remember { mutableStateOf("") }
    var nickname by remember { mutableStateOf("") }
    var lastFour by remember { mutableStateOf("") }
    var creditLimitText by remember { mutableStateOf("") }
    var cardType by remember { mutableStateOf(CardType.DEBIT) }
    var network by remember { mutableStateOf(CardNetwork.VISA) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Text(text = "Add a Card", style = MaterialTheme.typography.headlineMedium)

        Row(modifier = Modifier.padding(top = 16.dp)) {
            FilterChip(
                selected = cardType == CardType.DEBIT,
                onClick = { cardType = CardType.DEBIT },
                label = { Text("Debit") },
                modifier = Modifier.padding(end = 8.dp)
            )
            FilterChip(
                selected = cardType == CardType.CREDIT,
                onClick = { cardType = CardType.CREDIT },
                label = { Text("Credit") }
            )
        }

        OutlinedTextField(
            value = bankName,
            onValueChange = { bankName = it },
            label = { Text("Bank name") },
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
        )
        OutlinedTextField(
            value = nickname,
            onValueChange = { nickname = it },
            label = { Text("Nickname (e.g. Salary Account)") },
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
        )
        OutlinedTextField(
            value = lastFour,
            onValueChange = { if (it.length <= 4) lastFour = it.filter { c -> c.isDigit() } },
            label = { Text("Last 4 digits") },
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
        )

        if (cardType == CardType.CREDIT) {
            OutlinedTextField(
                value = creditLimitText,
                onValueChange = { creditLimitText = it.filter { c -> c.isDigit() || c == '.' } },
                label = { Text("Credit limit (optional)") },
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
            )
        }

        Text(
            text = "Network",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(top = 20.dp, bottom = 8.dp)
        )
        Row {
            CardNetwork.values().forEach { net ->
                FilterChip(
                    selected = network == net,
                    onClick = { network = net },
                    label = { Text(net.name) },
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
        }

        Button(
            onClick = {
                if (bankName.isBlank() || lastFour.length != 4) return@Button
                viewModel.addCard(
                    bankName = bankName,
                    nickname = nickname.ifBlank { bankName },
                    cardType = cardType,
                    network = network,
                    lastFourDigits = lastFour,
                    colorHex = "#4C6EF5",
                    creditLimit = creditLimitText.toDoubleOrNull()
                )
                onSaved()
            },
            modifier = Modifier.fillMaxWidth().padding(top = 32.dp)
        ) { Text("Save Card") }
    }
}
