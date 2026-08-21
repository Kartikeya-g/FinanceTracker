package com.financetracker.app.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.UUID

enum class TransactionType { EXPENSE, CREDIT }

enum class TransactionSource { SMS_AUTO, NOTIFICATION_AUTO, MANUAL }

enum class TransactionCategory {
    FOOD, SHOPPING, TRAVEL, BILLS, ENTERTAINMENT, HEALTH,
    GROCERY, RENT, SALARY, TRANSFER, INVESTMENT, OTHER
}

@Entity(
    tableName = "transactions",
    foreignKeys = [
        ForeignKey(
            entity = Card::class,
            parentColumns = ["id"],
            childColumns = ["cardId"],
            onDelete = ForeignKey.SET_NULL
        )
    ]
)
data class Transaction(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val title: String,                 // editable "name of the expense"
    val merchant: String? = null,       // raw merchant/payee parsed from SMS
    val amount: Double,
    val type: TransactionType,
    val category: TransactionCategory = TransactionCategory.OTHER,
    val cardId: String? = null,         // which card/account this hit
    val timestamp: Long = System.currentTimeMillis(),
    val note: String? = null,
    val source: TransactionSource = TransactionSource.MANUAL,
    val rawSmsBody: String? = null,     // original message, kept for audit/re-parsing
    val balanceAfter: Double? = null    // account balance if the SMS reported it
)
