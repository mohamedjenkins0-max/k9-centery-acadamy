package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AnalyticsDao {
    @Query("SELECT * FROM analytics_events ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentEvents(limit: Int = 50): Flow<List<AnalyticsEventEntity>>

    @Insert
    suspend fun insert(event: AnalyticsEventEntity): Long

    @Query("DELETE FROM analytics_events")
    suspend fun clearAll()
}
