package com.financetracker.app.ui.screens.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ProfileScreen(viewModel: ProfileViewModel) {
    val profile by viewModel.profile.collectAsState()

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var currency by remember { mutableStateOf("\u20B9") }
    var budgetText by remember { mutableStateOf("") }
    var smsEnabled by remember { mutableStateOf(true) }
    var notifEnabled by remember { mutableStateOf(true) }

    LaunchedEffect(profile) {
        profile?.let {
            name = it.name
            email = it.email
            currency = it.currencySymbol
            budgetText = it.monthlyBudget?.toString() ?: ""
            smsEnabled = it.smsAutoDetectEnabled
            notifEnabled = it.notificationsEnabled
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Text(text = "Your Profile", style = MaterialTheme.typography.headlineMedium)

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Full name") },
            modifier = Modifier.fillMaxWidth().padding(top = 20.dp)
        )
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
        )
        OutlinedTextField(
            value = currency,
            onValueChange = { if (it.length <= 3) currency = it },
            label = { Text("Currency symbol") },
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
        )
        OutlinedTextField(
            value = budgetText,
            onValueChange = { budgetText = it.filter { c -> c.isDigit() || c == '.' } },
            label = { Text("Monthly budget (optional)") },
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = "Auto-detect from SMS", style = MaterialTheme.typography.bodyLarge)
                Text(
                    text = "Reads bank SMS to log transactions automatically",
                    style = MaterialTheme.typography.labelSmall,
                    color = androidx.compose.ui.graphics.Color.Gray
                )
            }
            Switch(checked = smsEnabled, onCheckedChange = { smsEnabled = it })
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = "Transaction notifications", style = MaterialTheme.typography.bodyLarge)
                Text(
                    text = "Popup after every payment/credit detected",
                    style = MaterialTheme.typography.labelSmall,
                    color = androidx.compose.ui.graphics.Color.Gray
                )
            }
            Switch(checked = notifEnabled, onCheckedChange = { notifEnabled = it })
        }

        Button(
            onClick = {
                viewModel.saveProfile(
                    name = name,
                    email = email,
                    currencySymbol = currency.ifBlank { "\u20B9" },
                    monthlyBudget = budgetText.toDoubleOrNull(),
                    smsAutoDetectEnabled = smsEnabled,
                    notificationsEnabled = notifEnabled
                )
            },
            modifier = Modifier.fillMaxWidth().padding(top = 32.dp)
        ) { Text("Save Profile") }
    }
}
