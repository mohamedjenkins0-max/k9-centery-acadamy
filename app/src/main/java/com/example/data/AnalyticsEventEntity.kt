package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "analytics_events")
data class AnalyticsEventEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val eventType: String,
    val description: String,
    val timestamp: Long = System.currentTimeMillis(),
    val metadataJson: String = ""
)
