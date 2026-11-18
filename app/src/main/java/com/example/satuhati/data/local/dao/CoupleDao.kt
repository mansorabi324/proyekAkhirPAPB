package com.example.satuhati.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.satuhati.data.local.entity.CoupleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CoupleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCouple(couple: CoupleEntity): Long

    @Query("SELECT * FROM couples WHERE id = :coupleId LIMIT 1")
    fun getCoupleById(coupleId: String): Flow<CoupleEntity?>

    @Query("SELECT * FROM couples LIMIT 1")
    fun getFirstCouple(): Flow<CoupleEntity?>

    @Query("DELETE FROM couples")
    fun clearCouples(): Int
}
