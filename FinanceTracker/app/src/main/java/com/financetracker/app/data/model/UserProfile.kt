package com.financetracker.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val id: Int = 0,   // singleton row
    val name: String = "",
    val email: String = "",
    val currencySymbol: String = "\u20B9", // ₹ default, editable
    val photoUri: String? = null,
    val monthlyBudget: Double? = null,
    val smsAutoDetectEnabled: Boolean = true,
    val notificationsEnabled: Boolean = true
)
