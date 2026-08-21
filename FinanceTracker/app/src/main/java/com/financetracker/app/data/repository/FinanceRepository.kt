package com.financetracker.app.data.repository

import com.financetracker.app.data.db.AppDatabase
import com.financetracker.app.data.model.Card
import com.financetracker.app.data.model.Transaction
import com.financetracker.app.data.model.TransactionType
import com.financetracker.app.data.model.UserProfile
import kotlinx.coroutines.flow.Flow

/**
 * Single source of truth for all data access. ViewModels talk to this,
 * never directly to the DAOs.
 */
class FinanceRepository(private val db: AppDatabase) {

    // ---- Transactions ----
    fun getAllTransactions(): Flow<List<Transaction>> = db.transactionDao().getAll()
    fun getRecentTransactions(limit: Int = 10): Flow<List<Transaction>> = db.transactionDao().getRecent(limit)
    fun getTransactionsForCard(cardId: String): Flow<List<Transaction>> = db.transactionDao().getByCard(cardId)
    fun searchTransactions(query: String): Flow<List<Transaction>> = db.transactionDao().search(query)
    fun getTotalExpenses(): Flow<Double> = db.transactionDao().getTotalByType(TransactionType.EXPENSE)
    fun getTotalCredits(): Flow<Double> = db.transactionDao().getTotalByType(TransactionType.CREDIT)

    suspend fun addTransaction(transaction: Transaction) = db.transactionDao().insert(transaction)
    suspend fun updateTransaction(transaction: Transaction) = db.transactionDao().update(transaction)
    suspend fun deleteTransaction(transaction: Transaction) = db.transactionDao().delete(transaction)
    suspend fun getTransactionById(id: String) = db.transactionDao().getById(id)

    // ---- Cards ----
    fun getAllCards(): Flow<List<Card>> = db.cardDao().getAll()
    suspend fun addCard(card: Card) = db.cardDao().insert(card)
    suspend fun updateCard(card: Card) = db.cardDao().update(card)
    suspend fun deleteCard(card: Card) = db.cardDao().delete(card)
    suspend fun getCardById(id: String) = db.cardDao().getById(id)
    suspend fun findCardByLastFour(lastFour: String) = db.cardDao().findByLastFour(lastFour)

    // ---- Profile ----
    fun observeProfile(): Flow<UserProfile?> = db.userProfileDao().observe()
    suspend fun getProfile(): UserProfile? = db.userProfileDao().get()
    suspend fun saveProfile(profile: UserProfile) = db.userProfileDao().upsert(profile)

    companion object {
        @Volatile private var INSTANCE: FinanceRepository? = null

        fun getInstance(db: AppDatabase): FinanceRepository =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: FinanceRepository(db).also { INSTANCE = it }
            }
    }
}
