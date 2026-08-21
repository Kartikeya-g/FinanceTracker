package com.financetracker.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.financetracker.app.ui.theme.Green
import com.financetracker.app.ui.theme.GreenLight
import com.financetracker.app.ui.theme.Red
import com.financetracker.app.ui.theme.RedLight

@Composable
fun StatCard(
    label: String,
    amount: String,
    isPositive: Boolean,
    modifier: Modifier = Modifier
) {
    val accent = if (isPositive) Green else Red
    val bg = if (isPositive) GreenLight else RedLight

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = bg)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            androidx.compose.foundation.layout.Row(
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = if (isPositive) Icons.Filled.ArrowDownward else Icons.Filled.ArrowUpward,
                    contentDescription = null,
                    tint = accent
                )
                Text(text = label, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            }
            Text(
                text = amount,
                style = MaterialTheme.typography.titleLarge,
                color = accent
            )
        }
    }
}
