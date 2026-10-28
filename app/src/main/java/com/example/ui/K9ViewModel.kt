package com.example.ui

import android.app.Application
import android.util.Patterns
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ACADEMY_SECTORS
import com.example.data.AcademySector
import com.example.data.AnalyticsEventEntity
import com.example.data.DeviceTelemetry
import com.example.data.DeviceTelemetryCollector
import com.example.data.EnrollmentEntity
import com.example.data.K9Database
import com.example.data.LocationHelper
import com.example.data.SectorProximity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppTab(val title: String) {
    OVERVIEW("Overview"),
    ENROLLMENT("Enrollment"),
    RADAR("Radar"),
    BILLING("Billing"),
    DIAGNOSTICS("Diagnostics")
}

data class EnrollmentFormState(
    val ownerName: String = "",
    val email: String = "",
    val phone: String = "",
    val streetAddress: String = "",
    val dogName: String = "",
    val breed: String = "Belgian Malinois",
    val dogAgeMonths: Int = 12,
    val courseTrack: String = "Sentry Perimeter & Guard",
    val tuitionPlan: String = "Full Tuition ($2,850)",
    val cardholderName: String = "",
    val cardNumber: String = "",
    val expiryDate: String = "",
    val cvv: String = "",
    val billingZip: String = "",
    val isSubmitting: Boolean = false,
    val submissionSuccess: Boolean = false,
    val lastSubmittedCadet: EnrollmentEntity? = null,
    val errorMessage: String? = null
)

class K9ViewModel(application: Application) : AndroidViewModel(application) {
    private val database = K9Database.getDatabase(application, viewModelScope)
    private val enrollmentDao = database.enrollmentDao()
    private val analyticsDao = database.analyticsDao()
    private val locationHelper = LocationHelper(application)

    private val _currentTab = MutableStateFlow(AppTab.OVERVIEW)
    val currentTab: StateFlow<AppTab> = _currentTab.asStateFlow()

    val enrollments: StateFlow<List<EnrollmentEntity>> = enrollmentDao.getAllEnrollments()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentAnalytics: StateFlow<List<AnalyticsEventEntity>> = analyticsDao.getRecentEvents(50)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalRevenue: StateFlow<Double?> = enrollmentDao.getTotalRevenue()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val cadetCount: StateFlow<Int> = enrollmentDao.getCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Form State
    private val _formState = MutableStateFlow(EnrollmentFormState())
    val formState: StateFlow<EnrollmentFormState> = _formState.asStateFlow()

    // Location & Radar State
    private val _currentLatitude = MutableStateFlow(locationHelper.fallbackLatitude)
    val currentLatitude: StateFlow<Double> = _currentLatitude.asStateFlow()

    private val _currentLongitude = MutableStateFlow(locationHelper.fallbackLongitude)
    val currentLongitude: StateFlow<Double> = _currentLongitude.asStateFlow()

    private val _sectorProximities = MutableStateFlow<List<SectorProximity>>(emptyList())
    val sectorProximities: StateFlow<List<SectorProximity>> = _sectorProximities.asStateFlow()

    private val _selectedSector = MutableStateFlow<AcademySector?>(null)
    val selectedSector: StateFlow<AcademySector?> = _selectedSector.asStateFlow()

    private val _radarSweepAngle = MutableStateFlow(0f)
    val radarSweepAngle: StateFlow<Float> = _radarSweepAngle.asStateFlow()

    // Telemetry State
    private val _telemetry = MutableStateFlow(DeviceTelemetryCollector.collect(application))
    val telemetry: StateFlow<DeviceTelemetry> = _telemetry.asStateFlow()

    init {
        updateSectorProximities(_currentLatitude.value, _currentLongitude.value)
        logAnalyticsEvent("SYSTEM_START", "Handler dashboard loaded with motto: Inspect What You Expect.")
    }

    fun setTab(tab: AppTab) {
        _currentTab.value = tab
        logAnalyticsEvent("NAVIGATION", "Navigated to tab: ${tab.title}")
    }

    fun selectSector(sector: AcademySector?) {
        _selectedSector.value = sector
    }

    fun refreshTelemetry() {
        _telemetry.value = DeviceTelemetryCollector.collect(getApplication())
        logAnalyticsEvent("DIAGNOSTIC_QUERY", "Device telemetry refreshed by handler.")
    }

