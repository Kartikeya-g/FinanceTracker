package com.financetracker.app.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.financetracker.app.data.model.Transaction
import com.financetracker.app.data.model.TransactionType
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaction: Transaction): Long

    @Update
    suspend fun update(transaction: Transaction)

    @Delete
    suspend fun delete(transaction: Transaction)

    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    fun getAll(): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): Transaction?

    @Query("SELECT * FROM transactions WHERE cardId = :cardId ORDER BY timestamp DESC")
    fun getByCard(cardId: String): Flow<List<Transaction>>

    @Query("SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE type = :type")
    fun getTotalByType(type: TransactionType): Flow<Double>

    @Query(
        "SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE type = :type " +
        "AND timestamp BETWEEN :start AND :end"
    )
    fun getTotalByTypeBetween(type: TransactionType, start: Long, end: Long): Flow<Double>

    @Query("SELECT * FROM transactions ORDER BY timestamp DESC LIMIT :limit")
    fun getRecent(limit: Int): Flow<List<Transaction>>

    @Query(
        "SELECT * FROM transactions WHERE title LIKE '%' || :query || '%' " +
        "OR merchant LIKE '%' || :query || '%' ORDER BY timestamp DESC"
    )
    fun search(query: String): Flow<List<Transaction>>
}
