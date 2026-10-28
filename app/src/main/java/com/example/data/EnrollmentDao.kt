package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface EnrollmentDao {
    @Query("SELECT * FROM enrollments ORDER BY paymentDate DESC")
    fun getAllEnrollments(): Flow<List<EnrollmentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(enrollment: EnrollmentEntity): Long

    @Query("SELECT * FROM enrollments WHERE id = :id")
    suspend fun getById(id: Long): EnrollmentEntity?

    @Delete
    suspend fun delete(enrollment: EnrollmentEntity)

    @Query("SELECT COUNT(*) FROM enrollments")
    fun getCount(): Flow<Int>

    @Query("SELECT SUM(amountPaid) FROM enrollments")
    fun getTotalRevenue(): Flow<Double?>
}
