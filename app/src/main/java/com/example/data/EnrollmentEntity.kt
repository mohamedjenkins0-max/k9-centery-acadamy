package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "enrollments")
data class EnrollmentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val ownerName: String,
    val email: String,
    val phone: String,
    val streetAddress: String,
    val dogName: String,
    val breed: String,
    val dogAgeMonths: Int,
    val courseTrack: String,
    val tuitionPlan: String,
    val amountPaid: Double,
    val paymentDate: Long = System.currentTimeMillis(),
    val cardBrand: String,
    val cardLast4: String,
    val status: String = "Active Enrolled"
)
