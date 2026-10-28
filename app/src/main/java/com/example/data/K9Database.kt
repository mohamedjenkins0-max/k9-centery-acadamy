package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [EnrollmentEntity::class, AnalyticsEventEntity::class],
    version = 1,
    exportSchema = false
)
abstract class K9Database : RoomDatabase() {
    abstract fun enrollmentDao(): EnrollmentDao
    abstract fun analyticsDao(): AnalyticsDao

    companion object {
        @Volatile
        private var INSTANCE: K9Database? = null

        fun getDatabase(context: Context, scope: CoroutineScope): K9Database {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    K9Database::class.java,
                    "k9_century_academy.db"
                )
                .addCallback(K9DatabaseCallback(scope))
                .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class K9DatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateInitialData(database.enrollmentDao(), database.analyticsDao())
                }
            }
        }

        private suspend fun populateInitialData(
            enrollmentDao: EnrollmentDao,
            analyticsDao: AnalyticsDao
        ) {
            val now = System.currentTimeMillis()
            val day = 86400000L

            enrollmentDao.insert(
                EnrollmentEntity(
                    ownerName = "Sgt. Marcus Vance",
                    email = "m.vance@defenseguard.org",
                    phone = "(555) 234-8901",
                    streetAddress = "410 Bravo Ridge Blvd, Sector 2",
                    dogName = "Ares",
                    breed = "Belgian Malinois",
                    dogAgeMonths = 24,
                    courseTrack = "Executive Protection Handler",
                    tuitionPlan = "Full Tuition ($2,850)",
                    amountPaid = 2850.00,
                    paymentDate = now - (day * 3),
                    cardBrand = "Visa",
                    cardLast4 = "4242",
                    status = "Field Certified"
                )
            )

            enrollmentDao.insert(
                EnrollmentEntity(
                    ownerName = "Lt. Elena Rostova",
                    email = "elena.k9@centurytactical.com",
                    phone = "(555) 782-4119",
                    streetAddress = "88 Perimeter Way, Compound A",
                    dogName = "Valkyrie",
                    breed = "German Shepherd",
                    dogAgeMonths = 18,
                    courseTrack = "Sentry Perimeter & Guard",
                    tuitionPlan = "50% Milestone Deposit ($1,425)",
                    amountPaid = 1425.00,
                    paymentDate = now - (day * 6),
                    cardBrand = "Mastercard",
                    cardLast4 = "8812",
                    status = "Active Enrolled"
                )
            )

            enrollmentDao.insert(
                EnrollmentEntity(
                    ownerName = "Officer David Chen",
                    email = "dchen@metrocanine.gov",
                    phone = "(555) 349-9022",
                    streetAddress = "12 Harbor Sentry Post, Dock 4",
                    dogName = "Ghost",
                    breed = "Dutch Shepherd",
                    dogAgeMonths = 30,
                    courseTrack = "Scent & Detection Specialist",
                    tuitionPlan = "Monthly Handler Dues ($475/mo)",
                    amountPaid = 475.00,
                    paymentDate = now - (day * 1),
                    cardBrand = "Amex",
                    cardLast4 = "1004",
                    status = "Active Enrolled"
                )
            )

            enrollmentDao.insert(
                EnrollmentEntity(
                    ownerName = "Sarah Jenkins",
                    email = "sjenkins@sentryranch.io",
                    phone = "(555) 612-8874",
                    streetAddress = "704 Oak Meadow Trail, Highland",
                    dogName = "Titan",
                    breed = "Rottweiler",
                    dogAgeMonths = 8,
                    courseTrack = "Puppy Sentry Foundation",
                    tuitionPlan = "Full Tuition ($2,850)",
                    amountPaid = 2850.00,
                    paymentDate = now - (day * 10),
                    cardBrand = "Visa",
                    cardLast4 = "9133",
                    status = "Field Certified"
                )
            )

            analyticsDao.insert(
                AnalyticsEventEntity(
                    eventType = "SYSTEM_START",
                    description = "K9 Century Academy secure database initialized with pre-certified squadrons.",
                    timestamp = now - (day * 10),
                    metadataJson = """{"version":"1.0","sectors_active":4}"""
                )
            )

            analyticsDao.insert(
                AnalyticsEventEntity(
                    eventType = "BILLING_PAYMENT",
                    description = "Processed tuition payment for Cadet Ares ($2,850.00 via Visa •••• 4242)",
                    timestamp = now - (day * 3),
                    metadataJson = """{"cadet":"Ares","amount":2850.0,"plan":"Full"}"""
                )
            )

            analyticsDao.insert(
                AnalyticsEventEntity(
                    eventType = "ENROLLMENT",
                    description = "New Sentry Cadet enrolled: Valkyrie (German Shepherd, 18 mos).",
                    timestamp = now - (day * 6),
                    metadataJson = """{"handler":"Elena Rostova","track":"Perimeter"}"""
                )
            )
        }
    }
}