    fun refreshLocation() {
        locationHelper.fetchCurrentLocation(
            onLocationReceived = { lat, lon ->
                _currentLatitude.value = lat
                _currentLongitude.value = lon
                updateSectorProximities(lat, lon)
                logAnalyticsEvent(
                    "RADAR_GPS",
                    "GPS fix acquired: %.4f, %.4f. Recalculated distance to %d academy sectors."
                        .format(lat, lon, ACADEMY_SECTORS.size)
                )
            },
            onError = { err ->
                logAnalyticsEvent("RADAR_GPS", "Location acquisition alert: $err")
            }
        )
    }

    private fun updateSectorProximities(userLat: Double, userLon: Double) {
        val list = ACADEMY_SECTORS.map { sector ->
            val dist = LocationHelper.calculateDistanceMiles(userLat, userLon, sector.latitude, sector.longitude)
            val bearing = LocationHelper.calculateBearing(userLat, userLon, sector.latitude, sector.longitude)
            SectorProximity(sector, dist, bearing)
        }.sortedBy { it.distanceMiles }
        _sectorProximities.value = list
    }

    // Form Update Handlers
    fun updateOwnerName(value: String) { _formState.value = _formState.value.copy(ownerName = value, errorMessage = null) }
    fun updateEmail(value: String) { _formState.value = _formState.value.copy(email = value, errorMessage = null) }
    fun updatePhone(raw: String) {
        val digits = raw.filter { it.isDigit() }.take(10)
        val formatted = formatPhoneNumber(digits)
        _formState.value = _formState.value.copy(phone = formatted, errorMessage = null)
    }
    fun updateStreetAddress(value: String) { _formState.value = _formState.value.copy(streetAddress = value, errorMessage = null) }
    fun updateDogName(value: String) { _formState.value = _formState.value.copy(dogName = value, errorMessage = null) }
    fun updateBreed(value: String) { _formState.value = _formState.value.copy(breed = value) }
    fun updateDogAge(value: Int) { _formState.value = _formState.value.copy(dogAgeMonths = value.coerceIn(2, 120)) }
    fun updateCourseTrack(value: String) { _formState.value = _formState.value.copy(courseTrack = value) }
    fun updateTuitionPlan(value: String) { _formState.value = _formState.value.copy(tuitionPlan = value) }

    fun updateCardholder(value: String) { _formState.value = _formState.value.copy(cardholderName = value, errorMessage = null) }
    fun updateCardNumber(raw: String) {
        val digits = raw.filter { it.isDigit() }.take(16)
        val spaced = digits.chunked(4).joinToString(" ")
        _formState.value = _formState.value.copy(cardNumber = spaced, errorMessage = null)
    }
    fun updateExpiryDate(raw: String) {
        val digits = raw.filter { it.isDigit() }.take(4)
        val formatted = if (digits.length >= 2) "${digits.take(2)}/${digits.drop(2)}" else digits
        _formState.value = _formState.value.copy(expiryDate = formatted, errorMessage = null)
    }
    fun updateCvv(raw: String) {
        val digits = raw.filter { it.isDigit() }.take(4)
        _formState.value = _formState.value.copy(cvv = digits, errorMessage = null)
    }
    fun updateBillingZip(raw: String) {
        val digits = raw.filter { it.isDigit() }.take(5)
        _formState.value = _formState.value.copy(billingZip = digits, errorMessage = null)
    }

    fun detectCardBrand(digits: String): String {
        val clean = digits.filter { it.isDigit() }
        return when {
            clean.startsWith("4") -> "Visa"
            clean.startsWith("34") || clean.startsWith("37") -> "Amex"
            clean.startsWith("6011") || clean.startsWith("65") -> "Discover"
            clean.length >= 2 && clean.substring(0, 2).toIntOrNull() in 51..55 -> "Mastercard"
            clean.length >= 4 && clean.substring(0, 4).toIntOrNull() in 2221..2720 -> "Mastercard"
            else -> "Card"
        }
    }

    private fun formatPhoneNumber(digits: String): String {
        return when {
            digits.isEmpty() -> ""
            digits.length <= 3 -> "($digits"
            digits.length <= 6 -> "(${digits.substring(0, 3)}) ${digits.substring(3)}"
            else -> "(${digits.substring(0, 3)}) ${digits.substring(3, 6)}-${digits.substring(6)}"
        }
    }

