package com.financetracker.app.data.db

import androidx.room.TypeConverter
import com.financetracker.app.data.model.CardNetwork
import com.financetracker.app.data.model.CardType
import com.financetracker.app.data.model.TransactionCategory
import com.financetracker.app.data.model.TransactionSource
import com.financetracker.app.data.model.TransactionType

class Converters {
    @TypeConverter
    fun fromTransactionType(value: TransactionType): String = value.name
    @TypeConverter
    fun toTransactionType(value: String): TransactionType = TransactionType.valueOf(value)

    @TypeConverter
    fun fromCategory(value: TransactionCategory): String = value.name
    @TypeConverter
    fun toCategory(value: String): TransactionCategory = TransactionCategory.valueOf(value)

    @TypeConverter
    fun fromSource(value: TransactionSource): String = value.name
    @TypeConverter
    fun toSource(value: String): TransactionSource = TransactionSource.valueOf(value)

    @TypeConverter
    fun fromCardType(value: CardType): String = value.name
    @TypeConverter
    fun toCardType(value: String): CardType = CardType.valueOf(value)

    @TypeConverter
    fun fromCardNetwork(value: CardNetwork): String = value.name
    @TypeConverter
    fun toCardNetwork(value: String): CardNetwork = CardNetwork.valueOf(value)
}
