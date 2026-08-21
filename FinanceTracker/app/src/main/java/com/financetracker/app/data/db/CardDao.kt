package com.financetracker.app.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.financetracker.app.data.model.Card
import kotlinx.coroutines.flow.Flow

@Dao
interface CardDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(card: Card): Long

    @Update
    suspend fun update(card: Card)

    @Delete
    suspend fun delete(card: Card)

    @Query("SELECT * FROM cards WHERE isActive = 1 ORDER BY bankName")
    fun getAll(): Flow<List<Card>>

    @Query("SELECT * FROM cards WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): Card?

    @Query("SELECT * FROM cards WHERE lastFourDigits = :lastFour LIMIT 1")
    suspend fun findByLastFour(lastFour: String): Card?
}
