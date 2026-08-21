package com.financetracker.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.financetracker.app.data.model.Transaction
import com.financetracker.app.data.model.TransactionType
import com.financetracker.app.ui.theme.Green
import com.financetracker.app.ui.theme.GreenLight
import com.financetracker.app.ui.theme.Red
import com.financetracker.app.ui.theme.RedLight
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TransactionRow(
    transaction: Transaction,
    currencySymbol: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isExpense = transaction.type == TransactionType.EXPENSE
    val accent = if (isExpense) Red else Green
    val bg = if (isExpense) RedLight else GreenLight
    val dateFormat = remember(transaction.timestamp) {
        SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 10.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(bg, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isExpense) Icons.Filled.ArrowUpward else Icons.Filled.ArrowDownward,
                    contentDescription = null,
                    tint = accent
                )
            }
            Column(modifier = Modifier.padding(start = 12.dp)) {
                Text(text = transaction.title, style = MaterialTheme.typography.bodyLarge)
                Text(
                    text = "${transaction.category.name.lowercase().replaceFirstChar { it.uppercase() }} \u2022 ${dateFormat.format(Date(transaction.timestamp))}",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
            }
        }
        Text(
            text = "${if (isExpense) "-" else "+"}$currencySymbol${"%.2f".format(transaction.amount)}",
            style = MaterialTheme.typography.bodyLarge,
            color = accent
        )
    }
}
