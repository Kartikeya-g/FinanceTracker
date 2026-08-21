package com.financetracker.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.financetracker.app.data.model.Card
import com.financetracker.app.data.model.CardType

@Composable
fun CardVisual(
    card: Card,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val baseColor = try {
        Color(android.graphics.Color.parseColor(card.colorHex))
    } catch (e: Exception) {
        Color(0xFF4C6EF5)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1.6f)
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(baseColor, baseColor.copy(alpha = 0.7f))
                )
            )
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = card.bankName,
                color = Color.White,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = if (card.cardType == CardType.CREDIT) "CREDIT" else "DEBIT",
                color = Color.White.copy(alpha = 0.85f),
                style = MaterialTheme.typography.labelSmall
            )
        }

        Column(modifier = Modifier.padding(top = 24.dp)) {
            Text(
                text = "\u2022\u2022\u2022\u2022  \u2022\u2022\u2022\u2022  \u2022\u2022\u2022\u2022  ${card.lastFourDigits}",
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = card.nickname, color = Color.White.copy(alpha = 0.9f), style = MaterialTheme.typography.bodyMedium)
            Text(text = card.network.name, color = Color.White, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
        }
    }
}