    fun submitEnrollment() {
        val state = _formState.value
        val digitsPhone = state.phone.filter { it.isDigit() }
        val digitsCard = state.cardNumber.filter { it.isDigit() }

        // Validation
        if (state.ownerName.trim().length < 2) {
            _formState.value = state.copy(errorMessage = "Please enter the full client / handler name.")
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(state.email.trim()).matches()) {
            _formState.value = state.copy(errorMessage = "Please enter a valid email address for syllabus delivery.")
            return
        }
        if (digitsPhone.length < 10) {
            _formState.value = state.copy(errorMessage = "Please enter a complete 10-digit emergency phone number.")
            return
        }
        if (state.streetAddress.trim().length < 5) {
            _formState.value = state.copy(errorMessage = "Please specify a field service / residence address.")
            return
        }
        if (state.dogName.trim().isEmpty()) {
            _formState.value = state.copy(errorMessage = "Please enter the canine callsign / name.")
            return
        }
        if (state.cardholderName.trim().length < 2) {
            _formState.value = state.copy(errorMessage = "Please enter cardholder name as printed on card.")
            return
        }
        if (digitsCard.length < 15) {
            _formState.value = state.copy(errorMessage = "Please enter a valid 15 or 16-digit payment card number.")
            return
        }
        if (state.expiryDate.length < 5) {
            _formState.value = state.copy(errorMessage = "Please enter valid expiration date (MM/YY).")
            return
        }
        if (state.cvv.length < 3) {
            _formState.value = state.copy(errorMessage = "Please enter 3 or 4-digit security code (CVV/CVC).")
            return
        }

        val amount = when {
            state.tuitionPlan.contains("2,850") -> 2850.00
            state.tuitionPlan.contains("1,425") -> 1425.00
            else -> 475.00
        }
        val cardBrand = detectCardBrand(digitsCard)
        val last4 = if (digitsCard.length >= 4) digitsCard.takeLast(4) else "0000"

        val entity = EnrollmentEntity(
            ownerName = state.ownerName.trim(),
            email = state.email.trim(),
            phone = state.phone,
            streetAddress = state.streetAddress.trim(),
            dogName = state.dogName.trim(),
            breed = state.breed,
            dogAgeMonths = state.dogAgeMonths,
            courseTrack = state.courseTrack,
            tuitionPlan = state.tuitionPlan,
            amountPaid = amount,
            cardBrand = cardBrand,
            cardLast4 = last4,
            status = "Active Enrolled"
        )

        _formState.value = state.copy(isSubmitting = true, errorMessage = null)

        viewModelScope.launch(Dispatchers.IO) {
            val insertedId = enrollmentDao.insert(entity)
            val insertedRecord = entity.copy(id = insertedId)

            logAnalyticsEvent(
                "ENROLLMENT",
                "Enrolled Cadet ${entity.dogName} (${entity.breed}) with Handler ${entity.ownerName} in track: ${entity.courseTrack}."
            )
            logAnalyticsEvent(
                "BILLING_PAYMENT",
                "Processed payment of $%.2f via %s •••• %s for Cadet %s."
                    .format(amount, cardBrand, last4, entity.dogName)
            )

            _formState.value = EnrollmentFormState(
                submissionSuccess = true,
                lastSubmittedCadet = insertedRecord
            )
        }
    }

    fun dismissSubmissionSuccess() {
        _formState.value = _formState.value.copy(submissionSuccess = false, lastSubmittedCadet = null)
    }

    fun deleteEnrollment(enrollment: EnrollmentEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            enrollmentDao.delete(enrollment)
            logAnalyticsEvent(
                "ENROLLMENT",
                "Discharged / removed Cadet record: ${enrollment.dogName} (${enrollment.ownerName})"
            )
        }
    }

    fun clearAuditLogs() {
        viewModelScope.launch(Dispatchers.IO) {
            analyticsDao.clearAll()
            logAnalyticsEvent("DIAGNOSTIC_QUERY", "Handler cleared diagnostic audit logs.")
        }
    }

    private fun logAnalyticsEvent(type: String, description: String, metadata: String = "") {
        viewModelScope.launch(Dispatchers.IO) {
            analyticsDao.insert(
                AnalyticsEventEntity(
                    eventType = type,
                    description = description,
                    metadataJson = metadata
                )
            )
        }
    }
}
