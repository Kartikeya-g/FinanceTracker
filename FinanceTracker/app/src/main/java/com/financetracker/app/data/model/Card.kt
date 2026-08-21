package com.financetracker.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

enum class CardType { DEBIT, CREDIT }

enum class CardNetwork { VISA, MASTERCARD, RUPAY, AMEX, OTHER }

/**
 * We deliberately never store the full card number or CVV — only the
 * last 4 digits, which is enough to identify the card and match it
 * against parsed SMS text (banks always mask numbers the same way).
 */
@Entity(tableName = "cards")
data class Card(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val bankName: String,
    val nickname: String,              // user-friendly label, e.g. "HDFC Salary"
    val cardType: CardType,
    val network: CardNetwork = CardNetwork.OTHER,
    val lastFourDigits: String,
    val colorHex: String = "#4C6EF5",  // for the card UI
    val creditLimit: Double? = null,   // only relevant for CREDIT cards
    val isActive: Boolean = true
)
